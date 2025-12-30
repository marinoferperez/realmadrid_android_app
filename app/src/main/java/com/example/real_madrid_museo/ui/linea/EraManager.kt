package com.example.real_madrid_museo.ui.linea
import android.content.Context
import android.content.SharedPreferences
import com.example.real_madrid_museo.R

data class EraReal(
    val id: Int,
    val periodoRes: Int,
    val tituloRes: Int,
    val infoCortaRes: Int,
    val infoDetalladaRes: Int,
    val imagenRes: Int,
    val imagenPuzzleRes: Int
)

// 2. LA LISTA DE LAS 9 ÉPOCAS (Ahora usando recursos para multi-idioma)
val listaEras = listOf(
    EraReal(
        id = 0,
        periodoRes = R.string.era0_periodo,
        tituloRes = R.string.era0_titulo,
        infoCortaRes = R.string.era0_info_corta,
        infoDetalladaRes = R.string.era0_info_detallada,
        imagenRes = R.drawable.madrid0,
        imagenPuzzleRes = R.drawable.puzle00
    ),
    EraReal(
        id = 1,
        periodoRes = R.string.era1_periodo,
        tituloRes = R.string.era1_titulo,
        infoCortaRes = R.string.era1_info_corta,
        infoDetalladaRes = R.string.era1_info_detallada,
        imagenRes = R.drawable.madrid1,
        imagenPuzzleRes = R.drawable.puzle10
    ),
    EraReal(
        id = 2,
        periodoRes = R.string.era2_periodo,
        tituloRes = R.string.era2_titulo,
        infoCortaRes = R.string.era2_info_corta,
        infoDetalladaRes = R.string.era2_info_detallada,
        imagenRes = R.drawable.madrid2,
        imagenPuzzleRes = R.drawable.puzle20
    ),
    EraReal(
        id = 3,
        periodoRes = R.string.era3_periodo,
        tituloRes = R.string.era3_titulo,
        infoCortaRes = R.string.era3_info_corta,
        infoDetalladaRes = R.string.era3_info_detallada,
        imagenRes = R.drawable.madrid3,
        imagenPuzzleRes = R.drawable.puzle01
    ),
    EraReal(
        id = 4,
        periodoRes = R.string.era4_periodo,
        tituloRes = R.string.era4_titulo,
        infoCortaRes = R.string.era4_info_corta,
        infoDetalladaRes = R.string.era4_info_detallada,
        imagenRes = R.drawable.madrid4,
        imagenPuzzleRes = R.drawable.puzle11
    ),
    EraReal(
        id = 5,
        periodoRes = R.string.era5_periodo,
        tituloRes = R.string.era5_titulo,
        infoCortaRes = R.string.era5_info_corta,
        infoDetalladaRes = R.string.era5_info_detallada,
        imagenRes = R.drawable.madrid5,
        imagenPuzzleRes = R.drawable.puzle21
    ),
    EraReal(
        id = 6,
        periodoRes = R.string.era6_periodo,
        tituloRes = R.string.era6_titulo,
        infoCortaRes = R.string.era6_info_corta,
        infoDetalladaRes = R.string.era6_info_detallada,
        imagenRes = R.drawable.madrid6,
        imagenPuzzleRes = R.drawable.puzle02
    ),
    EraReal(
        id = 7,
        periodoRes = R.string.era7_periodo,
        tituloRes = R.string.era7_titulo,
        infoCortaRes = R.string.era7_info_corta,
        infoDetalladaRes = R.string.era7_info_detallada,
        imagenRes = R.drawable.madrid7,
        imagenPuzzleRes = R.drawable.puzle12
    ),
    EraReal(
        id = 8,
        periodoRes = R.string.era8_periodo,
        tituloRes = R.string.era8_titulo,
        infoCortaRes = R.string.era8_info_corta,
        infoDetalladaRes = R.string.era8_info_detallada,
        imagenRes = R.drawable.madrid8,
        imagenPuzzleRes = R.drawable.puzle22
    )
)

object EraManager {
    private const val PREFS_NAME = "progreso_museo"
    private const val KEY_SORPRESA = "sorpresa_reclamada"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun desbloquearEra(context: Context, email: String, id: Int) {
        val key = "eras_$email"
        val desbloqueadas = obtenerErasDesbloqueadas(context, email).toMutableSet()
        desbloqueadas.add(id.toString())
        getPrefs(context).edit().putStringSet(key, desbloqueadas).apply()
    }

    fun obtenerErasDesbloqueadas(context: Context, email: String): Set<String> {
        val key = "eras_$email"
        return getPrefs(context).getStringSet(key, emptySet()) ?: emptySet()
    }

    fun estaDesbloqueada(context: Context, email: String, id: Int): Boolean {
        return obtenerErasDesbloqueadas(context, email).contains(id.toString())
    }

    fun obtenerProgreso(context: Context, email: String): Float {
        val total = listaEras.size
        val desbloqueadas = obtenerErasDesbloqueadas(context, email).size
        return if (total > 0) desbloqueadas.toFloat() / total else 0f
    }

    fun haReclamadoSorpresa(context: Context, email: String): Boolean {
        return getPrefs(context).getBoolean("${KEY_SORPRESA}_$email", false)
    }

    fun marcarSorpresaReclamada(context: Context, email: String) {
        getPrefs(context).edit().putBoolean("${KEY_SORPRESA}_$email", true).apply()
    }
}
