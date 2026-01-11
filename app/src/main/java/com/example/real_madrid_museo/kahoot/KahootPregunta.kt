package com.example.real_madrid_museo.kahoot

import androidx.annotation.StringRes

// se define la estructura de una pregunta de Kahoot
data class KahootPregunta(
    // string que contiene el enunciado de la pregunta
    @StringRes val pregunta: Int,
    // lista de enteros que representan las posibles respuestas
    val respuestas: List<Int>,
    // índice de la respuesta correcta dentro de la lista de respuestas
    val respuestaCorrecta: Int,
    // tiempo máximo en segundos para responder la pregunta
    // por defecto = 30
    val tiempoLimite: Int = 1
)
