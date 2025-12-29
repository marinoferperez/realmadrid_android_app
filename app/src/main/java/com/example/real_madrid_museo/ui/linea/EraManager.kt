package com.example.real_madrid_museo.ui.linea
import android.content.Context
import android.content.SharedPreferences
import com.example.real_madrid_museo.R

data class EraReal(
    val id: Int,
    val periodo: String,
    val titulo: String,
    val infoCorta: String,
    val infoDetallada: String,
    val imagenRes: Int,
    val imagenPuzzleRes: Int

)
// 2. LA LISTA DE LAS 9 ÉPOCAS
val listaEras = listOf(
    EraReal(
        id = 0,
        periodo = "1902 - 1940",
        titulo = "Los orígenes",
        infoCorta = "Fundación del club y primer escudo.",
        infoDetallada = "El 6 de marzo de 1902 se fundó oficialmente el Madrid Foot-Ball Club bajo la presidencia de Juan Padrós. En estos primeros años, el club comenzó a destacar en el Campeonato Regional y conquistó sus primeras Copas de España. En 1920, el Rey Alfonso XIII le otorgó el título de 'Real', naciendo así el nombre que hoy conocemos. Fue una época de pioneros que jugaban en campos como el de O'Donnell o el Velódromo, sentando las bases de la ambición blanca.",
        imagenRes = R.drawable.madrid0, // Temporal
        imagenPuzzleRes = R.drawable.puzle00 // Temporal
    ),
    EraReal(
        id = 1,
        periodo = "1940 - 1953",
        titulo = "Don Santiago Bernabéu",
        infoCorta = "Santiago Bernabéu llega a la presidencia del club",
        infoDetallada = "En 1943, Santiago Bernabéu asume la presidencia en uno de los momentos más críticos tras la posguerra. Su visión fue revolucionaria: entendió que para ser los más grandes necesitaban el mejor escenario, impulsando la construcción del nuevo estadio (inaugurado en 1947). Bernabéu no solo reconstruyó las instalaciones, sino que profesionalizó la institución y sembró los valores de señorío y ambición que hoy forman el ADN del Real Madrid.",
        imagenRes = R.drawable.madrid1, // Temporal
        imagenPuzzleRes = R.drawable.puzle10 // Temporal
    ),
    EraReal(
        id = 2,
        periodo = "1953 - 1963",
        titulo = "El Madrid de Di Stéfano",
        infoCorta = "Las 5 Copas de Europa seguidas.",
        infoDetallada = "La llegada de Alfredo Di Stéfano en 1953 marcó un antes y un después en la historia del fútbol. Junto a leyendas como Puskas, Gento, Kopa y Santamaría, el Real Madrid se convirtió en el gran dominador del continente. Conquistaron de forma consecutiva las primeras cinco Copas de Europa (1956-1960), una hazaña jamás repetida. El equipo era admirado por su juego ofensivo y su elegancia, siendo reconocido mundialmente como el mejor equipo de la época.",
        imagenRes = R.drawable.madrid2, // Temporal
        imagenPuzzleRes = R.drawable.puzle20 // Temporal
    ),
    EraReal(
        id = 3,
        periodo = "1963 - 1980",
        titulo = "El Madrid de los Yé-yé",
        infoCorta = "Dominio total en España.",
        infoDetallada = "Tras la retirada de los grandes mitos, el club se reinventó con un equipo formado íntegramente por jugadores españoles, conocidos como 'Los Yé-yé'. Liderados por el veterano Paco Gento, lograron conquistar la Sexta Copa de Europa en 1966 en Bruselas. Durante los años 70, el equipo mantuvo su dominio en España ganando múltiples ligas y copas, consolidando la mística del club y la leyenda de jugadores como Pirri, Amancio o Santillana.",
        imagenRes = R.drawable.madrid3, // Temporal
        imagenPuzzleRes = R.drawable.puzle01 // Temporal
    ),
    EraReal(
        id = 4,
        periodo = "1980 - 1990",
        titulo = "Quinta del Buitre",
        infoCorta = "Cinco ligas consecutivas.",
        infoDetallada = "A principios de los 80 surge de la cantera una generación de futbolistas que cambiaría el estilo del club: la Quinta del Buitre. Formada por Butragueño, Míchel, Sanchís, Martín Vázquez y Pardeza, este equipo enamoró al Bernabéu con un fútbol técnico y asociativo. Lograron el hito de ganar cinco Ligas consecutivas (1986-1990) y dos Copas de la UEFA, marcando una década de fútbol total en España, aunque la Copa de Europa se les resistiera cruelmente.",
        imagenRes = R.drawable.madrid4, // Temporal
        imagenPuzzleRes = R.drawable.puzle11 // Temporal
    ),
    EraReal(
        id = 5,
        periodo = "1990 - 2003",
        titulo = "De la Séptima a la Novena",
        infoCorta = "Regreso al éxito europeo",
        infoDetallada = "La década de los 90 trajo el esperado regreso a la gloria europea. En 1998, el Real Madrid conquistó 'La Séptima' en Ámsterdam gracias al inolvidable gol de Pedja Mijatovic, rompiendo una sequía de 32 años. Fue el inicio de una nueva era dorada donde pronto llegarían la Octava (2000) en la primera final española de la historia, y la Novena (2002) con la volea magistral de Zidane en Glasgow el año del Centenario.",
        imagenRes = R.drawable.madrid5, // Temporal
        imagenPuzzleRes = R.drawable.puzle21 // Temporal
    ),
    EraReal(
        id = 6,
        periodo = "2000 - 2010",
        titulo = "Los Galácticos",
        infoCorta = "Zidane, Ronaldo y el Centenario.",
        infoDetallada = "Con la entrada del nuevo milenio, Florentino Pérez impulsó un modelo basado en reunir a los mejores jugadores del planeta. El club fichó a figuras de la talla de Figo, Zidane, Ronaldo y Beckham, creando el equipo conocido como 'Los Galácticos'. Más allá de los títulos, el club experimentó una expansión global sin precedentes y fue distinguido por la FIFA como el Mejor Club del Siglo XX, elevando la marca Real Madrid a un nivel mundial.",
        imagenRes = R.drawable.madrid6, // Temporal
        imagenPuzzleRes = R.drawable.puzle02 // Temporal
    ),
    EraReal(
        id = 7,
        periodo = "2009 - 2018",
        titulo = "Cristiano Ronaldo. El Bicho",
        infoCorta = "El astro portugués llega a la Castellana.",
        infoDetallada = "En 2009 comenzó uno de los ciclos más exitosos de la historia moderna. Cristiano Ronaldo llegó al club para convertirse en su máximo goleador histórico. Bajo la dirección de entrenadores como Mourinho, Ancelotti y Zidane, se logró el hito histórico de ganar cuatro Champions League en cinco años, incluyendo tres de ellas consecutivas (2016-2018). Fue la era de 'La Décima', las remontadas imposibles y un dominio europeo absoluto.",
        imagenRes = R.drawable.madrid7, // Temporal
        imagenPuzzleRes = R.drawable.puzle12 // Temporal
    ),
    EraReal(
        id = 8,
        periodo = "2019 - Hoy",
        titulo = "La Nueva Era",
        infoCorta = "La recontrucción del Madrid moderno",
        infoDetallada = "El Real Madrid actual afronta el futuro con la remodelación del Santiago Bernabéu, destinado a ser el mejor estadio del mundo. En lo deportivo, el club ha sabido realizar una transición ejemplar uniendo a veteranos de leyenda como Modric o Kroos con jóvenes estrellas como Vinícius Jr, Bellingham o Rodrygo. En 2022 se logró la 14ª Champions con una de las ediciones más épicas que se recuerdan, y en 2024 la 15ª, confirmando que la ambición del club no tiene límites.",
        imagenRes = R.drawable.madrid8, // Temporal
        imagenPuzzleRes = R.drawable.puzle22 // Temporal
    )
)


// 3. EL GESTOR DE ALMACENAMIENTO (Singleton)
object EraManager {
    private const val PREFS_NAME = "progreso_museo"
    private const val KEY_ERAS = "eras_desbloqueadas"
    private const val KEY_SORPRESA = "sorpresa_reclamada"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Guarda una época como desbloqueada
    fun desbloquearEra(context: Context, email: String, id: Int) {
        val key = "eras_$email" // Llave única por usuario
        val desbloqueadas = obtenerErasDesbloqueadas(context, email).toMutableSet()
        desbloqueadas.add(id.toString())
        getPrefs(context).edit().putStringSet(key, desbloqueadas).apply()
    }

    // Obtiene la lista de IDs desbloqueados
    fun obtenerErasDesbloqueadas(context: Context, email: String): Set<String> {
        val key = "eras_$email"
        return getPrefs(context).getStringSet(key, emptySet()) ?: emptySet()
    }

    // Comprueba si una era específica está desbloqueada
    fun estaDesbloqueada(context: Context, email: String, id: Int): Boolean {
        return obtenerErasDesbloqueadas(context, email).contains(id.toString())
    }

    fun obtenerProgreso(context: Context, email: String): Float {
        val total = listaEras.size
        val desbloqueadas = obtenerErasDesbloqueadas(context, email).size
        return if (total > 0) desbloqueadas.toFloat() / total else 0f
    }

    // NUEVAS FUNCIONES PARA LA SORPRESA
    fun haReclamadoSorpresa(context: Context, email: String): Boolean {
        return getPrefs(context).getBoolean("${KEY_SORPRESA}_$email", false)
    }

    fun marcarSorpresaReclamada(context: Context, email: String) {
        getPrefs(context).edit().putBoolean("${KEY_SORPRESA}_$email", true).apply()
    }
}
