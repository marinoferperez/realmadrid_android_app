package com.example.real_madrid_museo.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private var birthYear = 0
    private var birthMonth = 0
    private var birthDay = 0
    private var dateSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailReg)
        val etPass = findViewById<TextInputEditText>(R.id.etPassReg)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegisterAction)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                birthYear = year
                birthMonth = month
                birthDay = day
                dateSelected = true
                etDate.setText("$day/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnRegister.setOnClickListener {
            if (etName.text.isNullOrEmpty() || etPhone.text.isNullOrEmpty() || 
                etEmail.text.isNullOrEmpty() || etPass.text.isNullOrEmpty() || !dateSelected) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cbTerms.isChecked) {
                Toast.makeText(this, "Acepta los términos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculamos edad
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthYear
            if (today.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().apply { set(today.get(Calendar.YEAR), birthMonth, birthDay) }.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            
            val perfil = if (age < 14) "NIÑO" else "ADULTO"
            Toast.makeText(this, "Registrado como: $perfil", Toast.LENGTH_LONG).show()
            finish()
        }

        tvGoToLogin.setOnClickListener { finish() }
    }
}