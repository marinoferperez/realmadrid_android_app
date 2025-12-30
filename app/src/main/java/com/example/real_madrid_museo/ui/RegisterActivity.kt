package com.example.real_madrid_museo.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var birthYear = 0
    private var birthMonth = 0
    private var birthDay = 0
    private var dateSelected = false
    
    private val CHANNEL_ID = "ofertas_real_madrid"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, getString(R.string.toast_no_permission_notifications), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)
        crearCanalNotificacion()
        pedirPermisoNotificaciones()

        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val ccp = findViewById<CountryCodePicker>(R.id.ccp)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailReg)
        val etPass = findViewById<TextInputEditText>(R.id.etPassReg)
        
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val cbNotifications = findViewById<CheckBox>(R.id.cbNotifications)
        
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegisterAction)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        ccp.registerCarrierNumberEditText(etPhone)

        // Al marcar la casilla, quitamos el error automáticamente
        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTerms.error = null
            }
        }

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, R.style.CalendarioMorado, { _, year, month, day ->
                birthYear = year
                birthMonth = month
                birthDay = day
                dateSelected = true
                etDate.setText("$day/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()
            val name = etName.text.toString().trim()

            // 1. Validar campos obligatorios generales
            if (name.isEmpty() || etPhone.text.isNullOrEmpty() || 
                email.isEmpty() || password.isEmpty() || !dateSelected) {
                Toast.makeText(this, getString(R.string.toast_fill_all_fields_register), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. DETECTOR DE TÉRMINOS Y CONDICIONES
            if (!cbTerms.isChecked) {
                cbTerms.error = "Debes aceptar los términos para continuar"
                cbTerms.requestFocus() // Lleva el foco a la casilla para que sea obvio
                Toast.makeText(this, "Por favor, acepta los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                cbTerms.error = null
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = getString(R.string.error_invalid_email)
                return@setOnClickListener
            }
            
            if (databaseHelper.checkEmailExists(email)) {
                Toast.makeText(this, getString(R.string.toast_email_exists), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthYear
            if (today.get(Calendar.MONTH) < birthMonth || 
               (today.get(Calendar.MONTH) == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {
                age--
            }
            val perfil = if (age < 14) "NIÑO" else "ADULTO"

            val success = databaseHelper.addUser(email, password, name, perfil)

            if (success) {
                if (cbNotifications.isChecked) {
                    lanzarNotificacionDescuento()
                }
                Toast.makeText(this, getString(R.string.toast_account_created), Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.toast_user_save_error), Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener { finish() }
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun lanzarNotificacionDescuento() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_rm) 
            .setContentTitle(getString(R.string.notification_welcome_title))
            .setContentText(getString(R.string.notification_discount_text_short))
            .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_discount_text_long)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@RegisterActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(101, builder.build())
            }
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}