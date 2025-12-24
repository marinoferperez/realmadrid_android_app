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

    // 1. DEFINIMOS EL LANZADOR PARA PEDIR PERMISO (NUEVO)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido. No hacemos nada ahora, pero ya podremos enviar la notificación al final.
        } else {
            // Permiso denegado.
            Toast.makeText(this, "Sin permiso, no recibirás el descuento", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)
        crearCanalNotificacion()

        // 2. PEDIR PERMISO NADA MÁS ENTRAR (Para Android 13+)
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

            if (name.isEmpty() || etPhone.text.isNullOrEmpty() || 
                email.isEmpty() || password.isEmpty() || !dateSelected) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email inválido"
                return@setOnClickListener
            }

            if (!cbTerms.isChecked) {
                Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (databaseHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_LONG).show()
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
                // LÓGICA DEL PREMIO
                if (cbNotifications.isChecked) {
                    lanzarNotificacionDescuento()
                }

                Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener { finish() }
    }

    private fun pedirPermisoNotificaciones() {
        // Solo necesario en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no tenemos permiso, lo pedimos
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun lanzarNotificacionDescuento() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("¡Bienvenido al Real Madrid!")
            .setContentText("Tienes un 10% de descuento en tu entrada.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Gracias por suscribirte. Tienes un 10% de descuento en tu entrada al museo y en la tienda oficial."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // Comprobación final de seguridad antes de emitir
            if (ActivityCompat.checkSelfPermission(
                    this@RegisterActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(101, builder.build())
            }
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ofertas y Promociones"
            val descriptionText = "Descuentos del museo"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}