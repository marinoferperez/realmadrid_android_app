package com.example.real_madrid_museo.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri

fun abrirMapaBernabeu(context: Context) {
    val lat = 40.453053
    val lon = -3.688344

    val uri = Uri.parse(
        "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=18/$lat/$lon"
    )

    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}