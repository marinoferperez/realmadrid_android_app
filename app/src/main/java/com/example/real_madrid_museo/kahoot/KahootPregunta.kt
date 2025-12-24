package com.example.real_madrid_museo.kahoot

// se define la estructura de una pregunta de Kahoot

data class KahootPregunta(
    val pregunta: String,
    val respuestas: List<String>,
    val respuestaCorrecta: Int,
    val tiempoLimite: Int = 5
)