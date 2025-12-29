package com.example.real_madrid_museo.ui.linea

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.example.real_madrid_museo.MainActivity
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleHistoricoScreen(email: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val progreso = EraManager.obtenerProgreso(context, email)
    val puzleCompleto = progreso == 1f

    var mostrarPantallaSorpresa by remember { mutableStateOf(false) }
    var tiempoRestante by remember { mutableIntStateOf(10) }

    // Pantalla de Sorpresa Temporizada
    if (mostrarPantallaSorpresa) {
        LaunchedEffect(Unit) {
            while (tiempoRestante > 0) {
                delay(1000)
                tiempoRestante--
            }
            mostrarPantallaSorpresa = false
            tiempoRestante = 10
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MadridBlue)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = MadridGold,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "¡ENHORABUENA!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MadridGold,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Has sido inscrito en el sorteo de una\nREAL MADRID EXPERIENCE",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Podrás vivir un entrenamiento del primer equipo desde dentro en Valdebebas.",
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = MadridGold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Esta pantalla se cerrará en $tiempoRestante...",
                    color = MadridGold.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Puzzle Histórico", fontWeight = FontWeight.Bold, color = MadridBlue) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MadridBlue)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mensaje superior condicional
                Text(
                    text = if (puzleCompleto) "¡PUZLE COMPLETADO!" 
                           else "Completa las 9 épocas en la Sala Histórica para desbloquear una sorpresa",
                    fontSize = 14.sp,
                    color = if (puzleCompleto) MadridGold else Color.Gray,
                    fontWeight = if (puzleCompleto) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                // REJILLA 3x3
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .border(2.dp, MadridBlue, RoundedCornerShape(8.dp)),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(9) { index ->
                        val era = listaEras[index]
                        val desbloqueada = EraManager.estaDesbloqueada(context, email, era.id)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (desbloqueada) Color.Transparent else Color.LightGray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (desbloqueada) {
                                Image(
                                    painter = painterResource(id = era.imagenPuzzleRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MadridBlue.copy(alpha = 0.3f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN SORPRESA
                AnimatedVisibility(
                    visible = puzleCompleto,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = {
                            enviarNotificacionSorteo(context, email)
                            mostrarPantallaSorpresa = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MadridGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = MadridBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "VER SORPRESA",
                            color = MadridBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

private fun enviarNotificacionSorteo(context: Context, email: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "sorteo_recompensa"
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Sorteos Museo",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de premios del museo"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 
        0, 
        intent, 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo_rm) // He cambiado madrid_logo por logo_rm que sí existe
        .setContentTitle("¡Inscrito en el Sorteo! ⚽")
        .setContentText("Usuario: $email. Te has registrado para la Real Madrid Experience.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    notificationManager.notify(1, notification)
}
