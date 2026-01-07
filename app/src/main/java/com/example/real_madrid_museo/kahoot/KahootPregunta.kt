package com.example.real_madrid_museo.kahoot

import androidx.annotation.StringRes

// se define la estructura de una pregunta de Kahoot

data class KahootPregunta(
    @StringRes val pregunta: Int,
    val respuestas: List<Int>, // List of StringRes IDs
    val respuestaCorrecta: Int,
    val tiempoLimite: Int = 50
)
