package com.example.real_madrid_museo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.home.AppActivity
import com.example.real_madrid_museo.ui.comun.LanguageToggle
import com.example.real_madrid_museo.ui.comun.aplicarIdioma
import com.example.real_madrid_museo.ui.comun.cambiarIdioma
import com.example.real_madrid_museo.ui.comun.obtenerIdioma
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        aplicarIdioma(this)

        databaseHelper = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val composeBandera = findViewById<ComposeView>(R.id.composeBandera)
        
        // Elementos adicionales para traducir (títulos, hints)
        val titleWelcome = findViewById<TextView>(R.id.titleWelcome)
        val inputLayoutEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val inputLayoutPassword = findViewById<TextInputLayout>(R.id.inputLayoutPassword)

        // Configurar textos dinámicos desde resources (para que se aplique la traducción)
        titleWelcome.text = getString(R.string.login_title)
        inputLayoutEmail.hint = getString(R.string.login_email_hint)
        inputLayoutPassword.hint = getString(R.string.login_password_hint)
        btnLogin.text = getString(R.string.login_button_enter)
        btnGuest.text = getString(R.string.login_button_guest)
        tvRegister.text = getString(R.string.login_register_prompt)

        // Configurar el ComposeView para la bandera
        composeBandera.setContent {
            val currentLanguage = obtenerIdioma(this)
            LanguageToggle(
                currentLanguage = currentLanguage,
                onToggle = {
                    val newLanguage = if (currentLanguage == "es") "en" else "es"
                    cambiarIdioma(this, newLanguage)
                }
            )
        }

        // LOGIN
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_fill_fields), Toast.LENGTH_SHORT).show()
            } else {
                btnLogin.text = getString(R.string.login_button_verifying)
                btnLogin.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    val existe = databaseHelper.checkUser(email, password)
                    if (existe) {
                        Toast.makeText(this, getString(R.string.toast_welcome), Toast.LENGTH_SHORT).show()
                        irAlHome("USUARIO")
                    } else {
                        Toast.makeText(this, getString(R.string.toast_wrong_data), Toast.LENGTH_LONG).show()
                        btnLogin.text = getString(R.string.login_button_enter)
                        btnLogin.isEnabled = true
                    }
                }, 1000)
            }
        }

        // INVITADO
        btnGuest.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_guest_entry), Toast.LENGTH_SHORT).show()
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
            // Usamos getString con format args para el mensaje de error
            val errorMsg = getString(R.string.toast_home_error, e.message)
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}
