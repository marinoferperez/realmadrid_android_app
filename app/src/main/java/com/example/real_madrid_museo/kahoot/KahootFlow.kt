package com.example.real_madrid_museo.kahoot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Clase para almacenar el resultado de una pregunta fallada
data class PreguntaFallada(
    val pregunta: KahootPregunta,
    val respuestaSeleccionada: Int // Índice de la respuesta que eligió el usuario (-1 si fue timeout)
)

@Composable
fun KahootFlow() {
    var joined by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(0) }
    
    // Lista de preguntas para esta sesión de juego
    var currentQuestions by remember { mutableStateOf(emptyList<KahootPregunta>()) }

    // Estado para los resultados
    var correctAnswers by remember { mutableStateOf(0) }
    
    // Lista de preguntas falladas con la respuesta del usuario
    val failedQuestions = remember { mutableStateListOf<PreguntaFallada>() }

    if (!joined) {
        KahootJoinScreen(onJoined = {
            // AL UNIRSE: Seleccionamos 10 nuevas preguntas aleatorias
            currentQuestions = todasLasPreguntasRealMadrid.shuffled().take(10)
            
            joined = true
            index = 0
            correctAnswers = 0
            failedQuestions.clear()
        })
        return
    }

    if (index >= currentQuestions.size) {
        PantallaResultados(
            totalPreguntas = currentQuestions.size,
            respuestasCorrectas = correctAnswers,
            preguntasFalladas = failedQuestions,
            onReiniciar = {
                // AL REINICIAR: Seleccionamos OTRAS 10 preguntas nuevas
                currentQuestions = todasLasPreguntasRealMadrid.shuffled().take(10)
                
                // Reiniciamos el juego
                index = 0
                correctAnswers = 0
                failedQuestions.clear()
            },
            onFinalizar = {
                // Volvemos a la pantalla de unirse
                joined = false
                index = 0
                correctAnswers = 0
                failedQuestions.clear()
                currentQuestions = emptyList()
            }
        )
        return
    }

    val current = currentQuestions[index]
    val scope = rememberCoroutineScope()

    // Renderizamos directamente la pantalla de respuestas.
    // La animación de transición interna se maneja ahora dentro de pantallaRespuestas
    pantallaRespuestas(
        question = current,
        currentQuestionIndex = index,
        totalQuestions = currentQuestions.size,
        onAnswered = { isCorrect, selectedIndex ->
            if (isCorrect) {
                correctAnswers++
            } else {
                // Guardamos la pregunta y la respuesta que dio el usuario
                failedQuestions.add(PreguntaFallada(current, selectedIndex))
            }

            // Coroutine para manejar el tiempo entre preguntas
            scope.launch {
                delay(1000) // Delay breve para ver el feedback del botón antes de cambiar
                index++
            }
        }
    )
}
