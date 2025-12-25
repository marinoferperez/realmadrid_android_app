package com.example.real_madrid_museo.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun FondoAnimado() {

    val transition = rememberInfiniteTransition(label = "bg")

    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000, // MUY lento
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.White,
            Color.White,
            Color(0xFF5B73AF).copy(alpha = 0.08f), // azul MUY sutil
            Color(0xFFFFF2BF).copy(alpha = 0.15f)  // dorado suave
        ),
        start = androidx.compose.ui.geometry.Offset(0f, offset),
        end = androidx.compose.ui.geometry.Offset(offset, 0f)
    )

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
fun FondoAnimadoKahoot() {
    val transition = rememberInfiniteTransition(label = "bg")
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "progress"
    )

    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFD700).copy(alpha = 0.12f), // Oro Real Madrid
            Color(0xFFFEFEFE),                    // Blanco Puro
            Color(0xFF0033A0).copy(alpha = 0.05f)  // Azul Real Madrid muy sutil
        ),
        center = androidx.compose.ui.geometry.Offset(
            x = 200f + (animProgress * 500f),
            y = 300f + (animProgress * 800f)
        ),
        radius = 1200f
    )

    Box(modifier = Modifier.fillMaxSize().background(brush))
}