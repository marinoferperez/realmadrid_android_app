package com.example.real_madrid_museo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

// onboarding slide
import com.example.real_madrid_museo.ui.onboarding.screen
import com.example.real_madrid_museo.ui.onboarding.slide
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Real_madrid_museoTheme {

                // 1. onboarding
                val slides = listOf(
                    slide(
                        title = "¡Bienvenido al Museo!",
                        description = "Descubre la historia y los momentos más legendarios del Real Madrid.",
                        imageRes = R.drawable.onboarding_1
                    ),
                    slide(
                        title = "Escanea y descubre",
                        description = "Escanea códigos QR del museo para acceder a contenido exclusivo.",
                        imageRes = R.drawable.onboarding_2
                    ),
                    slide(
                        title = "Vive el club",
                        description = "Participa en juegos, consulta eventos y explora el pasado, presente y futuro del club.",
                        imageRes = R.drawable.onboarding_3
                    )
                )

                screen(
                    slides = slides,
                    onFinish = {
                        // Aquí irá el login más adelante
                    }
                )
            }
        }
    }
}