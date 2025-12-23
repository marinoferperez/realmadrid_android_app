package com.example.real_madrid_museo.ui.onboarding
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingSlide(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val imageRes: Int
)

