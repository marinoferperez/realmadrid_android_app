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
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@Composable
fun SalaHistorica(email: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val proximitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val accelSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val gyroSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    val eras = remember { listaEras }
    var eraParaMostrar by remember { mutableStateOf<EraReal?>(null) }
    var eraParaVerPuzzle by remember { mutableStateOf<EraReal?>(null) }
    
    val pagerState = rememberPagerState(pageCount = { eras.size })
    var refreshKey by remember { mutableIntStateOf(0) }
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
                if (EraManager.estaDesbloqueada(context, email, eraActual.id)) return
                
                var detectarDesbloqueo = false
                
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    val distance = event.values[0]
                    if (distance < (proximitySensor?.maximumRange ?: 5f) || distance == 0f) detectarDesbloqueo = true
                }
                
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val currentLux = event.values[0]
                    if (currentLux > maxLuxDetected) maxLuxDetected = currentLux
                    val threshold = maxLuxDetected * 0.4f
                    if (maxLuxDetected > 10f && currentLux < threshold) detectarDesbloqueo = true
                }

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val accelMag = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - 9.81f
                    if (accelMag > 13f) detectarDesbloqueo = true
                }
                
                if (detectarDesbloqueo) {
                    EraManager.desbloquearEra(context, email, eraActual.id)
                    maxLuxDetected = 0f
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                    else vibrator.vibrate(150)
                    playUnlockSound()
                    Toast.makeText(context, eraDiscoveredMsg, Toast.LENGTH_SHORT).show()
                    refreshKey++
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
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
                CartaEra(
                    era = era, 
                    desbloqueada = estaDesbloqueada, 
                    onSaberMas = { eraParaMostrar = era },
                    onVerPuzzle = { eraParaVerPuzzle = era }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        IconButton(onClick = onBack, modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.history_back), tint = MadridBlue)
        }

        eraParaMostrar?.let { era -> DialogoInfoEra(era = era, onDismiss = { eraParaMostrar = null }) }
        
        eraParaVerPuzzle?.let { era ->
            VisualizadorPuzzleGiroscopio(
                era = era,
                sensorManager = sensorManager,
                gyroSensor = gyroSensor,
                onDismiss = { eraParaVerPuzzle = null }
            )
        }
    }
}

@Composable
fun CartaEra(era: EraReal, desbloqueada: Boolean, onSaberMas: () -> Unit, onVerPuzzle: () -> Unit) {
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onSaberMas, 
                                modifier = Modifier.weight(1f), 
                                colors = ButtonDefaults.buttonColors(containerColor = MadridBlue, contentColor = Color.White), 
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.era_saber_mas), color = Color.White, fontSize = 12.sp)
                            }
                            
                            FilledIconButton(
                                onClick = onVerPuzzle,
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MadridGold)
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = stringResource(R.string.era_puzzle_view_btn), tint = Color.White)
                            }
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
fun VisualizadorPuzzleGiroscopio(
    era: EraReal,
    sensorManager: SensorManager,
    gyroSensor: Sensor?,
    onDismiss: () -> Unit
) {
    var timer by remember { mutableIntStateOf(10) }
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    
    // Sensibilidad del giroscopio
    val sensitivity = 1.5f

    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
        onDismiss()
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || event.sensor.type != Sensor.TYPE_GYROSCOPE) return
                // Los valores son velocidad angular rad/s
                rotationY += event.values[1] * sensitivity
                rotationX += event.values[0] * sensitivity
                
                // Limitamos la rotación para que no sea infinita
                rotationX = rotationX.coerceIn(-30f, 30f)
                rotationY = rotationY.coerceIn(-30f, 30f)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.era_puzzle_view_title),
                color = MadridGold,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )
            Text(
                text = stringResource(R.string.era_puzzle_view_instruction),
                color = Color.White,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.DarkGray)
            ) {
                // Aquí simulamos el efecto de "ventana" usando la rotación del móvil
                Image(
                    painter = painterResource(id = era.imagenPuzzleRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (rotationY * 5).dp, y = (rotationX * 5).dp)
                        .scale(1.5f), // Escalamos para que al movernos veamos distintas partes
                    contentScale = ContentScale.Crop
                )
                
                // Marco de la "mira"
                Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                    radius = 500f
                )))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Indicador de tiempo
            Surface(
                color = MadridBlue,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = timer.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
        
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd).padding(30.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
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
