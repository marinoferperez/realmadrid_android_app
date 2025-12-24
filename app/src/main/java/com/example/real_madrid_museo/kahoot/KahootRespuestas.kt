package com.example.real_madrid_museo.kahoot

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

import com.example.real_madrid_museo.kahoot.sensores.*
import com.example.real_madrid_museo.ui.onboarding.FondoAnimado
import com.example.real_madrid_museo.ui.onboarding.FondoAnimadoKahoot
import com.example.real_madrid_museo.ui.theme.RealMadridBlue
import com.example.real_madrid_museo.ui.theme.RealMadridGold

@Composable
fun pantallaRespuestas(
    question: KahootPregunta,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswered: (Boolean, Int) -> Unit // Modificado: Devuelve (Acierto, ÍndiceSeleccionado)
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FondoAnimadoKahoot()

        // Barra de progreso fija en la parte inferior (bottom)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            val progressTarget = if (totalQuestions > 0) ((currentQuestionIndex + 1).toFloat() / totalQuestions) else 0f
            val animatedProgress by animateFloatAsState(
                targetValue = progressTarget,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "progressAnim"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(RealMadridBlue.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(RealMadridGold, Color(0xFFFFD700))
                            )
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth + 100 },
                        animationSpec = tween(1200)
                    ) + fadeIn(animationSpec = tween(1200)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth - 100 },
                        animationSpec = tween(1200)
                    ) + fadeOut(animationSpec = tween(1200))
                },
                label = "transicionRespuestas"
            ) { currentQuestion ->
                ContenidoPreguntaUnica(
                    question = currentQuestion,
                    onAnswered = onAnswered
                )
            }
        }
    }
}

@Composable
fun ContenidoPreguntaUnica(
    question: KahootPregunta,
    onAnswered: (Boolean, Int) -> Unit
) {
    val context = LocalContext.current

    // Estados de la pregunta
    var answered by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var timeLeft by remember { mutableStateOf(question.tiempoLimite) }

    /* ─────────────── 🔊 TTS ─────────────── */
    val lectorTTS = remember { LectorPreguntaTTS(context) }
    LaunchedEffect(Unit) {
        lectorTTS.leer(question.pregunta)
    }

    /* ─────────────── ⏱ TIMER LOGIC ─────────────── */
    LaunchedEffect(Unit) {
        while (timeLeft > 0 && !answered) {
            delay(1000)
            timeLeft--
            if (timeLeft in 1..3 && !answered) {
                vibracionCorrecta(context)
            }
        }
        if (!answered && timeLeft == 0) {
            answered = true
            vibracionIncorrecta(context)
            // Timeout: enviamos -1 como índice seleccionado
            onAnswered(false, -1)
        }
    }

    /* ─────────────── 📳 ACELERÓMETRO ─────────────── */
    val shakeDetector = remember {
        DetectorAgitarAcelerometro(context) {
            if (!answered && selectedIndex != null) {
                answered = true
                val correcto = selectedIndex == question.respuestaCorrecta
                if (correcto) vibracionCorrecta(context)
                else vibracionIncorrecta(context)
                // Enviamos el índice seleccionado
                onAnswered(correcto, selectedIndex ?: -1)
            }
        }
    }
    DisposableEffect(Unit) {
        shakeDetector.start()
        onDispose { shakeDetector.stop() }
    }

    /* ─────────────── 🎙️ VOZ ─────────────── */
    val voiceRecognizer = remember {
        ReconocedorVozKahoot(context) { index ->
            if (!answered) selectedIndex = index
        }
    }
    DisposableEffect(Unit) {
        voiceRecognizer.startListening()
        onDispose {
            voiceRecognizer.stop()
            lectorTTS.liberar()
        }
    }

    /* ─────────────── 🎨 UI DE LA PREGUNTA ─────────────── */
    Column {
        question.respuestas.forEachIndexed { index, answer ->
            val isSelected = selectedIndex == index
            val isCorrect = index == question.respuestaCorrecta

            // Animaciones de color
            val targetColor = when {
                answered && isCorrect -> Color(0xFF22C55E) // Verde triunfo
                answered && isSelected && !isCorrect -> Color(0xFFEF4444) // Rojo error
                isSelected -> Color(0xFF6366F1) // Azul selección
                else -> RealMadridBlue.copy(alpha = 0.9f)
            }

            val bgColor by animateColorAsState(targetColor, tween(400))

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.06f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            val elevation by animateDpAsState(if (isSelected) 12.dp else 4.dp)
            val letter = ('A' + index).toString()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .padding(vertical = 10.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !answered
                    ) {
                        selectedIndex = index
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔤 LETRA
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = RealMadridGold,
                    tonalElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            fontWeight = FontWeight.Black,
                            color = RealMadridBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 🟦 BOTÓN DE RESPUESTA
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = bgColor,
                        contentColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(elevation),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) RealMadridGold else Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = answer,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
