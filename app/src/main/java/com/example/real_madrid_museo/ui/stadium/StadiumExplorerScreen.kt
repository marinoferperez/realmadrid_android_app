package com.example.real_madrid_museo.ui.stadium

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.Config
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlin.math.hypot

// 🎨 Colores Real Madrid
private val MadridBlue = Color(0xFF002D72)
private val MadridGold = Color(0xFFFEBE10)

private enum class VisitMode(val title: String) {
    VISITOR("Visitante"),
    FAN("Aficionado"),
    ARCHITECT("Arquitecto"),
    HISTORICAL("Histórico")
}

@Composable
fun StadiumExplorerScreen(
    assetPath: String = "models/bernabeu.glb"
) {
    val context = LocalContext.current
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()

    var modelNode by remember { mutableStateOf<ModelNode?>(null) }

    var visitMode by remember { mutableStateOf(VisitMode.VISITOR) }
    var showInfo by remember { mutableStateOf(false) }

    // Long press (solo 1 dedo)
    var downTime by remember { mutableStateOf(0L) }
    var downX by remember { mutableStateOf(0f) }
    var downY by remember { mutableStateOf(0f) }
    var movedTooMuch by remember { mutableStateOf(false) }

    // Pinch
    var isPinching by remember { mutableStateOf(false) }

    // ===== Pinch SOLO para zoom =====
    val scaleDetector = remember {
        ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    isPinching = true
                    showInfo = false
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    modelNode?.let {
                        it.scale =
                            it.scale * detector.scaleFactor.coerceIn(0.96f, 1.04f)
                    }
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    isPinching = false
                }
            }
        )
    }

    fun modeFromPointers(count: Int): VisitMode =
        when (count) {
            1 -> VisitMode.VISITOR
            2 -> VisitMode.FAN
            3 -> VisitMode.ARCHITECT
            5 -> VisitMode.HISTORICAL
            else -> VisitMode.VISITOR
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

                        when (event.actionMasked) {

                            MotionEvent.ACTION_DOWN -> {
                                downTime = System.currentTimeMillis()
                                downX = event.getX(0)
                                downY = event.getY(0)
                                movedTooMuch = false
                                showInfo = false
                                visitMode = VisitMode.VISITOR
                            }

                            MotionEvent.ACTION_POINTER_DOWN -> {
                                visitMode = modeFromPointers(event.pointerCount)
                                showInfo = true
                            }

                            MotionEvent.ACTION_MOVE -> {
                                if (event.pointerCount == 1 && !isPinching) {
                                    val dist = hypot(
                                        (event.getX(0) - downX).toDouble(),
                                        (event.getY(0) - downY).toDouble()
                                    ).toFloat()

                                    if (dist > touchSlop) movedTooMuch = true

                                    val elapsed =
                                        System.currentTimeMillis() - downTime

                                    showInfo =
                                        !movedTooMuch && elapsed > 650
                                }
                            }

                            MotionEvent.ACTION_UP,
                            MotionEvent.ACTION_POINTER_UP,
                            MotionEvent.ACTION_CANCEL -> {
                                showInfo = false
                                isPinching = false
                                movedTooMuch = false
                            }
                        }
                        true
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = showInfo,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            InfoPanel(mode = visitMode)
        }
    }
}

// ================= UI =================

@Composable
private fun InfoPanel(mode: VisitMode) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .heightIn(min = 96.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MadridBlue)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Modo ${mode.title}",
                style = MaterialTheme.typography.titleMedium,
                color = MadridGold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = infoText(mode),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

private fun infoText(mode: VisitMode): String =
    when (mode) {
        VisitMode.VISITOR ->
            "El Santiago Bernabéu es la casa del Real Madrid y uno de los estadios más emblemáticos del mundo."
        VisitMode.FAN ->
            "Escenario de noches europeas históricas, clásicos legendarios y celebraciones inolvidables."
        VisitMode.ARCHITECT ->
            "Remodelación con cubierta retráctil, nueva envolvente estructural y tecnología de vanguardia."
        VisitMode.HISTORICAL ->
            "Inaugurado en 1947, el Bernabéu ha evolucionado junto al club y sus generaciones de leyendas."
    }
