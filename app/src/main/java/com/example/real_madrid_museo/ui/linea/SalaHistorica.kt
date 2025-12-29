package com.example.real_madrid_museo.ui.linea

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold

@Composable
fun SalaHistorica(email: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val proximitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    var eraParaMostrar by remember { mutableStateOf<EraReal?>(null) }
    val pagerState = rememberPagerState(pageCount = { listaEras.size })
    var refreshKey by remember { mutableIntStateOf(0) }

    // Estado para seguimiento de luz ambiental (para lógica adaptativa)
    var maxLuxDetected by remember { mutableFloatStateOf(0f) }

    val playUnlockSound = {
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.tech_click)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    DisposableEffect(pagerState.currentPage, refreshKey) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val eraActual = listaEras[pagerState.currentPage]
                if (EraManager.estaDesbloqueada(context, email, eraActual.id)) return

                var detectarDesbloqueo = false

                // 1. PROXIMIDAD (Para dispositivos con sensor físico real)
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    val distance = event.values[0]
                    if (distance < (proximitySensor?.maximumRange ?: 5f) || distance == 0f) {
                        detectarDesbloqueo = true
                    }
                }

                // 2. LUZ ADAPTATIVA (Solución para el problema del brillo de pantalla)
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val currentLux = event.values[0]
                    
                    // Actualizamos el nivel de luz "ambiente" (con la pantalla encendida)
                    if (currentLux > maxLuxDetected) {
                        maxLuxDetected = currentLux
                    }

                    // LÓGICA: Si la luz cae por debajo del 15% del máximo detectado recientemente
                    // O si cae por debajo de un umbral de seguridad (3.0 lux), es que se ha tapado.
                    val threshold = (maxLuxDetected * 0.15f).coerceAtLeast(3.0f)
                    
                    if (currentLux < threshold && maxLuxDetected > 5f) {
                        detectarDesbloqueo = true
                    }
                }

                if (detectarDesbloqueo) {
                    EraManager.desbloquearEra(context, email, eraActual.id)
                    maxLuxDetected = 0f // Reiniciamos para la siguiente carta

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(150)
                    }

                    playUnlockSound()
                    Toast.makeText(context, "¡Época descubierta!", Toast.LENGTH_SHORT).show()
                    refreshKey++
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.White, MadridBlue.copy(alpha = 0.05f)))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = "SALA HISTÓRICA", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MadridBlue)
            Text(text = "Tapa el sensor superior para revelar", style = MaterialTheme.typography.bodyMedium, color = MadridGold, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 45.dp),
                pageSpacing = 20.dp
            ) { page ->
                val era = listaEras[page]
                val estaDesbloqueada = remember(page, refreshKey) { EraManager.estaDesbloqueada(context, email, era.id) }
                CartaEra(era = era, desbloqueada = estaDesbloqueada, onSaberMas = { eraParaMostrar = era })
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        IconButton(onClick = onBack, modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MadridBlue)
        }

        eraParaMostrar?.let { era -> DialogoInfoEra(era = era, onDismiss = { eraParaMostrar = null }) }
    }
}

@Composable
fun CartaEra(era: EraReal, desbloqueada: Boolean, onSaberMas: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(500.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(if (desbloqueada) 12.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Image(
                    painter = painterResource(id = era.imagenRes),
                    contentDescription = era.titulo,
                    modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = if (desbloqueada) 1f else 0.3f
                )

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = era.periodo, fontWeight = FontWeight.Bold, color = MadridGold, fontSize = 18.sp)
                    Text(text = era.titulo, fontWeight = FontWeight.Black, color = MadridBlue, fontSize = 22.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (desbloqueada) era.infoCorta
                        else "Contenido Bloqueado.\n¡Cubre el sensor superior para limpiar el cristal de la historia!",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = if (desbloqueada) Color.DarkGray else Color.Gray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (desbloqueada) {
                        Button(
                            onClick = onSaberMas,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MadridBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saber más")
                        }
                    } else {
                        Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = MadridGold.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                    }
                }
            }
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                shape = CircleShape,
                color = if (desbloqueada) MadridGold else Color.Gray.copy(alpha = 0.8f),
                shadowElevation = 6.dp
            ) {
                Icon(imageVector = if (desbloqueada) Icons.Default.LockOpen else Icons.Default.Lock, contentDescription = null, modifier = Modifier.padding(10.dp).size(24.dp), tint = Color.White)
            }
        }
    }
}

@Composable
fun DialogoInfoEra(era: EraReal, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MadridBlue), shape = RoundedCornerShape(12.dp)) {
                Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Column {
                Text(text = era.titulo, fontWeight = FontWeight.Black, color = MadridBlue, fontSize = 24.sp)
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp).width(50.dp), thickness = 3.dp, color = MadridGold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Image(painter = painterResource(id = era.imagenRes), contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "RESUMEN HISTÓRICO", fontWeight = FontWeight.Bold, color = MadridGold, fontSize = 14.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = era.infoDetallada, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray, lineHeight = 24.sp, textAlign = TextAlign.Justify)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    )
}
