package com.example.real_madrid_museo.ui

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper // Nuestra base de datos

    private var birthYear = 0
    private var birthMonth = 0
    private var birthDay = 0
    private var dateSelected = false
    private val CHANNEL_ID = "ofertas_real_madrid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this) // Inicializamos la BD
        crearCanalNotificacion()

        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val ccp = findViewById<CountryCodePicker>(R.id.ccp)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailReg)
        val etPass = findViewById<TextInputEditText>(R.id.etPassReg)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
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

            // 1. Validaciones básicas
            if (name.isEmpty() || etPhone.text.isNullOrEmpty() ||
                email.isEmpty() || password.isEmpty() || !dateSelected) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. VALIDACIÓN DE EMAIL (Debe tener @ y .)
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email inválido (ej: hola@correo.com)"
                return@setOnClickListener
            }

            if (!cbTerms.isChecked) {
                Toast.makeText(this, "Acepta los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Comprobar si ya existe el usuario
            if (databaseHelper.checkEmailExists(email)) {
                Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 4. Calcular perfil
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthYear
            if (today.get(Calendar.MONTH) < birthMonth ||
                (today.get(Calendar.MONTH) == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {
                age--
            }
            val perfil = if (age < 14) "NIÑO" else "ADULTO"

            // 5. GUARDAR EN BASE DE DATOS
            val success = databaseHelper.addUser(email, password, name, perfil)

            if (success) {
                lanzarNotificacionDescuento()
                Toast.makeText(this, "¡Cuenta creada! Inicia sesión ahora.", Toast.LENGTH_LONG).show()
                finish() // Vuelve al login
            } else {
                Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener { finish() }
    }

    private fun lanzarNotificacionDescuento() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Usa tu icono R.drawable.logo_rm si puedes
            .setContentTitle("¡Bienvenido al Club!")
            .setContentText("Tienes un 10% de descuento para tu próxima visita.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(200, builder.build())
            }
        } catch (e: SecurityException) {
            // Permisos Android 13+
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Descuentos", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}