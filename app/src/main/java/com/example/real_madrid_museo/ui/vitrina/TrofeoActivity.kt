package com.example.real_madrid_museo.ui.vitrina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class TrofeoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Recibimos el índice del trofeo que nos manda el Escáner
        val indiceRecibido = intent.getIntExtra("INDICE_TROFEO", 0)

        // 2. Buscamos el trofeo en la lista (que está en Trofeo.kt)
        // Usamos getOrElse por seguridad: si el número no existe, mostramos el primero (Champions)
        val trofeoAVisualizar = listaTrofeos.getOrElse(indiceRecibido) { listaTrofeos[0] }

        setContent {
            // Usamos MaterialTheme para que se vean bien los colores y tipografías
            MaterialTheme {
                Trofeo(
                    trofeo = trofeoAVisualizar,
                    onBackClick = {
                        // Al pulsar la flecha de volver, cerramos esta pantalla y volvemos al escáner
                        finish()
                    }
                )
            }
        }
    }
}