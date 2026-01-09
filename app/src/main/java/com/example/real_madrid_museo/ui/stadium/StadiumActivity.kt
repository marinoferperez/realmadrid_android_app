package com.example.real_madrid_museo.ui.stadium

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity // <--- CAMBIO CLAVE
import com.example.real_madrid_museo.ui.comun.idiomas.aplicarIdioma

class StadiumActivity : AppCompatActivity() { // <--- Hereda de AppCompatActivity, igual que tu Login

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Aplicamos idioma ANTES de que se cree la actividad
        aplicarIdioma(this)

        super.onCreate(savedInstanceState)

        setContent {
            // Al ser AppCompatActivity, el contexto aquí ya tiene el parche del idioma aplicado
            StadiumExplorerScreen()
        }
    }
}