package com.example.real_madrid_museo.ui.vitrina

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.real_madrid_museo.R

object TrofeoManager {
    private const val PREFS_NAME = "progreso_museo"
    private const val CHANNEL_ID = "canal_descuentos"

    // --- SECCIÓN DE TROFEOS (VITRINA) ---

    fun marcarTrofeoVisto(context: Context, indice: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("trofeo_$indice", true).apply()

        if (obtenerProgreso(context) == 1.0f) {
            lanzarNotificacionDescuento(context)
        }
    }

    fun estaDesbloqueado(context: Context, index: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("trofeo_$index", false)
    }

    fun obtenerProgreso(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val totalTrofeos = listaTrofeos.size
        var vistos = 0

        for (i in 0 until totalTrofeos) {
            if (prefs.getBoolean("trofeo_$i", false)) {
                vistos++
            }
        }
        return if (totalTrofeos > 0) vistos.toFloat() / totalTrofeos else 0f
    }

    // --- SECCIÓN DE SALAS (MAPA / LOGRO EXPLORADOR) ---

    /**
     * Registra que el usuario ha visitado una sala del mapa.
     * Se usa en el botón "Visitar" del MapScreen.
     */
    fun registrarVisitaSeccion(context: Context, nombreSeccion: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Guardamos con un prefijo especial para no mezclarlos con los trofeos
        prefs.edit().putBoolean("seccion_$nombreSeccion", true).apply()
    }

    /**
     * Comprueba si el usuario ha visitado al menos 5 salas diferentes.
     * Se usa en PerfilContent para iluminar el logro de Explorador.
     */
    fun esExplorador(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Obtenemos todos los datos (Corregido: sin espacio en el nombre)
        val todosLosDatos = prefs.all

        // Contamos cuántas llaves que empiezan por "seccion_" son verdaderas
        val contador = todosLosDatos.keys.count { etiqueta ->
            etiqueta.startsWith("seccion_") && todosLosDatos[etiqueta] == true
        }

        // Si el contador llega a 5, devuelve true (Logro conseguido)
        return contador >= 5
    }

    // --- SISTEMA DE NOTIFICACIONES ---

    private fun lanzarNotificacionDescuento(context: Context) {
        crearCanalNotificacion(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡ENHORABUENA MADRIDISTA!")
            .setContentText("Has completado la colección de trofeos.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Por haber escaneado todos los trofeos del museo, has desbloqueado un 20% DE DESCUENTO EXTRA en la tienda oficial."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(200, builder.build())
            }
        }
    }

    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Descuentos Museo"
            val descriptionText = "Notificaciones de logros y premios"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}