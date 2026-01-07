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
import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
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

    // Usamos listaEras directamente desde EraManager.kt
    val eras = remember { listaEras }
    var eraParaMostrar by remember { mutableStateOf<EraReal?>(null) }

    val pagerState = rememberPagerState(pageCount = { eras.size })
    var refreshKey by remember { mutableIntStateOf(0) }

    // Guardamos la máxima luz detectada para calcular el contraste
    var maxLuxDetected by remember { mutableFloatStateOf(0f) }

    val playUnlockSound = {
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.tech_click)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    val eraDiscoveredMsg = stringResource(R.string.era_toast_discovered)

    DisposableEffect(pagerState.currentPage, refreshKey) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val eraActual = eras[pagerState.currentPage]
                
                // Si ya está desbloqueada, no hacemos nada
                if (EraManager.estaDesbloqueada(context, email, eraActual.id)) return
                
                var detectarDesbloqueo = false
                
                // Lógica del sensor de proximidad (fallback)
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    val distance = event.values[0]
                    if (distance < (proximitySensor?.maximumRange ?: 5f) || distance == 0f) {
                        detectarDesbloqueo = true
                    }
                }
                
                // Lógica del sensor de luz mejorada por contraste
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val currentLux = event.values[0]
                    
                    // Actualizamos el máximo nivel de luz visto en esta sesión de la página
                    if (currentLux > maxLuxDetected) {
                        maxLuxDetected = currentLux
                    }
                    
                    // Detectamos desbloqueo por contraste:
                    // Si hay una caída significativa (ej. menos del 40% de la luz máxima previa)
                    // y el nivel de luz previo era lo suficientemente alto para notar el cambio.
                    val threshold = maxLuxDetected * 0.4f
                    if (maxLuxDetected > 10f && currentLux < threshold) {
                        detectarDesbloqueo = true
                    }
                }
                
                if (detectarDesbloqueo) {
                    EraManager.desbloquearEra(context, email, eraActual.id)
                    maxLuxDetected = 0f // Reseteamos para la siguiente era
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(150)
                    }
                    playUnlockSound()
                    Toast.makeText(context, eraDiscoveredMsg, Toast.LENGTH_SHORT).show()
                    refreshKey++
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White, MadridBlue.copy(alpha = 0.05f)))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = stringResource(R.string.history_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MadridBlue)
            Text(text = stringResource(R.string.history_instruction), style = MaterialTheme.typography.bodyMedium, color = MadridGold, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 45.dp), pageSpacing = 20.dp) { page ->
                val era = eras[page]
                val estaDesbloqueada = remember(page, refreshKey) { EraManager.estaDesbloqueada(context, email, era.id) }
                CartaEra(era = era, desbloqueada = estaDesbloqueada, onSaberMas = { eraParaMostrar = era })
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        IconButton(onClick = onBack, modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.history_back), tint = MadridBlue)
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
            Column(modifier = Modifier.fillMaxWidth()) {
                val alignment = if (era.id == 3 || era.id == 5) BiasAlignment(0f, -1f) else Alignment.Center
                Image(
                    painter = painterResource(id = era.imagenRes),
                    contentDescription = stringResource(era.tituloRes),
                    modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = alignment,
                    alpha = if (desbloqueada) 1f else 0.3f
                )
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(era.periodoRes), fontWeight = FontWeight.Bold, color = MadridGold, fontSize = 18.sp)
                    Text(text = stringResource(era.tituloRes), fontWeight = FontWeight.Black, color = MadridBlue, fontSize = 22.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (desbloqueada) stringResource(era.infoCortaRes) 
                               else stringResource(R.string.era_locked_title) + "\n" + stringResource(R.string.era_locked_desc), 
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
                            colors = ButtonDefaults.buttonColors(containerColor = MadridBlue, contentColor = Color.White), 
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.era_saber_mas), color = Color.White)
                        }
                    } else {
                        Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = MadridGold.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                    }
                }
            }
            Surface(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp), shape = CircleShape, color = if (desbloqueada) MadridGold else Color.Gray.copy(alpha = 0.8f), shadowElevation = 6.dp) {
                Icon(imageVector = if (desbloqueada) Icons.Default.LockOpen else Icons.Default.Lock, contentDescription = null, modifier = Modifier.padding(10.dp).size(24.dp), tint = Color.White)
            }
        }
    }
}

@Composable
fun DialogoInfoEra(era: EraReal, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isListening by remember { mutableStateOf(false) }
    
    val tituloStr = stringResource(era.tituloRes)
    val infoDetalladaStr = stringResource(era.infoDetalladaRes)
    val periodoStr = stringResource(era.periodoRes)

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val currentLocale = configuration.locales[0]
                tts?.language = currentLocale
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    AlertDialog(
        onDismissRequest = {
            tts?.stop()
            onDismiss()
        },
        confirmButton = {
            Button(
                onClick = {
                    tts?.stop()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MadridBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.era_close), color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tituloStr,
                        fontWeight = FontWeight.Black,
                        color = MadridBlue,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (isListening) {
                                tts?.stop()
                                isListening = false
                            } else {
                                tts?.speak(infoDetalladaStr, TextToSpeech.QUEUE_FLUSH, null, null)
                                isListening = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = stringResource(R.string.history_listen),
                            tint = MadridGold
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp).width(50.dp), thickness = 3.dp, color = MadridGold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val alignment = if (era.id == 3 || era.id == 5) BiasAlignment(0f, -1f) else Alignment.Center
                Image(
                    painter = painterResource(id = era.imagenRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = alignment
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Surface(
                    color = MadridBlue.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.era_chronicle),
                            fontWeight = FontWeight.ExtraBold,
                            color = MadridGold,
                            fontSize = 13.sp,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = infoDetalladaStr,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 26.sp,
                                letterSpacing = 0.2.sp
                            ),
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.era_period_label) + " " + periodoStr,
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    )
}
