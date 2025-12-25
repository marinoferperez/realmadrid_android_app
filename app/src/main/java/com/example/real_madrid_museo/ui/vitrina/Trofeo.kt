package com.example.real_madrid_museo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import com.example.real_madrid_museo.home.PerfilContent

// Colores (puedes importarlos de tu archivo Color.kt si prefieres)
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trofeo(
    trofeo: TrofeoInfo, // AHORA RECIBE EL TROFEO
    onBackClick: () -> Unit
) {
    // --- LÓGICA DE SENSORES (Igual que antes) ---
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        if (sensorManager != null) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        rotationX = it.values[0] * 2f
                        rotationY = it.values[1] * 2f
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            onDispose { sensorManager.unregisterListener(listener) }
        } else { onDispose { } }
    }

    // --- INTERFAZ GRÁFICA ---
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(trofeo.nombre, fontWeight = FontWeight.Bold, color = MadridBlue) }, // Nombre dinámico
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MadridBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TTS */ }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Audio", tint = MadridGold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(trofeo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MadridBlue, textAlign = TextAlign.Center)
            Text(trofeo.subTitulo, style = MaterialTheme.typography.titleMedium, color = MadridGold)

            Spacer(modifier = Modifier.height(40.dp))

            // --- ZONA INTERACTIVA (PARALLAX) ---
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                // Sombra
                Box(modifier = Modifier.size(100.dp, 20.dp).align(Alignment.BottomCenter)
                    .graphicsLayer { translationY = 140f; alpha = 0.2f }
                    .background(Color.Black, shape = RoundedCornerShape(50)))

                // IMAGEN DINÁMICA
                Image(
                    painter = painterResource(id = trofeo.imagenRes), // Aquí carga la imagen específica
                    contentDescription = trofeo.nombre,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        rotationZ = -rotationX
                        translationX = -rotationX * 10
                        translationY = rotationY * 10
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- DESCRIPCIÓN DINÁMICA ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MadridBlue.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Información Histórica", fontWeight = FontWeight.Bold, color = MadridBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = trofeo.descripcion, // Descripción específica
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrofeoPreview() {
    // Creamos un trofeo "falso" solo para ver cómo queda el diseño
    val trofeoEjemplo = TrofeoInfo(
        nombre = "Champions League",
        subTitulo = "La Decimoquinta",
        descripcion = "Ganada en Wembley contra el Dortmund. Carvajal y Vinicius marcaron los goles de la gloria.",
        imagenRes = R.drawable.copa_champions // Asegúrate de que esta imagen existe
    )

    // Llamamos a tu pantalla pasándole este trofeo
    Trofeo(
        trofeo = trofeoEjemplo,
        onBackClick = { /* No hace nada en la preview */ }
    )
}

// 1. Clase de datos para definir qué tiene cada trofeo
data class TrofeoInfo(
    val nombre: String,
    val subTitulo: String,
    val descripcion: String,
    val imagenRes: Int // ID de la imagen en drawable
)

// 2. La lista con TUS 7 trofeos
val listaTrofeos = listOf(
    TrofeoInfo(
        "Champions League", "La Decimoquinta",
        "El trofeo más prestigioso a nivel de clubes. El Real Madrid es el rey absoluto de esta competición.",
        R.drawable.copa_champions // Asegúrate de tener esta imagen
    ),
    TrofeoInfo(
        "Copa de Europa (Antigua)", "El inicio de la Leyenda",
        "Las primeras 6 copas ganadas por Gento, Di Stéfano y Puskás que forjaron la historia.",
        R.drawable.copa_champions_old // Necesitarás esta imagen
    ),
    TrofeoInfo(
        "La Liga", "Regularidad Histórica",
        "El trofeo que premia al mejor equipo de España tras 38 jornadas de lucha.",
        R.drawable.copa_liga // Necesitarás esta imagen
    ),
    TrofeoInfo(
        "Copa del Rey", "El torneo del KO",
        "Competición histórica del fútbol español. Una de las más emocionantes.",
        R.drawable.copa_rey // Necesitarás esta imagen
    ),
    TrofeoInfo(
        "Mundial de Clubes", "Campeones del Mundo",
        "El título que acredita al Real Madrid como el mejor equipo de todo el planeta.",
        R.drawable.copa_mundialito // Necesitarás esta imagen
    ),
    TrofeoInfo(
        "Supercopa de Europa", "Supercampeones",
        "Duelo entre el campeón de la Champions y el de la Europa League.",
        R.drawable.copa_super_uefa // Necesitarás esta imagen
    ),
    TrofeoInfo(
        "Supercopa de España", "El primer título",
        "Torneo que enfrenta a los mejores equipos de la temporada española.",
        R.drawable.copa_super_espana // Necesitarás esta imagen
    )
)