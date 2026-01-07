package com.example.real_madrid_museo.kahoot.sensores

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private fun getVibrator(context: Context): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}


fun vibracionCorrecta(context: Context) {
    val vibrator = getVibrator(context)

    // Verificamos si el dispositivo tiene vibrador
    if (!vibrator.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(
            150,
            VibrationEffect.DEFAULT_AMPLITUDE
        )

        // Usar AudioAttributes asegura que la vibración se trate como parte del juego,
        // evitando que algunos modos de "No molestar" o configuraciones de sistema la bloqueen.
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        vibrator.vibrate(effect, audioAttributes)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(150)
    }
}

fun vibracionIncorrecta(context: Context) {
    val vibrator = getVibrator(context)

    if (!vibrator.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 120, 80, 120),
            -1
        )

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        vibrator.vibrate(effect, audioAttributes)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 120, 80, 120), -1)
    }
}

fun vibracionCuentaAtras(context: Context) {
    val vibrator = getVibrator(context)
    if (!vibrator.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Vibración muy corta y seca para marcar el segundo
        val effect = VibrationEffect.createOneShot(
            50,
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        vibrator.vibrate(effect, audioAttributes)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}
