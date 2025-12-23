package com.example.real_madrid_museo.ui.comun

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.real_madrid_museo.R
private const val PREFS = "settings"
private const val KEY_LANG = "lang"

@Composable
fun LanguageToggle(
    currentLanguage: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = if (currentLanguage == "es")
                R.drawable.reino_unido_bandera
            else
                R.drawable.espania_bandera
        ),
        contentDescription = "Change language",
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable { onToggle() }
    )
}

fun guardarIdioma(context: Context, language: String) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_LANG, language)
        .apply()
}

fun obtenerIdioma(context: Context): String {
    return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(KEY_LANG, Locale.getDefault().language) ?: "es"
}

fun updateLocale(context: Context, languageToLoad: String): ContextWrapper {
    val locale = Locale(languageToLoad)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)

    // Esto crea un nuevo contexto con la configuración forzada
    val newContext = context.createConfigurationContext(config)
    return ContextWrapper(newContext)
}

fun cambiarIdioma(context: Context, language: String) {
    guardarIdioma(context, language)
    // Ya no llamamos a aplicarIdioma aquí, solo recreamos
    (context as? Activity)?.recreate()
}

