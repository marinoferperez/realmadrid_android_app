package com.example.real_madrid_museo

data class Jugador(
    val nombre: String,
    val posicion: String,
    val descripcionAdulto: String, // Este será el RESUMEN inicial
    val descripcionNino: String,
    val biografiaExtensa: String,  // <--- NUEVO: Aquí va la "Wikipedia"
    val imagenResId: Int
)