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
        infoDetallada = "En 1902 nace el Madrid Foot-Ball Club. En este periodo se empieza a forjar la leyenda en el antiguo Campo de O'Donnell.",
        imagenRes = R.drawable.madrid0, // Temporal
        imagenPuzzleRes = R.drawable.puzle00 // Temporal
    ),
    EraReal(
        id = 1,
        periodo = "1940 - 1953",
        titulo = "Don Santiago Bernabéu",
        infoCorta = "Santiago Bernabéu llega a la presidencia del club",
        infoDetallada = "Santiago Bernabéu llega a la presidencia en 1943 para reconstruir el club y proyectar el estadio que hoy lleva su nombre.",
        imagenRes = R.drawable.madrid1, // Temporal
        imagenPuzzleRes = R.drawable.puzle10 // Temporal
    ),
    EraReal(
        id = 2,
        periodo = "1953 - 1963",
        titulo = "El Madrid de Di Stéfano",
        infoCorta = "Las 5 Copas de Europa seguidas.",
        infoDetallada = "Con el fichaje de Alfredo Di Stéfano, el Real Madrid se convierte en el mejor equipo del mundo ganando las 5 primeras Copas de Europa.",
        imagenRes = R.drawable.madrid2, // Temporal
        imagenPuzzleRes = R.drawable.puzle20 // Temporal
    ),
    EraReal(
        id = 3,
        periodo = "1963 - 1980",
        titulo = "El Madrid de los Yé-yé",
        infoCorta = "Dominio total en España.",
        infoDetallada = "Un Madrid liderado por Paco Gento y formado solo por jugadores españoles conquista la Sexta Copa de Europa en 1966.",
        imagenRes = R.drawable.madrid3, // Temporal
        imagenPuzzleRes = R.drawable.puzle01 // Temporal
    ),
    EraReal(
        id = 4,
        periodo = "1980 - 1990",
        titulo = "Quinta del Buitre",
        infoCorta = "Cinco ligas consecutivas.",
        infoDetallada = "Una generación irrepetible de canteranos (Butragueño, Míchel, Sanchís, Martín Vázquez y Pardeza) maravilla al mundo.",
        imagenRes = R.drawable.madrid4, // Temporal
        imagenPuzzleRes = R.drawable.puzle11 // Temporal
    ),
    EraReal(
        id = 5,
        periodo = "1990 - 2003",
        titulo = "De la Séptima a la Novena",
        infoCorta = "Regreso al éxito europeo",
        infoDetallada = "Después de 32 años de espera, el gol de Mijatovic en Ámsterdam devuelve al Madrid al trono de Europa en 1998.",
        imagenRes = R.drawable.madrid5, // Temporal
        imagenPuzzleRes = R.drawable.puzle21 // Temporal
    ),
    EraReal(
        id = 6,
        periodo = "2000 - 2010",
        titulo = "Los Galácticos",
        infoCorta = "Zidane, Ronaldo y el Centenario.",
        infoDetallada = "Florentino Pérez ficha a los mejores del mundo y el club es nombrado por la FIFA como el Mejor Club del Siglo XX.",
        imagenRes = R.drawable.madrid6, // Temporal
        imagenPuzzleRes = R.drawable.puzle02 // Temporal
    ),
    EraReal(
        id = 7,
        periodo = "2009 - 2018",
        titulo = "Cristiano Ronaldo. El Bicho",
        infoCorta = "El astro portugués llega a la Castellana.",
        infoDetallada = "Se convierte en el fichaje más caro de la historia del club y marca una época en su binomio con Leo Messi, consiguiendo entrar en el olimpo del fútbol.",
        imagenRes = R.drawable.madrid7, // Temporal
        imagenPuzzleRes = R.drawable.puzle12 // Temporal
    ),
    EraReal(
        id = 8,
        periodo = "2019 - Hoy",
        titulo = "La Nueva Era",
        infoCorta = "La recontrucción del Madrid moderno",
        infoDetallada = "Los últimos años de los artifices del triplete europeo y las jóvenes estrellas, se unen para volver el nuevo Bernabéu en un manicomio al conseguir 2 Champions legendarias.",
        imagenRes = R.drawable.madrid8, // Temporal
        imagenPuzzleRes = R.drawable.puzle22 // Temporal
    )
)


// 3. EL GESTOR DE ALMACENAMIENTO (Singleton)
object EraManager {
    private const val PREFS_NAME = "progreso_museo"
    private const val KEY_ERAS = "eras_desbloqueadas"

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
}

