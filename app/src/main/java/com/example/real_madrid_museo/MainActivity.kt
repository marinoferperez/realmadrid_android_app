package com.example.real_madrid_museo

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigationevent.NavigationEventInfo
import com.example.real_madrid_museo.ui.LoginActivity
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

// onboarding
import com.example.real_madrid_museo.ui.onboarding.OnboardingSlide
import com.example.real_madrid_museo.ui.onboarding.OnboardingScreen

// cambiar idioma
import com.example.real_madrid_museo.ui.comun.idiomas.aplicarIdioma

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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


//
//
//import androidx.activity.compose.setContent
//import androidx.compose.runtime.*
//import com.example.real_madrid_museo.kahoot.KahootJoinScreen
//import com.example.real_madrid_museo.kahoot.KahootPregunta
//import com.example.real_madrid_museo.kahoot.pantallaRespuestas
//import com.example.real_madrid_museo.kahoot.sensores.vibracionCorrecta
//import com.example.real_madrid_museo.kahoot.sensores.vibracionIncorrecta
//import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme
//import android.Manifest
//import android.content.pm.PackageManager
//import android.view.WindowInsets
//import android.view.WindowInsetsController
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.view.WindowCompat
//import com.example.real_madrid_museo.kahoot.KahootFlow
//import android.os.Build
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        solicitarPermisos()
//
//        setContent {
//            Real_madrid_museoTheme {
//                KahootFlow()
//            }
//        }
//    }
//
//    private fun solicitarPermisos() {
//        val permisos = mutableListOf(Manifest.permission.RECORD_AUDIO)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
//        }
//
//        val permisosNoConcedidos = permisos.filter {
//            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (permisosNoConcedidos.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                this,
//                permisosNoConcedidos.toTypedArray(),
//                1001
//            )
//        }
//    }
//}
