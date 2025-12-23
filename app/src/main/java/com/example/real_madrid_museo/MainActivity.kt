package com.example.real_madrid_museo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

// onboarding
import com.example.real_madrid_museo.ui.onboarding.OnboardingSlide
import com.example.real_madrid_museo.ui.onboarding.OnboardingScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Real_madrid_museoTheme {

                // 1. onboarding
                val slides = listOf(
                    OnboardingSlide(
                        title = "¡Bienvenido al Museo!",
                        description = "Descubre la historia y los momentos más legendarios del Real Madrid.",
                        imageRes = R.drawable.onboarding_1
                    ),
                    OnboardingSlide(
                        title = "Escanea y descubre",
                        description = "Escanea códigos QR del museo para acceder a contenido exclusivo.",
                        imageRes = R.drawable.onboarding_2
                    ),
                    OnboardingSlide(
                        title = "Vive el club",
                        description = "Participa en juegos, consulta eventos y explora el pasado, presente y futuro del club.",
                        imageRes = R.drawable.onboarding_3
                    )
                )

                OnboardingScreen(
                    slides = slides,
                    onFinish = {
                        // Aquí irá el login más adelante
                    }
                )
            }
        }
    }
}