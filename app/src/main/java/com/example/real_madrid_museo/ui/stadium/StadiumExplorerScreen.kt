package com.example.real_madrid_museo.ui.stadium

import android.app.Activity
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ViewInAr
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

// Modificado para usar Int (Recursos) en lugar de String directo
data class BernabeuFact(
    val id: Int,
    val titleRes: Int,       // ID del recurso de string
    val descriptionRes: Int, // ID del recurso de string
    val imageRes: Int,
    val angle: Float
)

@Composable
fun StadiumExplorerScreen(assetPath: String = "models/bernabeu.glb") {
    val context = LocalContext.current
    var showInstructions by remember { mutableStateOf(true) }

    // Al quitar el botón, el idioma se hereda automáticamente del Contexto de la App
    if (showInstructions) {
        StadiumInstructionsScreen(
            onStart = { showInstructions = false },
            onBack = { (context as? Activity)?.finish() }
        )
    } else {
        StadiumExplorerContent(assetPath)
    }
}

@Composable
fun StadiumExplorerContent(assetPath: String) {
    val context = LocalContext.current
    var isARMode by remember { mutableStateOf(false) }

    // Definimos los datos usando R.string para que se traduzcan solos
    val bernabeuHistory = remember {
        listOf(
            BernabeuFact(0, R.string.stadium_fact_1_title, R.string.stadium_fact_1_desc, R.drawable.foto_1947, 0f),
            BernabeuFact(1, R.string.stadium_fact_2_title, R.string.stadium_fact_2_desc, R.drawable.foto_1955, 60f),
            BernabeuFact(2, R.string.stadium_fact_3_title, R.string.stadium_fact_3_desc, R.drawable.foto_finales, 120f),
            BernabeuFact(3, R.string.stadium_fact_4_title, R.string.stadium_fact_4_desc, R.drawable.foto_torres, 180f),
            BernabeuFact(4, R.string.stadium_fact_5_title, R.string.stadium_fact_5_desc, R.drawable.foto_elite, 240f),
            BernabeuFact(5, R.string.stadium_fact_6_title, R.string.stadium_fact_6_desc, R.drawable.foto_nuevo, 300f)
        )
    }

    Box(Modifier.fillMaxSize()) {
        if (!isARMode) {
            CompassRouletteView(bernabeuHistory, onEnterAR = { isARMode = true })

            // Botón de salir (Igual que en SalaHistorica)
            IconButton(
                onClick = { (context as? Activity)?.finish() },
                modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.btn_back), tint = Color.White)
            }
        } else {
            ARStadiumView(assetPath, onExitAR = { isARMode = false })
        }
    }
}

@Composable
fun StadiumInstructionsScreen(onStart: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, MadridGold),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.stadium_explorer_title).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MadridGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    StadiumInstructionItem(
                        icon = Icons.Default.Explore,
                        title = stringResource(R.string.stadium_instr_1_title),
                        desc = stringResource(R.string.stadium_instr_1_desc)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    StadiumInstructionItem(
                        icon = Icons.Default.LockOpen,
                        title = stringResource(R.string.stadium_instr_2_title),
                        desc = stringResource(R.string.stadium_instr_2_desc)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    StadiumInstructionItem(
                        icon = Icons.Default.ViewInAr,
                        title = stringResource(R.string.stadium_instr_3_title),
                        desc = stringResource(R.string.stadium_instr_3_desc)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = MadridGold),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.btn_start_visit), color = MadridBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MadridGold)
                    ) {
                        Text(stringResource(R.string.btn_back), color = MadridGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StadiumInstructionItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MadridGold.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = MadridGold, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
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
    val visitedFacts = remember { mutableStateListOf<Int>() }
    val isUnlocked = visitedFacts.size == history.size

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

    LaunchedEffect(lastAzimuth) {
        val normalized = (lastAzimuth % 360 + 360) % 360
        history.forEach { fact ->
            val diff = abs(normalized - fact.angle)
            val realDiff = if (diff > 180) 360 - diff else diff
            if (realDiff < 15 && !visitedFacts.contains(fact.id)) {
                visitedFacts.add(fact.id)
            }
        }
    }

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
        // Cabecera
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.stadium_name).uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text(stringResource(R.string.stadium_compass_subtitle).uppercase(), color = MadridGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                history.forEach { fact ->
                    val isVisited = visitedFacts.contains(fact.id)
                    Box(Modifier.size(if(isVisited) 8.dp else 6.dp).clip(CircleShape).background(if (isVisited) MadridGold else Color.White.copy(0.2f)))
                }
            }
        }

        // Ruleta Central
        Box(
            modifier = Modifier.size(300.dp).offset(y = (-50).dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize().alpha(0.15f)) {
                drawCircle(MadridGold, radius = size.minDimension / 2, style = Stroke(1f))
            }

            history.forEach { fact ->
                val diff = abs(normalizedAzimuth - fact.angle)
                val realDiff = if (diff > 180) 360 - diff else diff
                val isCurrentlyActive = realDiff < 15
                val isVisited = visitedFacts.contains(fact.id)

                Box(Modifier.fillMaxSize().graphicsLayer { rotationZ = fact.angle }, contentAlignment = Alignment.TopCenter) {
                    Box(
                        Modifier
                            .size(if (isCurrentlyActive) 4.dp else if (isVisited) 2.5.dp else 1.5.dp, 25.dp)
                            .background(if (isCurrentlyActive || isVisited) MadridGold else Color.White.copy(0.2f), RoundedCornerShape(2.dp))
                            .shadow(if (isCurrentlyActive) 10.dp else 0.dp, spotColor = MadridGold)
                    )
                }
            }

            Box(Modifier.fillMaxSize().graphicsLayer { rotationZ = smoothAzimuth }, contentAlignment = Alignment.TopCenter) {
                Box(Modifier.size(22.dp).offset(y = (-5).dp).background(Color.White, CircleShape).drawBehind {
                    drawCircle(MadridGold, radius = size.minDimension * 0.8f, alpha = 0.5f)
                    drawCircle(MadridGold.copy(0.2f), radius = size.minDimension * 2f)
                })
            }

            AnimatedContent(targetState = isUnlocked, label = "Unlock") { unlocked ->
                if (unlocked) {
                    Surface(
                        onClick = onEnterAR,
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MadridGold,
                        shadowElevation = 20.dp,
                        border = BorderStroke(2.dp, Color.White.copy(0.5f))
                    ) {
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MadridBlue, modifier = Modifier.size(36.dp))
                            Text("3D VIEW", color = MadridBlue, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = DeepNight,
                        border = BorderStroke(1.dp, MadridGold.copy(0.3f)),
                        shadowElevation = 10.dp
                    ) {
                        Column(Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MadridGold.copy(0.5f), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.stadium_locked_msg, visitedFacts.size, 6),
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

        // Tarjeta Info
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp)) {
            AnimatedContent(
                targetState = activeFact,
                transitionSpec = { (fadeIn(tween(300)) + scaleIn(initialScale = 0.95f)).togetherWith(fadeOut(tween(300)) + scaleOut(targetScale = 1.05f)) },
                label = "Card"
            ) { fact ->
                fact?.let {
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
                                    Text(stringResource(it.titleRes), color = MadridGold, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                                    if (isCollected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MadridGold, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(stringResource(it.descriptionRes), color = Color.White.copy(0.9f), fontSize = 12.sp, lineHeight = 16.sp)
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