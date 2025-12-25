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

        // 1. Recuperamos los datos del Intent (enviados desde LoginActivity)
        val tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: "INVITADO"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // 2. Buscamos los datos en la base de datos ANTES del setContent
        val db = DatabaseHelper(this)
        val userData = if (tipoUsuario == "USUARIO") db.getUserDetails(userEmail) else null

        // 3. Extraemos las variables para pasarlas a la interfaz
        val nombreFinal = userData?.get("name") ?: "Visitante"
        val perfilFinal = userData?.get("profile") ?: "INVITADO"
        val esInvitado = (tipoUsuario == "INVITADO")

        // 4. Lanzamos la interfaz una sola vez
        setContent {
            Real_madrid_museoTheme {
                MainScreen(
                    nombre = nombreFinal,
                    perfil = perfilFinal,
                    esInvitado = esInvitado
                )
            }
        }
    }
}