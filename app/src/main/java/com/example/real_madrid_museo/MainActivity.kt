package com.example.real_madrid_museo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.real_madrid_museo.ui.comun.aplicarIdioma

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aplicarIdioma(this)

        val tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: "INVITADO"
        val tvUserType = findViewById<TextView>(R.id.tvUserType)

        if (tipoUsuario == "INVITADO") {
            tvUserType.text = "Modo: Visitante"
        } else {
            tvUserType.text = "Modo: Socio Madridista"
        }
    }
}