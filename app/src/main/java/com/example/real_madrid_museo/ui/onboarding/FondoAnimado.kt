package com.example.real_madrid_museo.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
                durationMillis = 15000, // MUY lento
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
            Color(0xFFFFF3C4).copy(alpha = 0.15f)  // dorado suave
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