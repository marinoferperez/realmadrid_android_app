package com.example.real_madrid_museo.kahoot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.real_madrid_museo.ui.comun.aplicarIdioma
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

class KahootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicamos el idioma actual al iniciar la actividad
        aplicarIdioma(this)

        setContent {
            Real_madrid_museoTheme {
                // Esta actividad solo muestra el flujo del juego Kahoot
                KahootFlow()
            }
        }
    }
}
