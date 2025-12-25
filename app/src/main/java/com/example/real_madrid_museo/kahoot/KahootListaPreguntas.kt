package com.example.real_madrid_museo.kahoot

import com.example.real_madrid_museo.R

// Lista extendida de preguntas
val todasLasPreguntasRealMadrid = listOf(
    // 1. Historia y Fundación
    KahootPregunta(
        pregunta = R.string.kq1_question,
        respuestas = listOf(R.string.kq1_a1, R.string.kq1_a2, R.string.kq1_a3, R.string.kq1_a4),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = R.string.kq2_question,
        respuestas = listOf(R.string.kq2_a1, R.string.kq2_a2, R.string.kq2_a3, R.string.kq2_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq3_question,
        respuestas = listOf(R.string.kq3_a1, R.string.kq3_a2, R.string.kq3_a3, R.string.kq3_a4),
        respuestaCorrecta = 1
    ),

    // 2. Jugadores Leyenda
    KahootPregunta(
        pregunta = R.string.kq4_question,
        respuestas = listOf(R.string.kq4_a1, R.string.kq4_a2, R.string.kq4_a3, R.string.kq4_a4),
        respuestaCorrecta = 3
    ),
    KahootPregunta(
        pregunta = R.string.kq5_question,
        respuestas = listOf(R.string.kq5_a1, R.string.kq5_a2, R.string.kq5_a3, R.string.kq5_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq6_question,
        respuestas = listOf(R.string.kq6_a1, R.string.kq6_a2, R.string.kq6_a3, R.string.kq6_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq7_question,
        respuestas = listOf(R.string.kq7_a1, R.string.kq7_a2, R.string.kq7_a3, R.string.kq7_a4),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = R.string.kq8_question,
        respuestas = listOf(R.string.kq8_a1, R.string.kq8_a2, R.string.kq8_a3, R.string.kq8_a4),
        respuestaCorrecta = 0
    ),

    // 3. Champions League
    KahootPregunta(
        pregunta = R.string.kq9_question,
        respuestas = listOf(R.string.kq9_a1, R.string.kq9_a2, R.string.kq9_a3, R.string.kq9_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq10_question,
        respuestas = listOf(R.string.kq10_a1, R.string.kq10_a2, R.string.kq10_a3, R.string.kq10_a4),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = R.string.kq11_question,
        respuestas = listOf(R.string.kq11_a1, R.string.kq11_a2, R.string.kq11_a3, R.string.kq11_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq12_question,
        respuestas = listOf(R.string.kq12_a1, R.string.kq12_a2, R.string.kq12_a3, R.string.kq12_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq13_question,
        respuestas = listOf(R.string.kq13_a1, R.string.kq13_a2, R.string.kq13_a3, R.string.kq13_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq14_question,
        respuestas = listOf(R.string.kq14_a1, R.string.kq14_a2, R.string.kq14_a3, R.string.kq14_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq15_question,
        respuestas = listOf(R.string.kq15_a1, R.string.kq15_a2, R.string.kq15_a3, R.string.kq15_a4),
        respuestaCorrecta = 2
    ),

    // 4. Entrenadores y Estadio
    KahootPregunta(
        pregunta = R.string.kq16_question,
        respuestas = listOf(R.string.kq16_a1, R.string.kq16_a2, R.string.kq16_a3, R.string.kq16_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq17_question,
        respuestas = listOf(R.string.kq17_a1, R.string.kq17_a2, R.string.kq17_a3, R.string.kq17_a4),
        respuestaCorrecta = 3
    ),
    KahootPregunta(
        pregunta = R.string.kq18_question,
        respuestas = listOf(R.string.kq18_a1, R.string.kq18_a2, R.string.kq18_a3, R.string.kq18_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq19_question,
        respuestas = listOf(R.string.kq19_a1, R.string.kq19_a2, R.string.kq19_a3, R.string.kq19_a4),
        respuestaCorrecta = 0
    ),

    // 5. Curiosidades y Actualidad
    KahootPregunta(
        pregunta = R.string.kq20_question,
        respuestas = listOf(R.string.kq20_a1, R.string.kq20_a2, R.string.kq20_a3, R.string.kq20_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq21_question,
        respuestas = listOf(R.string.kq21_a1, R.string.kq21_a2, R.string.kq21_a3, R.string.kq21_a4),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = R.string.kq22_question,
        respuestas = listOf(R.string.kq22_a1, R.string.kq22_a2, R.string.kq22_a3, R.string.kq22_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq23_question,
        respuestas = listOf(R.string.kq23_a1, R.string.kq23_a2, R.string.kq23_a3, R.string.kq23_a4),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = R.string.kq24_question,
        respuestas = listOf(R.string.kq24_a1, R.string.kq24_a2, R.string.kq24_a3, R.string.kq24_a4),
        respuestaCorrecta = 1
    )
)

// Selecciona 10 preguntas aleatorias cada vez que se llama
val preguntasRealMadrid = todasLasPreguntasRealMadrid.shuffled().take(10)
