package com.example.real_madrid_museo.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.real_madrid_museo.ui.RegisterActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        crearCanalNotificacion()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            if (etEmail.text.isNullOrEmpty() || etPassword.text.isNullOrEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                btnLogin.text = "Conectando..."
                btnLogin.isEnabled = false
                
                Handler(Looper.getMainLooper()).postDelayed({
                    btnLogin.text = "ENTRAR"
                    btnLogin.isEnabled = true
                    lanzarNotificacion()
                    Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show()
                    // Aquí iríamos al MainActivity:
                    // startActivity(Intent(this, MainActivity::class.java)) 
                }, 1500)
            }
        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, "Modo Invitado Activo", Toast.LENGTH_SHORT).show()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun lanzarNotificacion() {
        val builder = NotificationCompat.Builder(this, "ofertas_real_madrid")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Bienvenido de nuevo!")
            .setContentText("Tienes un 10% de descuento en la tienda.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(100, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("ofertas_real_madrid", "Ofertas", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}