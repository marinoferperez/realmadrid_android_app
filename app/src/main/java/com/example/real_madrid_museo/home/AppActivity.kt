package com.example.real_madrid_museo.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.real_madrid_museo.ui.DatabaseHelper
import com.example.real_madrid_museo.ui.comun.aplicarIdioma
import com.example.real_madrid_museo.ui.theme.Real_madrid_museoTheme

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        aplicarIdioma(this)

        val tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: "INVITADO"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val db = DatabaseHelper(this)
        val userData = if (tipoUsuario == "USUARIO") db.getUserDetails(userEmail) else null

        // Extraemos los datos, con valores por defecto si es nulo
        val nombreFinal = userData?.get("name") as? String ?: "Visitante"
        val perfilFinal = userData?.get("profile") as? String ?: "INVITADO"
        val visitas = userData?.get("visits") as? Int ?: 1
        val puntos = userData?.get("points") as? Int ?: 0
        val ranking = userData?.get("ranking") as? Int ?: 0 // Si es 0 o nulo, mostraremos "--"
        
        val esInvitado = (tipoUsuario == "INVITADO")

        setContent {
            Real_madrid_museoTheme {
                MainScreen(
                    nombre = nombreFinal,
                    perfil = perfilFinal,
                    esInvitado = esInvitado,
                    visitas = visitas,
                    puntos = puntos,
                    ranking = ranking, // PASAMOS EL RANKING INICIAL
                    email = if (esInvitado) null else userEmail
                )
            }
        }
    }
}
