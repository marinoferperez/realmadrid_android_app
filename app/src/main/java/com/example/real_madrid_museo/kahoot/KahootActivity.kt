package com.example.real_madrid_museo.kahoot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.real_madrid_museo.ui.comun.idiomas.aplicarIdioma
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

// actividad principal que inicia el flujo del juego kahoot
class KahootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // aplicamos el idioma actual al iniciar la actividad
        aplicarIdioma(this)

        setContent {
            Real_madrid_museoTheme {
                // esta actividad solo activa el flujo del juego kahoot
                KahootFlow()
            }
        }
    }
}
