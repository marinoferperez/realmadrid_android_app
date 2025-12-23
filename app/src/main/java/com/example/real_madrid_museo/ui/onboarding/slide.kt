package com.example.real_madrid_museo.ui.onboarding
import androidx.annotation.DrawableRes

data class slide(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

