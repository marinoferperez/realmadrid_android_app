package com.example.real_madrid_museo.kahoot.sensores

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class LectorPreguntaTTS(context: Context) {

    private val tts: TextToSpeech
    private var ready = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS

            if (ready) {
                tts.language = Locale("es", "ES")
                tts.setSpeechRate(0.95f)

                pendingText?.let {
                    pendingText = null
                    speak(it)
                }
            }
        }
    }

    fun leer(texto: String) {
        if (!ready) {
            pendingText = texto
            return
        }
        speak(texto)
    }

    private fun speak(texto: String) {
        try {
            tts.speak(
                texto,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "KAHOOT_PREGUNTA"
            )
        } catch (_: Exception) {
            // El emulador puede fallar, lo ignoramos
        }
    }

    fun liberar() {
        tts.stop()
        tts.shutdown()
    }
}