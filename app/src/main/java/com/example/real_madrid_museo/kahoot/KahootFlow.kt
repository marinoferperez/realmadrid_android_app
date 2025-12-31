package com.example.real_madrid_museo.kahoot

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Clase para almacenar el resultado de una pregunta fallada
data class PreguntaFallada(
    val pregunta: KahootPregunta,
    val respuestaSeleccionada: Int // Índice de la respuesta que eligió el usuario (-1 si fue timeout)
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KahootFlow() {
    val context = LocalContext.current
    var gameStarted by rememberSaveable { mutableStateOf(false) }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var currentQuestions by remember { mutableStateOf(emptyList<KahootPregunta>()) }
    var correctAnswers by rememberSaveable { mutableIntStateOf(0) }
    val failedQuestions = remember { mutableStateListOf<PreguntaFallada>() }
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = gameStarted,
        transitionSpec = {
            // Si estamos empezando el juego (pasamos de false a true)
            if (targetState) {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, 
                    animationSpec = tween(600, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 100)) togetherWith
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight }, 
                    animationSpec = tween(600)
                ) + fadeOut(animationSpec = tween(600))
            } else {
                // Si volvemos a las instrucciones (pasamos de true a false)
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight }, 
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600)) togetherWith
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }, 
                    animationSpec = tween(600, delayMillis = 100)
                ) + fadeOut(animationSpec = tween(600, delayMillis = 100))
            }
        },
        label = "KahootFlowTransition"
    ) { isGameStarted ->
        if (!isGameStarted) {
            KahootInstructionsScreen(onStart = {
                currentQuestions = todasLasPreguntasRealMadrid.shuffled().take(10)
                gameStarted = true
                index = 0
                correctAnswers = 0
                failedQuestions.clear()
            })
        } else {
            // --- Flujo del juego principal ---
            if (index >= currentQuestions.size) {
                PantallaResultados(
                    totalPreguntas = currentQuestions.size,
                    respuestasCorrectas = correctAnswers,
                    preguntasFalladas = failedQuestions,
                    onReiniciar = { gameStarted = false }, // Vuelve a instrucciones
                    onFinalizar = { 
                        // MODIFICADO: Cierra la actividad Kahoot para volver al MainScreen
                        if (context is Activity) {
                            context.finish()
                        }
                    }
                )
            } else {
                val current = currentQuestions[index]
                pantallaRespuestas(
                    question = current,
                    currentQuestionIndex = index,
                    totalQuestions = currentQuestions.size,
                    onAnswered = { isCorrect, selectedIndex ->
                        if (isCorrect) {
                            correctAnswers++
                        } else {
                            failedQuestions.add(PreguntaFallada(current, selectedIndex))
                        }

                        scope.launch {
                            delay(1000)
                            index++
                        }
                    }
                )
            }
        }
    }
}
