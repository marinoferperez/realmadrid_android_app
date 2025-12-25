package com.example.real_madrid_museo.kahoot

import com.example.real_madrid_museo.kahoot.KahootPregunta

// Lista extendida de preguntas
val todasLasPreguntasRealMadrid = listOf(
    // 1. Historia y Fundación
    KahootPregunta(
        pregunta = "¿En qué año se fundó el Real Madrid?",
        respuestas = listOf("1902", "1910", "1925", "1899"),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = "¿Cuál fue el primer nombre del club?",
        respuestas = listOf("Real Madrid FC", "Madrid Foot-Ball Club", "Sociedad Deportiva Madrid", "Club Español de Madrid"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Quién fue el primer presidente del Real Madrid?",
        respuestas = listOf("Santiago Bernabéu", "Juan Padrós", "Adolfo Meléndez", "Carlos Padrós"),
        respuestaCorrecta = 1
    ),

    // 2. Jugadores Leyenda
    KahootPregunta(
        pregunta = "¿Quién es el máximo goleador histórico del Real Madrid?",
        respuestas = listOf("Raúl", "Benzema", "Di Stéfano", "Cristiano Ronaldo"),
        respuestaCorrecta = 3
    ),
    KahootPregunta(
        pregunta = "¿Qué jugador ganó 6 Copas de Europa con el club?",
        respuestas = listOf("Alfredo Di Stéfano", "Paco Gento", "Ferenc Puskás", "Amancio"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Quién es el jugador con más partidos en la historia del Real Madrid?",
        respuestas = listOf("Casillas", "Sergio Ramos", "Raúl", "Sanchís"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Qué dorsal mítico llevó Juanito?",
        respuestas = listOf("7", "9", "10", "5"),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = "¿Quién era conocido como 'La Saeta Rubia'?",
        respuestas = listOf("Di Stéfano", "Puskás", "Kopa", "Gento"),
        respuestaCorrecta = 0
    ),

    // 3. Champions League
    KahootPregunta(
        pregunta = "¿Quién NO marcó en la final de la Champions 2014?",
        respuestas = listOf("Cristiano Ronaldo", "Sergio Ramos", "Benzema", "Gareth Bale"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Quién fue capitán en la Décima?",
        respuestas = listOf("Casillas", "Sergio Ramos", "Cristiano", "Modric"),
        respuestaCorrecta = 0
    ),
    KahootPregunta(
        pregunta = "¿Cuántas Champions League ha ganado el Real Madrid hasta 2024?",
        respuestas = listOf("13", "14", "15", "16"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Contra qué equipo ganó la 'Séptima'?",
        respuestas = listOf("Bayer Leverkusen", "Valencia", "Juventus", "Liverpool"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Quién marcó el gol de la 'Novena'?",
        respuestas = listOf("Raúl", "Zidane", "Morientes", "Roberto Carlos"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Cuántas veces consecutivas ganó el Real Madrid la Champions entre 2016 y 2018?",
        respuestas = listOf("2", "3", "4", "5"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿En qué ciudad se ganó la 'Duodécima'?",
        respuestas = listOf("Lisboa", "Milán", "Cardiff", "Kiev"),
        respuestaCorrecta = 2
    ),

    // 4. Entrenadores y Estadio
    KahootPregunta(
        pregunta = "¿Quién fue entrenador del Real Madrid durante la Décima?",
        respuestas = listOf("Mourinho", "Zidane", "Ancelotti", "Del Bosque"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿En qué año se inauguró el estadio Santiago Bernabéu?",
        respuestas = listOf("1952", "1939", "1960", "1947"),
        respuestaCorrecta = 3
    ),
    KahootPregunta(
        pregunta = "¿Qué entrenador ganó 3 Champions seguidas?",
        respuestas = listOf("Del Bosque", "Mourinho", "Zidane", "Ancelotti"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Dónde se ubica la Ciudad Deportiva?",
        respuestas = listOf("Valdebebas", "Las Rozas", "Alcorcón", "Getafe"),
        respuestaCorrecta = 0
    ),

    // 5. Curiosidades y Actualidad
    KahootPregunta(
        pregunta = "¿Quién ganó el Balón de Oro 2018 siendo jugador del Madrid?",
        respuestas = listOf("Cristiano Ronaldo", "Modric", "Benzema", "Kroos"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Cuál de estos jugadores NO jugó en el Real Madrid?",
        respuestas = listOf("Beckham", "Zidane", "Ronaldinho", "Ronaldo Nazário"),
        respuestaCorrecta = 2
    ),
    KahootPregunta(
        pregunta = "¿Qué apodo recibe la cantera del Real Madrid?",
        respuestas = listOf("La Masía", "La Fábrica", "Lezama", "Valdebebas"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Quién es el portero titular en la final de la 14ª?",
        respuestas = listOf("Keylor Navas", "Courtois", "Lunin", "Casillas"),
        respuestaCorrecta = 1
    ),
    KahootPregunta(
        pregunta = "¿Cómo se llama el grupo de animación del fondo sur?",
        respuestas = listOf("Ultras Sur", "Grada Fans", "Frente Atlético", "Biris Norte"),
        respuestaCorrecta = 1
    )
)

// Selecciona 10 preguntas aleatorias cada vez que se llama
val preguntasRealMadrid = todasLasPreguntasRealMadrid.shuffled().take(10)
