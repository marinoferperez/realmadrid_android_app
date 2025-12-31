package com.example.real_madrid_museo.kahoot.sensores

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class ReconocedorVozKahoot(private val context: Context, private val onResult: (Int) -> Unit) {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    private val answerMap = mapOf(
        // --- Español ---
        "1" to 0, "uno" to 0, "opción 1" to 0, "la primera" to 0,
        "2" to 1, "dos" to 1, "opción 2" to 1, "la segunda" to 1,
        "3" to 2, "tres" to 2, "opción 3" to 2, "la tercera" to 2,
        "4" to 3, "cuatro" to 3, "opción 4" to 3, "la cuarta" to 3,

        // --- Inglés ---
        "one" to 0, "option 1" to 0, "the first one" to 0,
        "two" to 1, "option 2" to 1, "the second one" to 1,
        "three" to 2, "option 3" to 2, "the third one" to 2,
        "four" to 3, "option 4" to 3, "the fourth one" to 3
    )

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    speechRecognizer.startListening(speechRecognizerIntent)
                }
            }
            override fun onResults(results: Bundle?) {}

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].lowercase(Locale.getDefault()).trim()
                    Log.d("VOZ", "Detectado: $spokenText")
                    for ((key, value) in answerMap) {
                        if (spokenText.contains(key)) {
                            onResult(value)
                            break
                        }
                    }
                }
            }

            // --- MÉTODO AÑADIDO PARA SOLUCIONAR EL ERROR ---
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    fun stop() {
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
    }
}
