package com.example.real_madrid_museo.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.home.MainScreen
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Real_madrid_museoTheme {
                MainScreen()
            }
        }
    }
}