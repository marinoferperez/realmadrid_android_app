package com.example.real_madrid_museo.ui

import android.content.Context
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
import com.example.real_madrid_museo.ui.comun.idiomas.LanguageToggle
import com.example.real_madrid_museo.ui.comun.idiomas.aplicarIdioma
import com.example.real_madrid_museo.ui.comun.idiomas.cambiarIdioma
import com.example.real_madrid_museo.ui.comun.idiomas.obtenerIdioma
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        aplicarIdioma(this) // Aplicar antes de setContentView
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        databaseHelper = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGuest = findViewById<MaterialButton>(R.id.btnGuest)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val composeBandera = findViewById<ComposeView>(R.id.composeBandera)
        
        val titleWelcome = findViewById<TextView>(R.id.titleWelcome)
        val inputLayoutEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val inputLayoutPassword = findViewById<TextInputLayout>(R.id.inputLayoutPassword)

        // Actualizar textos manualmente por si acaso el XML no los pilla al vuelo tras recrear
        titleWelcome.text = getString(R.string.login_title)
        inputLayoutEmail.hint = getString(R.string.login_email_hint)
        inputLayoutPassword.hint = getString(R.string.login_password_hint)
        btnLogin.text = getString(R.string.login_button_enter)
        btnGuest.text = getString(R.string.login_button_guest)
        tvRegister.text = getString(R.string.login_register_prompt)

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
                        
                        databaseHelper.incrementVisits(email)

                        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("current_email", email)
                            apply()
                        }

                        irAlHome("USUARIO", email)
                    } else {
                        Toast.makeText(this, getString(R.string.toast_wrong_data), Toast.LENGTH_LONG).show()
                        btnLogin.text = getString(R.string.login_button_enter)
                        btnLogin.isEnabled = true
                    }
                }, 1000)
            }
        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_guest_entry), Toast.LENGTH_SHORT).show()
            
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }

            irAlHome("INVITADO", null)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun irAlHome(tipoUsuario: String, email: String?) {
        try {
            val intent = Intent(this, AppActivity::class.java)
            intent.putExtra("TIPO_USUARIO", tipoUsuario)
            if (email != null) {
                intent.putExtra("USER_EMAIL", email)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = getString(R.string.toast_home_error, e.message)
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}
