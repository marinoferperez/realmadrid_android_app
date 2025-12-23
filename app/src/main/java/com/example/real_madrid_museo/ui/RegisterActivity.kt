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
import com.hbb20.CountryCodePicker // Importamos la librería de banderas
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
        val ccp = findViewById<CountryCodePicker>(R.id.ccp) // Referencia a las banderas
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailReg)
        val etPass = findViewById<TextInputEditText>(R.id.etPassReg)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegisterAction)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // Vinculamos el selector de banderas con el campo de texto para que se formateen juntos
        ccp.registerCarrierNumberEditText(etPhone)

        // LÓGICA DEL CALENDARIO MORADO
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            
            // Aquí usamos R.style.CalendarioMorado para cambiar el color
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.CalendarioMorado, // <--- AQUÍ APLICAMOS TU ESTILO MORADO
                { _, year, month, day ->
                    birthYear = year
                    birthMonth = month
                    birthDay = day
                    dateSelected = true
                    etDate.setText("$day/${month + 1}/$year")
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            // Esto permite que al tocar el AÑO en el título del calendario,
            // salga una lista de años para elegir rápido (funciona nativo en Android)
            datePickerDialog.show()
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

            // Validar si el número es válido para ese país (Opcional pero recomendado)
            if (!ccp.isValidFullNumber) {
                etPhone.error = "Número no válido"
                return@setOnClickListener
            }

            // Obtenemos el número completo con el prefijo (ej: +34666555444)
            val fullPhoneNumber = ccp.fullNumberWithPlus 

            // Cálculo de edad
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - birthYear
            if (today.get(Calendar.MONTH) < birthMonth || 
               (today.get(Calendar.MONTH) == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {
                age--
            }
            
            val perfil = if (age < 14) "NIÑO" else "ADULTO"
            
            Toast.makeText(this, "Registro OK. Tel: $fullPhoneNumber. Perfil: $perfil", Toast.LENGTH_LONG).show()
            finish()
        }

        tvGoToLogin.setOnClickListener { finish() }
    }
}