package com.example.real_madrid_museo.kahoot.sensores

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class ReconocedorVozKahoot(
    private val context: Context,
    private val onLetterDetected: (Int) -> Unit
) {

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    // Variantes aceptadas por letra
    private val variantesA = listOf("OPCIÓN A", "A", "AH", "EI", "UNO", "1")
    private val variantesB = listOf("OPCIÓN B", "B", "BE", "BI", "DOS", "2")
    private val variantesC = listOf("OPCIÓN C", "C", "CE", "SE", "TRES", "3")
    private val variantesD = listOf("OPCIÓN D", "D", "DE", "CUATRO", "4")

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val matches = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.joinToString(" ")
                    ?.uppercase() ?: return

                Log.d("VOICE", "Detectado: $matches")

                when {
                    variantesA.any { matches.contains(it) } -> onLetterDetected(0)
                    variantesB.any { matches.contains(it) } -> onLetterDetected(1)
                    variantesC.any { matches.contains(it) } -> onLetterDetected(2)
                    variantesD.any { matches.contains(it) } -> onLetterDetected(3)
                }
            }

            override fun onError(error: Int) {
                Log.e("VOICE", "Error reconocimiento: $error")
                // NO hacemos nada → seguimos escuchando
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        recognizer.startListening(intent)
    }

    fun stop() {
        recognizer.stopListening()
        recognizer.destroy()
    }
}