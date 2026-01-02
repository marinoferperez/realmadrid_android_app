package com.example.real_madrid_museo.ui.stadium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class StadiumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StadiumExplorerScreen()
        }
    }
}
