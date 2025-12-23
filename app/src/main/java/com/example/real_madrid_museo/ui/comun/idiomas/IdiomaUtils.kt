package com.example.real_madrid_museo.ui.comun

import android.app.Activity
import android.content.Context
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

    context.resources.updateConfiguration(
        config,
        context.resources.displayMetrics
    )
}

fun cambiarIdioma(context: Context, language: String) {
    guardarIdioma(context, language)
    (context as? Activity)?.recreate()
}