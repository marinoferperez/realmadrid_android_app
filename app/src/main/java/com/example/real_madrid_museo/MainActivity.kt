package com.example.real_madrid_museo

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.ui.LoginActivity
import com.example.real_madrid_museo.ui.comun.aplicarIdioma
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

// onboarding
import com.example.real_madrid_museo.ui.onboarding.OnboardingSlide
import com.example.real_madrid_museo.ui.onboarding.OnboardingScreen

// cambiar idioma
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        aplicarIdioma(this)

        setContent {
            Real_madrid_museoTheme {

                // 1. onboarding
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

                OnboardingScreen(
                    slides = slides,
                    onFinish = {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
