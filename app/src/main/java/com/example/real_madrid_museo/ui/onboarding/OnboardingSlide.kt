package com.example.real_madrid_museo.ui.onboarding
import androidx.annotation.DrawableRes

data class OnboardingSlide(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

