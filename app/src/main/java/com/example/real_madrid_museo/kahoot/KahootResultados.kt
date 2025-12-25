package com.example.real_madrid_museo.kahoot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.DatabaseHelper
import com.example.real_madrid_museo.ui.onboarding.FondoAnimadoKahoot
import com.example.real_madrid_museo.ui.theme.RealMadridBlue
import com.example.real_madrid_museo.ui.theme.RealMadridGold
import kotlinx.coroutines.delay
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
    preguntasFalladas: List<PreguntaFallada>,
    onReiniciar: () -> Unit,
    onFinalizar: () -> Unit
) {
    val context = LocalContext.current
    var mostrarSoluciones by remember { mutableStateOf(false) }
    var showPointsPopup by remember { mutableStateOf(false) }

    // Calculamos los puntos: 10 puntos por respuesta correcta
    val puntosGanados = respuestasCorrectas * 10

    // Lógica para confeti (se mostrará si se supera el 50%)
    val porcentajeAciertos = if (totalPreguntas > 0) (respuestasCorrectas.toFloat() / totalPreguntas) * 100 else 0f
    val mostrarConfeti = porcentajeAciertos >= 50f // Bajamos el umbral para ver el efecto más fácilmente

    val party = remember {
        Party(
            speed = 10f, maxSpeed = 30f, damping = 0.9f, spread = 360,
            colors = listOf(0xfeca57, 0xf39c12, 0xFEF9E7, 0xFFFFFF),
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(50),
            position = Position.Relative(0.5, 0.3),
            size = listOf(Size.MEDIUM, Size.LARGE)
        )
    }

    // Efectos lanzados una sola vez al entrar
    LaunchedEffect(Unit) {
        // 1. Mostrar el popup de puntos
        showPointsPopup = true
        
        // 2. GUARDAR PUNTOS EN BBDD
        if (puntosGanados > 0) {
            val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val currentEmail = sharedPref.getString("current_email", null)
            
            if (currentEmail != null) {
                val db = DatabaseHelper(context)
                db.addPoints(currentEmail, puntosGanados)
            }
        }

        // 3. Enviar notificación push
        val mensaje = when {
            respuestasCorrectas == totalPreguntas -> context.getString(R.string.result_msg_perfect)
            porcentajeAciertos >= 50 -> context.getString(R.string.result_msg_good)
            else -> context.getString(R.string.result_msg_retry)
        }
        enviarNotificacionResultados(context, mensaje, respuestasCorrectas, totalPreguntas)

        // 4. Ocultar el popup después de 4 segundos
        delay(4000)
        showPointsPopup = false
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
            FondoAnimadoKahoot()

            // --- Contenido Principal ---
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RealMadridBlue.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, RealMadridGold),
                    elevation = CardDefaults.cardElevation(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = if (respuestasCorrectas == totalPreguntas) stringResource(R.string.game_over_perfect) else stringResource(R.string.game_over),
                             style = MaterialTheme.typography.headlineMedium, color = RealMadridGold, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = stringResource(R.string.final_score_title), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text(text = "$respuestasCorrectas / $totalPreguntas",
                             style = MaterialTheme.typography.displayLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(32.dp))
                        if (preguntasFalladas.isNotEmpty()) {
                            Button(
                                onClick = { mostrarSoluciones = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.view_solutions), color = RealMadridBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            OutlinedButton(
                                onClick = onFinalizar,
                                border = BorderStroke(1.dp, RealMadridGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RealMadridGold),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.button_exit), maxLines = 1)
                            }
                            Button(
                                onClick = onReiniciar,
                                colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = RealMadridBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.button_restart), color = RealMadridBlue, maxLines = 1)
                            }
                        }
                    }
                }
            }
            
            // --- Capas Superiores (Confeti y Popup) ---
            if (mostrarConfeti) {
                KonfettiView(
                    parties = listOf(party), 
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f) // Aseguramos que se dibuje ENCIMA de todo
                )
            }

            PointsPopup(visible = showPointsPopup, points = puntosGanados)
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun PointsPopup(visible: Boolean, points: Int) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(700)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500, delayMillis = 3000)) + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500, delayMillis = 3000)),
        modifier = Modifier.fillMaxSize().zIndex(20f) // También aseguramos que el popup esté encima
    ) {
        // CAMBIO: Alineación BottomCenter con padding para que no tape el centro
        Box(contentAlignment = Alignment.BottomCenter) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, RealMadridGold),
                modifier = Modifier.padding(bottom = 100.dp) // Margen inferior para separarlo del borde
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("+$points", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = RealMadridBlue)
                    Text(stringResource(R.string.points_popup_text), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RealMadridBlue)
                }
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "kahoot_results_channel"
        val channelName = "Resultados del Kahoot"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply { description = "Canal para mostrar los resultados del juego" }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return 
        }
    }

    val builder = NotificationCompat.Builder(context, "kahoot_results_channel")
        .setSmallIcon(R.drawable.logo_rm2)
        .setContentTitle(context.getString(R.string.result_notif_title, aciertos, total))
        .setContentText(mensaje)
        .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

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
    preguntasFalladas: List<PreguntaFallada>,
    onVolver: () -> Unit,
    onReiniciar: () -> Unit,
    onFinalizar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.solutions_title), color = RealMadridGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = RealMadridGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RealMadridBlue)
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
                            Text(text = stringResource(fallo.pregunta.pregunta), style = MaterialTheme.typography.titleMedium, color = RealMadridBlue, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (fallo.respuestaSeleccionada != -1) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stringResource(fallo.pregunta.respuestas[fallo.respuestaSeleccionada]), style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.6f), fontWeight = FontWeight.Normal, modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.solutions_timeout), style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.6f), fontWeight = FontWeight.Normal)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(fallo.pregunta.respuestas[fallo.pregunta.respuestaCorrecta]), style = MaterialTheme.typography.bodyLarge, color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
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
                             Text(stringResource(R.string.solutions_play_again), color = RealMadridBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                             Text(stringResource(R.string.solutions_finalize), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
