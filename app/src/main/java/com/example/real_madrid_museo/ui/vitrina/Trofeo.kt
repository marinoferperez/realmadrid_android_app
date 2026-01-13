package com.example.real_madrid_museo.ui.vitrina

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.sqrt

// --- DATOS Y LISTA DE TROFEOS ---
data class TrofeoInfo(
    val nombre: String,
    val subTitulo: String,
    val descripcion: String,
    val infoExtra: String,
    val imagenRes: Int
)

val listaTrofeos = listOf(
    TrofeoInfo(
        "Champions League", "La Decimoquinta",
        "El trofeo más prestigioso a nivel de clubes. El Real Madrid es el rey absoluto de esta competición.",
        "El Real Madrid ganó su primera Copa de Europa en 1956. Desde entonces, ha establecido un idilio con la competición, destacando las 5 copas consecutivas de la era Di Stéfano y las 3 seguidas de la era Zidane.",
        R.drawable.copa_champions
    ),
    TrofeoInfo(
        "Copa de Europa (Antigua)", "El inicio de la Leyenda",
        "Las primeras copas ganadas por Gento, Di Stéfano y Puskás. Forja del mito madridista.",
        "Este trofeo original, con un diseño diferente a la 'Orejona' actual, fue otorgado en propiedad al Real Madrid tras ganar seis de las primeras ediciones. Jugadores legendarios como Alfredo Di Stéfano, Paco Gento (el único jugador con 6 copas) y Ferenc Puskás forjaron aquí el prestigio mundial del que goza el club hoy en día.",
        R.drawable.copa_champions_old
    ),
    TrofeoInfo(
        "La Liga", "Regularidad Histórica",
        "El trofeo que premia al mejor equipo de España tras 38 jornadas.",
        "Con más de 36 títulos en sus vitrinas, el Real Madrid es el dominador histórico del campeonato español. Destacan hitos como la 'Liga de los 100 puntos' conseguida en la temporada 2011-12 o la racha de las cinco ligas seguidas de la 'Quinta del Buitre' en los años 80, demostrando una consistencia inigualable.",
        R.drawable.copa_liga
    ),
    TrofeoInfo(
        "Copa del Rey", "El torneo del KO",
        "La competición más antigua del fútbol español.",
        "La competición más antigua de España ha dejado momentos grabados en la memoria madridista, como la final de 2011 en Mestalla con el gol de cabeza de Cristiano Ronaldo, o la carrera icónica de Gareth Bale en 2014. Es un trofeo que celebra la pasión y la resistencia en eliminatorias directas.",
        R.drawable.copa_rey
    ),
    TrofeoInfo(
        "Mundial de Clubes", "Campeones del Mundo",
        "El título que acredita al Real Madrid como el mejor equipo del planeta.",
        "Sumando las antiguas Copas Intercontinentales y los actuales Mundiales de Clubes de la FIFA, el Real Madrid es el club con más títulos mundiales del planeta. Este trofeo acredita que el equipo ha vencido a los campeones de todos los continentes, luciendo con orgullo el parche dorado en su camiseta.",
        R.drawable.copa_mundialito
    ),
    TrofeoInfo(
        "Supercopa de Europa", "Supercampeones",
        "El duelo que decide quién manda en el continente.",
        "Este título enfrenta anualmente al campeón de la Champions League contra el ganador de la Europa League. El Real Madrid ha demostrado su hegemonía europea ganando este trofeo en numerosas ocasiones, consolidando su estatus como el equipo a batir en el viejo continente al inicio de cada temporada.",
        R.drawable.copa_super_uefa
    ),
    TrofeoInfo(
        "Supercopa de España", "El primer título",
        "Torneo que enfrenta a los mejores equipos de la temporada española.",
        "Tradicionalmente enfrentaba al campeón de Liga y Copa, pero desde 2020 se disputa en un emocionante formato de 'Final Four'. El Real Madrid ha dominado este nuevo sistema, ofreciendo grandes actuaciones y Clásicos memorables en sedes internacionales, siendo a menudo el primer trofeo que marca el rumbo de una temporada exitosa.",
        R.drawable.copa_super_espana
    )
)

// Colores
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trofeo(
    trofeo: TrofeoInfo,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) } // Estado para el diálogo

    LaunchedEffect(Unit) {
        // Buscamos el índice de este trofeo en la lista oficial
        val indice = listaTrofeos.indexOf(trofeo)
        if (indice != -1) {
            com.example.real_madrid_museo.ui.vitrina.TrofeoManager.marcarTrofeoVisto(context, indice)
        }
    }

    // --- 1. CONFIGURACIÓN DEL AUDIO (TTS) ---
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // --- 2. CONFIGURACIÓN DEL SENSOR DE MOVIMIENTO (ACELERÓMETRO) ---
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    var estaCelebrando by remember { mutableStateOf(false) }

    // Animación para el efecto de "Latido" de la copa cuando celebras
    val escalaAnimada by animateFloatAsState(
        targetValue = if (estaCelebrando) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "zoomCopa"
    )

    // Lógica del Sensor
    DisposableEffect(Unit) {
        if (sensorManager != null) {
            val accelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val listener = object : SensorEventListener {
                // Umbral de fuerza para detectar la agitación (Shake)
                // 9.8 es la gravedad, así que buscamos algo mayor (ej: 12 o 15)
                val umbralShake = 12f
                var ultimoTiempo = 0L

                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        // Calculamos la magnitud total de la aceleración
                        // Fórmula física: raíz cuadrada de (x² + y² + z²)
                        val aceleracionTotal = sqrt((x*x + y*y + z*z).toDouble()).toFloat()

                        // Si la aceleración supera la gravedad + el umbral... ¡AGITACIÓN DETECTADA!
                        // Y ponemos un pequeño retardo (500ms) para que no parpadee a lo loco
                        val tiempoActual = System.currentTimeMillis()
                        if (aceleracionTotal > umbralShake && (tiempoActual - ultimoTiempo > 1000)) {
                            ultimoTiempo = tiempoActual
                            estaCelebrando = true

                            // Vibración para dar feedback físico
                            vibrarMovil(context)
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, accelerometro, SensorManager.SENSOR_DELAY_UI)
            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            onDispose { }
        }
    }

    // Temporizador para apagar la celebración automáticamente después de 2 segundos
    LaunchedEffect(estaCelebrando) {
        if (estaCelebrando) {
            delay(2000) // La fiesta dura 2 segundos
            estaCelebrando = false
        }
    }

    // --- 3. INTERFAZ GRÁFICA ---
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(trofeo.nombre, fontWeight = FontWeight.Bold, color = MadridBlue) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MadridBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        tts?.speak(trofeo.descripcion, TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar audio", tint = MadridGold)
                    }
                }
            )
        }
    ) { padding ->
        // Contenedor principal
        Box(modifier = Modifier.fillMaxSize()) {

            // Fondo animado: Si celebra, se pone dorado; si no, blanco
            val colorFondo by animateColorAsState(
                if (estaCelebrando) MadridGold.copy(alpha = 0.3f) else Color.White,
                label = "colorFondo"
            )

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(colorFondo),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Título condicional
                if (estaCelebrando) {
                    Text("¡CAMPEONES!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MadridBlue)
                } else {
                    Text(trofeo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MadridBlue, textAlign = TextAlign.Center)
                }

                Text(trofeo.subTitulo, style = MaterialTheme.typography.titleMedium, color = MadridGold)

                Spacer(modifier = Modifier.height(40.dp))

                // --- ZONA DE IMAGEN CON ZOOM ---
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                    // Sombra inferior
                    Box(modifier = Modifier.size(100.dp, 20.dp).align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(50)))

                    // IMAGEN DEL TROFEO QUE RESPONDE AL SHAKE
                    Image(
                        painter = painterResource(id = trofeo.imagenRes),
                        contentDescription = trofeo.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(escalaAnimada) // <--- Aquí aplicamos el Zoom del sensor
                    )
                }

                // Instrucción para el usuario (si no está celebrando)
                if (!estaCelebrando) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("💡 ¡Agita el móvil para celebrar!", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- TARJETA DE INFORMACIÓN ---
                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MadridBlue.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Información Histórica", fontWeight = FontWeight.Bold, color = MadridBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trofeo.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            color = Color.DarkGray
                        )
                    }
                }

                // --- BOTÓN DE MÁS INFORMACIÓN ---
                OutlinedButton(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MadridBlue),
                    border = BorderStroke(1.dp, MadridBlue)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Más información histórica")
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // Confeti simulado (Overlay simple)
            if (estaCelebrando) {
                // Aquí podrías poner una imagen de confeti transparente encima si tuvieras
                // Por ahora, el cambio de color y zoom es suficiente feedback
            }

            // --- LÓGICA DEL DIÁLOGO EMERGENTE ---
            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    title = { Text(trofeo.nombre, fontWeight = FontWeight.Bold, color = MadridBlue) },
                    text = { Text(trofeo.infoExtra, textAlign = TextAlign.Justify) },
                    confirmButton = {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Cerrar", color = MadridGold, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

// Función auxiliar para vibrar compatible con versiones nuevas y viejas de Android
fun vibrarMovil(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(500)
    }
}

@Preview(showBackground = true)
@Composable
fun TrofeoPreview() {
    val trofeoEjemplo = TrofeoInfo("Champions", "La 15", "Desc...", "Info...", R.drawable.copa_champions)
    Trofeo(trofeo = trofeoEjemplo, onBackClick = {})
}