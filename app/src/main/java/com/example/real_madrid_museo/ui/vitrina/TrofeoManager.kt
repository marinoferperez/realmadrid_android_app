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

    // Guarda que has visitado un trofeo (por su índice 0, 1, 2...)
    fun marcarTrofeoVisto(context: Context, indice: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("trofeo_$indice", true).apply()

        // Comprobamos si ya los tiene todos para lanzar la notificación
        if (obtenerProgreso(context) == 1.0f) { // 1.0f significa 100%
            lanzarNotificacionDescuento(context)
        }
    }

    fun estaDesbloqueado(context: Context, index: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("trofeo_$index", false)
    }

    // Devuelve el porcentaje de 0.0 a 1.0 (para la barra de progreso)
    fun obtenerProgreso(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val totalTrofeos = listaTrofeos.size // Usa tu lista de 7 trofeos
        var vistos = 0

        for (i in 0 until totalTrofeos) {
            if (prefs.getBoolean("trofeo_$i", false)) {
                vistos++
            }
        }
        return if (totalTrofeos > 0) vistos.toFloat() / totalTrofeos else 0f
    }

    // TU CÓDIGO DE NOTIFICACIÓN ADAPTADO
    private fun lanzarNotificacionDescuento(context: Context) {
        crearCanalNotificacion(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡ENHORABUENA MADRIDISTA!") // Título personalizado
            .setContentText("Has completado la colección de trofeos.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Por haber escaneado todos los trofeos del museo, has desbloqueado un 20% DE DESCUENTO EXTRA en la tienda oficial."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(200, builder.build()) // ID 200 para diferenciarla de la de registro
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