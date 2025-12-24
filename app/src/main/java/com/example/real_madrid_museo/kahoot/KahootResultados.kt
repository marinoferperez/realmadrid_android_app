package com.example.real_madrid_museo.kahoot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.onboarding.FondoAnimadoKahoot
import com.example.real_madrid_museo.ui.theme.RealMadridBlue
import com.example.real_madrid_museo.ui.theme.RealMadridGold
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

@Composable
fun PantallaResultados(
    totalPreguntas: Int,
    respuestasCorrectas: Int,
    preguntasFalladas: List<PreguntaFallada>, // Actualizado al nuevo tipo
    onReiniciar: () -> Unit,
    onFinalizar: () -> Unit
) {
    val context = LocalContext.current
    var mostrarSoluciones by remember { mutableStateOf(false) }

    // Lógica para confeti (temporalmente siempre true para testing)
    val porcentajeAciertos = if (totalPreguntas > 0) (respuestasCorrectas.toFloat() / totalPreguntas) * 100 else 0f
    // TODO: Cambiar a 'porcentajeAciertos >= 80f' después de verificar
    val mostrarConfeti = true // porcentajeAciertos >= 80f

    val party = remember {
        Party(
            speed = 10f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfeca57, 0xf39c12, 0xFEF9E7, 0xFFFFFF), // Tonos dorados y blancos
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(50),
            position = Position.Relative(0.5, 0.3),
            size = listOf(Size.MEDIUM, Size.LARGE)
        )
    }

    // Efecto para lanzar la notificación al mostrar los resultados
    LaunchedEffect(Unit) {
        val mensaje = when {
            respuestasCorrectas == totalPreguntas -> "¡Perfecto, madridista legendario! 🤍"
            respuestasCorrectas >= totalPreguntas / 2 -> "¡Gran visita al museo! ⚽"
            else -> "¡Buen intento! Aprende y vuelve a jugar 💪"
        }
        enviarNotificacionResultados(context, mensaje, respuestasCorrectas, totalPreguntas)
    }

    if (mostrarSoluciones) {
        PantallaSoluciones(
            preguntasFalladas = preguntasFalladas,
            onVolver = { mostrarSoluciones = false },
            onReiniciar = onReiniciar,
            onFinalizar = onFinalizar
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Fondo (capa más baja)
            FondoAnimadoKahoot()

            // 2. Contenido Principal (capa media)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = RealMadridBlue.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, RealMadridGold),
                    elevation = CardDefaults.cardElevation(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (respuestasCorrectas == totalPreguntas) "¡PERFECTO!" else "¡JUEGO TERMINADO!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = RealMadridGold,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Puntuación Final",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Text(
                            text = "$respuestasCorrectas / $totalPreguntas",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (preguntasFalladas.isNotEmpty()) {
                            Button(
                                onClick = { mostrarSoluciones = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ver Soluciones",
                                    color = RealMadridBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = onFinalizar,
                                border = BorderStroke(1.dp, RealMadridGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RealMadridGold),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salir", maxLines = 1)
                            }
                            
                            Button(
                                onClick = onReiniciar,
                                colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = RealMadridBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reiniciar", color = RealMadridBlue, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // 3. Confeti (capa superior, ENCIMA de todo)
            if (mostrarConfeti) {
                KonfettiView(
                    parties = listOf(party),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Función auxiliar para enviar la notificación
private fun enviarNotificacionResultados(
    context: Context,
    mensaje: String,
    aciertos: Int,
    total: Int
) {
    // 1. Crear el canal de notificación (necesario para Android 8.0+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "kahoot_results_channel"
        val channelName = "Resultados del Kahoot"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Canal para mostrar los resultados del juego"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // 2. Verificar permisos (necesario para Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return 
        }
    }

    // 3. Construir la notificación
    val builder = NotificationCompat.Builder(context, "kahoot_results_channel")
        .setSmallIcon(R.drawable.logo_rm2)
        .setContentTitle("¡Juego Terminado! ($aciertos/$total)")
        .setContentText(mensaje)
        .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // 4. Mostrar la notificación
    with(NotificationManagerCompat.from(context)) {
        try {
            notify(1001, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSoluciones(
    preguntasFalladas: List<PreguntaFallada>, // Tipo actualizado
    onVolver: () -> Unit,
    onReiniciar: () -> Unit,
    onFinalizar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soluciones", color = RealMadridGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = RealMadridGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RealMadridBlue
                )
            )
        },
        containerColor = RealMadridBlue
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            FondoAnimadoKahoot()
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(preguntasFalladas) { fallo ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Pregunta
                            Text(
                                text = fallo.pregunta.pregunta,
                                style = MaterialTheme.typography.titleMedium,
                                color = RealMadridBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // 1. Respuesta Incorrecta (del usuario), si existe
                            if (fallo.respuestaSeleccionada != -1) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Default.Close, 
                                        contentDescription = null, 
                                        tint = Color(0xFFEF4444), // Rojo
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = fallo.pregunta.respuestas[fallo.respuestaSeleccionada],
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black.copy(alpha = 0.6f), // Texto un poco más suave
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.weight(1f) // Evita desbordamiento
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                // Si fue timeout
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Default.Close, 
                                        contentDescription = null, 
                                        tint = Color(0xFFEF4444), // Rojo
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tiempo agotado",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // 2. Respuesta Correcta (siempre visible debajo)
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.CheckCircle, 
                                    contentDescription = null, 
                                    tint = Color(0xFF22C55E), // Verde
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = fallo.pregunta.respuestas[fallo.pregunta.respuestaCorrecta],
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold, // Más destacado
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                         Button(
                            onClick = onReiniciar,
                            colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = RealMadridBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                text = "Jugar de nuevo",
                                color = RealMadridBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onFinalizar,
                            border = BorderStroke(1.dp, RealMadridGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RealMadridGold),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                text = "Finalizar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
