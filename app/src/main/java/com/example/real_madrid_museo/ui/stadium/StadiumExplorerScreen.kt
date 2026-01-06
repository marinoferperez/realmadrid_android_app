package com.example.real_madrid_museo.ui.stadium

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.ScaleGestureDetector
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.real_madrid_museo.R
import com.google.ar.core.Config
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlin.math.abs

// 🎨 Paleta de Colores
private val MadridBlue = Color(0xFF002D72)
private val MadridGold = Color(0xFFFEBE10)
private val DeepNight = Color(0xFF000814)

data class BernabeuFact(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val angle: Float
)

@Composable
fun StadiumExplorerScreen(assetPath: String = "models/bernabeu.glb") {
    var isARMode by remember { mutableStateOf(false) }

    val bernabeuHistory = listOf(
        BernabeuFact(0, "INAUGURACIÓN (1947)", "Se inauguró el 14 de diciembre de 1947 como Nuevo Estadio Chamartín.", R.drawable.foto_1947, 0f),
        BernabeuFact(1, "LA ÉPOCA DE ORO (1950s)", "En 1954 alcanzó un aforo de 125.000 espectadores e iluminación eléctrica.", R.drawable.foto_1955, 60f),
        BernabeuFact(2, "MUNDIAL ESPAÑA '82", "Remodelación total: gran cubierta y modernización de accesos.", R.drawable.foto_finales, 120f),
        BernabeuFact(3, "LAS TORRES DE LOS 90", "Aparición de las cuatro icónicas torres y el tercer anfiteatro.", R.drawable.foto_torres, 180f),
        BernabeuFact(4, "ESTADIO DE ÉLITE (2007)", "La UEFA lo certificó como Estadio de Élite para finales continentales.", R.drawable.foto_elite, 240f),
        BernabeuFact(5, "EL NUEVO BERNABÉU", "Piel de acero, techo retráctil y césped hipogeo.", R.drawable.foto_nuevo, 300f)
    )

    Box(Modifier.fillMaxSize()) {
        if (!isARMode) {
            CompassRouletteView(bernabeuHistory, onEnterAR = { isARMode = true })
        } else {
            ARStadiumView(assetPath, onExitAR = { isARMode = false })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompassRouletteView(history: List<BernabeuFact>, onEnterAR: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var smoothAzimuth by remember { mutableStateOf(0f) }
    var lastAzimuth by remember { mutableStateOf(0f) }
    var initialOffset by remember { mutableStateOf<Float?>(null) }

    // Lista de IDs visitados
    val visitedFacts = remember { mutableStateListOf<Int>() }
    val isUnlocked = visitedFacts.size == history.size

    // --- SENSOR ---
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ORIENTATION) {
                    val rawAzimuth = event.values[0]
                    if (initialOffset == null) initialOffset = rawAzimuth
                    val calibratedAzimuth = rawAzimuth - (initialOffset ?: 0f)

                    var delta = calibratedAzimuth - lastAzimuth
                    if (delta > 180) delta -= 360 else if (delta < -180) delta += 360
                    smoothAzimuth += delta
                    lastAzimuth = calibratedAzimuth
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    // --- LÓGICA DE DETECCIÓN MEJORADA ---
    // Se ejecuta cada vez que 'lastAzimuth' cambia. Es mucho más preciso.
    LaunchedEffect(lastAzimuth) {
        val normalized = (lastAzimuth % 360 + 360) % 360

        history.forEach { fact ->
            // Calculamos la distancia angular al hecho
            val diff = abs(normalized - fact.angle)
            val realDiff = if (diff > 180) 360 - diff else diff

            // Si estamos cerca (margen de 15 grados) y NO lo hemos visitado aún -> Añadir
            if (realDiff < 15 && !visitedFacts.contains(fact.id)) {
                visitedFacts.add(fact.id)
            }
        }
    }

    // Cálculo visual normalizado
    val normalizedAzimuth = (lastAzimuth % 360 + 360) % 360
    val activeFact = history.minByOrNull { fact ->
        val diff = abs(normalizedAzimuth - fact.angle)
        if (diff > 180) 360 - diff else diff
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MadridBlue, DeepNight, Color.Black))),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. CABECERA ---
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SANTIAGO BERNABÉU", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("BRÚJULA DIGITAL", color = MadridGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)

            Spacer(Modifier.height(10.dp))

            // Barra de progreso (Puntos superiores)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                history.forEach { fact ->
                    val isVisited = visitedFacts.contains(fact.id)
                    Box(
                        Modifier
                            .size(if(isVisited) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isVisited) MadridGold else Color.White.copy(0.2f))
                    )
                }
            }
        }

        // --- 2. ZONA CENTRAL (RULETA) ---
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(y = (-50).dp),
            contentAlignment = Alignment.Center
        ) {
            // Fondo tenue
            Canvas(Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(MadridGold, radius = size.minDimension / 2, style = Stroke(1f))
            }

            // HITOS (Marcas alrededor)
            history.forEach { fact ->
                // ¿Está activo ahora mismo (estoy encima)?
                val diff = abs(normalizedAzimuth - fact.angle)
                val realDiff = if (diff > 180) 360 - diff else diff
                val isCurrentlyActive = realDiff < 15

                // ¿Ha sido visitado alguna vez?
                val isVisited = visitedFacts.contains(fact.id)

                Box(
                    Modifier.fillMaxSize().graphicsLayer { rotationZ = fact.angle },
                    contentAlignment = Alignment.TopCenter
                ) {
                    // LÓGICA VISUAL DE LA RALLA
                    Box(
                        Modifier
                            // Si está activo es más ancho (4dp), si está visitado normal (2.5dp), si no fino (1.5dp)
                            .size(if (isCurrentlyActive) 4.dp else if (isVisited) 2.5.dp else 1.5.dp, 25.dp)
                            // Si está activo O visitado -> ORO. Si no -> Blanco transparente
                            .background(
                                if (isCurrentlyActive || isVisited) MadridGold else Color.White.copy(0.2f),
                                RoundedCornerShape(2.dp)
                            )
                            // Efecto de brillo si está activo
                            .shadow(if (isCurrentlyActive) 10.dp else 0.dp, spotColor = MadridGold)
                    )
                }
            }

            // Puntero (Se mueve)
            Box(
                Modifier.fillMaxSize().graphicsLayer { rotationZ = smoothAzimuth },
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .offset(y = (-5).dp)
                        .background(Color.White, CircleShape)
                        .drawBehind {
                            drawCircle(MadridGold, radius = size.minDimension * 0.8f, alpha = 0.5f)
                            drawCircle(MadridGold.copy(0.2f), radius = size.minDimension * 2f)
                        }
                )
            }

            // NÚCLEO CENTRAL (BOTÓN / CANDADO)
            AnimatedContent(targetState = isUnlocked, label = "Unlock") { unlocked ->
                if (unlocked) {
                    // DESBLOQUEADO
                    Surface(
                        onClick = onEnterAR,
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MadridGold,
                        shadowElevation = 20.dp,
                        border = BorderStroke(2.dp, Color.White.copy(0.5f))
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MadridBlue, modifier = Modifier.size(36.dp))
                            Text("3D VIEW", color = MadridBlue, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                } else {
                    // BLOQUEADO
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = DeepNight,
                        border = BorderStroke(1.dp, MadridGold.copy(0.3f)),
                        shadowElevation = 10.dp
                    ) {
                        Column(
                            Modifier.fillMaxSize().padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MadridGold.copy(0.5f), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "EXPLORA (${visitedFacts.size}/6)\nPARA ABRIR",
                                color = MadridGold.copy(0.8f),
                                fontSize = 9.sp,
                                lineHeight = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // --- 3. TARJETA INFO ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        ) {
            AnimatedContent(
                targetState = activeFact,
                transitionSpec = {
                    (fadeIn(tween(300)) + scaleIn(initialScale = 0.95f))
                        .togetherWith(fadeOut(tween(300)) + scaleOut(targetScale = 1.05f))
                },
                label = "Card"
            ) { fact ->
                fact?.let {
                    // Si el hecho ha sido visitado, mostramos el icono de check
                    val isCollected = visitedFacts.contains(it.id)

                    Card(
                        modifier = Modifier.fillMaxWidth(0.92f).height(160.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(MadridGold.copy(0.5f), Color.Transparent)))
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = it.imageRes),
                                contentDescription = null,
                                modifier = Modifier.weight(0.38f).fillMaxHeight().clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(0.62f).padding(18.dp).verticalScroll(rememberScrollState())) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(it.title, color = MadridGold, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                                    if (isCollected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MadridGold, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(it.description, color = Color.White.copy(0.9f), fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ARStadiumView(assetPath: String, onExitAR: () -> Unit) {
    val context = LocalContext.current
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    val scaleDetector = remember {
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                modelNode?.let { it.scale = it.scale * detector.scaleFactor.coerceIn(0.96f, 1.04f) }
                return true
            }
        })
    }
    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                ARSceneView(ctx).apply {
                    configureSession { session, config ->
                        config.planeFindingMode = Config.PlaneFindingMode.DISABLED
                        config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                        session.configure(config)
                    }
                    modelLoader.createModelInstance(assetPath)?.let { instance ->
                        val node = ModelNode(modelInstance = instance).apply {
                            scale = scale * 0.02f
                            position = Position(0f, -0.5f, -2f)
                        }
                        addChildNode(node)
                        modelNode = node
                    }
                    setOnTouchListener { _, event ->
                        scaleDetector.onTouchEvent(event)
                        true
                    }
                }
            }
        )
        IconButton(
            onClick = onExitAR,
            modifier = Modifier.padding(25.dp).align(Alignment.TopEnd).background(MadridGold, CircleShape)
        ) { Icon(Icons.Default.Close, contentDescription = null, tint = MadridBlue) }
    }
}