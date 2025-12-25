package com.example.real_madrid_museo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.comun.aplicarIdioma
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme
import com.example.real_madrid_museo.ui.onboarding.OnboardingSlide
import com.example.real_madrid_museo.ui.onboarding.OnboardingScreen

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        aplicarIdioma(this)

        setContent {
            Real_madrid_museoTheme {
                // Definimos las diapositivas
                val slides = listOf(
                    OnboardingSlide(
                        title = R.string.onboarding_title_1,
                        description = R.string.onboarding_desc_1,
                        imageRes = R.drawable.onboarding_1
                    ),
                    OnboardingSlide(
                        title = R.string.onboarding_title_2,
                        description = R.string.onboarding_desc_2,
                        imageRes = R.drawable.onboarding_2
                    ),
                    OnboardingSlide(
                        title = R.string.onboarding_title_3,
                        description = R.string.onboarding_desc_3,
                        imageRes = R.drawable.onboarding_3
                    )
                )

                // Mostramos la pantalla y definimos qué pasa al terminar
                OnboardingScreen(
                    slides = slides,
                    onFinish = {
                        // Al terminar, vamos al LOGIN y cerramos esta pantalla
                        startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}