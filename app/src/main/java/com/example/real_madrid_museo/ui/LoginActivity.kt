package com.example.real_madrid_museo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.home.AppActivity // <--- AHORA APUNTA A TU HOME
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        databaseHelper = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // LOGIN
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                btnLogin.text = "Verificando..."
                btnLogin.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    val existe = databaseHelper.checkUser(email, password)
                    if (existe) {
                        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        irAlHome("USUARIO")
                    } else {
                        Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show()
                        btnLogin.text = "ENTRAR"
                        btnLogin.isEnabled = true
                    }
                }, 1000)
            }
        }

        // INVITADO
        btnGuest.setOnClickListener {
            Toast.makeText(this, "Entrando como Invitado...", Toast.LENGTH_SHORT).show()
            irAlHome("INVITADO")
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun irAlHome(tipoUsuario: String) {
        try {
            // AQUÍ ESTÁ EL CAMBIO: Vamos a AppActivity (que contiene tu MainScreen)
            val intent = Intent(this, AppActivity::class.java)
            intent.putExtra("TIPO_USUARIO", tipoUsuario)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir Home: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}