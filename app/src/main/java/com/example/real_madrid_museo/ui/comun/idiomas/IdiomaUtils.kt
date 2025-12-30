package com.example.real_madrid_museo.ui.comun.idiomas

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

private const val PREFS = "settings"
private const val KEY_LANG = "lang"

fun guardarIdioma(context: Context, language: String) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_LANG, language)
        .apply()
}

fun obtenerIdioma(context: Context): String {
    return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(KEY_LANG, "es") ?: "es"
}

fun aplicarIdioma(context: Context) {
    val language = obtenerIdioma(context)
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    // Actualizamos la configuración de recursos
    context.resources.updateConfiguration(
        config,
        context.resources.displayMetrics
    )
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

fun cambiarIdioma(context: Context, language: String) {
    guardarIdioma(context, language)
    aplicarIdioma(context)
    
    // Recreamos la actividad para que todos los componentes (Compose y XML) se actualicen
    context.findActivity()?.recreate()
}
