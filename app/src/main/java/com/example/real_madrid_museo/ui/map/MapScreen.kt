package com.example.real_madrid_museo.ui.map

import android.content.Intent
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.kahoot.KahootActivity
import com.example.real_madrid_museo.home.PlayersActivity
import kotlinx.coroutines.launch
import kotlin.random.Random

// Colores oficiales
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

data class Point2D(val x: Float, val y: Float)
data class RoomShape(
    val name: String, 
    val vertices2D: List<Point2D>, 
    val roofColor: Color, 
    val wallColor: Color,
    val icon: ImageVector? = null
)

@Composable
fun MapScreen(onNavigate: (String) -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados de transformación
    val scaleAnim = remember { Animatable(0.35f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedRoomName by remember { mutableStateOf<String?>(null) }

    // --- ANIMACIONES ---
    val infiniteTransition = rememberInfiniteTransition(label = "PremiumAnims")
    
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -30f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // Animación para las partículas de polvo
    val particleAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "particles"
    )

    // Generar partículas una sola vez
    val particles = remember { List(40) { Offset(Random.nextFloat(), Random.nextFloat()) } }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scope.launch {
            scaleAnim.snapTo((scaleAnim.value * zoomChange).coerceIn(0.15f, 1.2f))
            offsetX.snapTo(offsetX.value + offsetChange.x)
            offsetY.snapTo(offsetY.value + offsetChange.y)
        }
    }

    val hallName = stringResource(R.string.map_hall)
    val entranceName = stringResource(R.string.map_entrance)
    val exitName = stringResource(R.string.map_exit)
    val roomNames = listOf(
        hallName, entranceName, exitName,
        stringResource(R.string.map_showcase),
        stringResource(R.string.map_history),
        stringResource(R.string.map_stadium),
        stringResource(R.string.map_game),
        stringResource(R.string.map_players)
    )

    val selectableRooms = roomNames.filter { 
        it != hallName && it != entranceName && it != exitName && it.isNotEmpty() 
    }

    val mapStructure = remember(roomNames) { getProMapStructure(roomNames) }
    val iconPainters = mapStructure.associate { room ->
        room to room.icon?.let { rememberVectorPainter(it) }
    }

    var showTrophyDialog by remember { mutableStateOf(false)}

    val gameRoomName = stringResource(R.string.map_game)
    val playersRoomName = stringResource(R.string.map_players)
    val showcaseRoomName = stringResource(R.string.map_showcase)


    fun focusOnRoom(roomName: String) {
        val room = mapStructure.find { it.name == roomName } ?: return
        val center = room.vertices2D.map { it.toIso() }.let { pts ->
            Offset(pts.map { it.x }.average().toFloat(), pts.map { it.y }.average().toFloat())
        }
        selectedRoomName = roomName
        scope.launch {
            launch { scaleAnim.animateTo(0.6f, tween(800)) }
            launch { offsetX.animateTo(-center.x * 0.6f, tween(800)) }
            launch { offsetY.animateTo(-center.y * 0.6f, tween(800)) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF000000))))
            .transformable(state = state)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val worldIsoPos = Offset(
                        (tapOffset.x - size.width / 2 - offsetX.value) / scaleAnim.value,
                        (tapOffset.y - size.height / 2 - offsetY.value) / scaleAnim.value
                    )
                    val clickedRoom = mapStructure.find { isPointInIsoRoom(worldIsoPos, it.vertices2D) }
                    if (clickedRoom != null && clickedRoom.name.isNotEmpty()) {
                        focusOnRoom(clickedRoom.name)
                    } else {
                        selectedRoomName = null
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform({
                translate(size.width / 2 + offsetX.value, size.height / 2 + offsetY.value)
                scale(scaleAnim.value, scaleAnim.value)
            }) {
                // 1. Suelo y Partículas
                drawProGround()
                drawAtmosphericParticles(particles, particleAnim)

                // 2. Ruta de Navegación (Si hay selección)
                selectedRoomName?.let { target ->
                    drawNavigationPath(mapStructure, entranceName, target, pulseAlpha)
                }

                // 3. Estructura del Mapa
                mapStructure.forEach { drawRoomShadow(it) }
                mapStructure.forEach { room ->
                    if (room.name == selectedRoomName) drawRoomHighlight(room, pulseAlpha)
                }
                mapStructure.forEach { drawProWalls(it) }
                mapStructure.forEach { drawProRoof(it) }

                // 4. Etiquetas e Iconos
                if (scaleAnim.value >= 0.15f) {
                    mapStructure.forEach { room ->
                        drawRoomLabel(room)
                        iconPainters[room]?.let { painter -> drawRoomIcon(room, floatAnim, painter) }
                    }
                }
            }
        }

        // --- Interfaz de Usuario ---
        SmallFloatingActionButton(
            onClick = { 
                selectedRoomName = null
                scope.launch {
                    launch { scaleAnim.animateTo(0.35f) }
                    launch { offsetX.animateTo(0f) }
                    launch { offsetY.animateTo(0f) }
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).padding(bottom = 140.dp),
            containerColor = MadridGold,
            contentColor = MadridBlue,
            shape = CircleShape
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
        }

        // Selector Superior
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.elevatedCardElevation(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = MadridBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.explore_rooms), color = MadridBlue, fontWeight = FontWeight.Bold)
                    }
                    Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MadridBlue)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.85f).background(Color.White, RoundedCornerShape(20.dp)), offset = DpOffset(0.dp, 8.dp)) {
                selectableRooms.forEach { roomName ->
                    DropdownMenuItem(text = { Text(roomName, color = MadridBlue, fontWeight = FontWeight.Medium) }, leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MadridGold) }, onClick = { expanded = false; focusOnRoom(roomName) })
                }
            }
        }

        // Action Card Inferior
        AnimatedVisibility(visible = selectedRoomName != null, enter = slideInVertically(initialOffsetY = { it }) + fadeIn(), exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(), modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(25.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White), elevation = CardDefaults.elevatedCardElevation(16.dp)) {
                Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedRoomName ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MadridBlue)
                        Text(stringResource(R.string.museum_room), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Button(
                        onClick = { 
                            selectedRoomName?.let { name ->
                                // CAMBIO AQUÍ: Añadimos la condición para PlayersActivity
                                
                                // 1. Si es la sala de Juegos -> Kahoot
                                if (name.equals(gameRoomName, ignoreCase = true)) {
                                    context.startActivity(Intent(context, KahootActivity::class.java))
                                } 
                                // 2. Si es la sala de Jugadores -> PlayersActivity
                                else if (name.equals(playersRoomName, ignoreCase = true)) {
                                    context.startActivity(Intent(context, PlayersActivity::class.java))
                                }
                                    else if (name.equals(showcaseRoomName, ignoreCase = true)){
                                        showTrophyDialog = true

                                }
                                // 3. Si es otra sala -> Navegación normal
                                else {
                                    onNavigate(name)
                                }
                            } 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MadridBlue),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(stringResource(R.string.visit_button), color = MadridGold, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
        // --- DIÁLOGO DE SELECCIÓN DE TROFEOS ---
        if (showTrophyDialog) {
            AlertDialog(
                onDismissRequest = { showTrophyDialog = false },
                title = {
                    Text("Selecciona un Trofeo", fontWeight = FontWeight.Bold, color = MadridBlue)
                },
                text = {
                    // Listamos los trofeos que definimos en Trofeo.kt
                    Column(modifier = Modifier.fillMaxWidth()) {
                        com.example.real_madrid_museo.ui.vitrina.listaTrofeos.forEachIndexed { index, trofeo ->
                            TextButton(
                                onClick = {
                                    showTrophyDialog = false

                                    // 1. EL CAMBIO CLAVE: Registramos el trofeo como visto al seleccionarlo
                                    // Esto hace que aparezca a color en el álbum y cuente para el progreso
                                    com.example.real_madrid_museo.ui.vitrina.TrofeoManager.marcarTrofeoVisto(context, index)

                                    // 2. Lanzamos la actividad como antes
                                    val intent = Intent(context, com.example.real_madrid_museo.ui.vitrina.TrofeoActivity::class.java)
                                    intent.putExtra("INDICE_TROFEO", index)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MadridGold)
                                    Spacer(Modifier.width(12.dp))
                                    Text(trofeo.nombre, color = MadridBlue, textAlign = TextAlign.Start)
                                }
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTrophyDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    } // Fin de MapScreen
}

// --- DIBUJO PREMIUM ---

fun DrawScope.drawAtmosphericParticles(particles: List<Offset>, animValue: Float) {
    particles.forEach { p ->
        val x = ((p.x * 8000f - 4000f) + animValue * 0.2f) % 4000f
        val y = ((p.y * 18000f - 9000f) + animValue * 0.5f) % 9000f
        drawCircle(color = Color.White.copy(alpha = 0.2f), radius = 3f + p.x * 5f, center = Offset(x, y).toIso())
    }
}

fun DrawScope.drawNavigationPath(map: List<RoomShape>, startName: String, endName: String, alpha: Float) {
    val start = map.find { it.name == startName } ?: return
    val end = map.find { it.name == endName } ?: return
    
    val startPos = start.vertices2D.map { it.toIso() }.let { Offset(it.map { p -> p.x }.average().toFloat(), it.map { p -> p.y }.average().toFloat()) }
    val endPos = end.vertices2D.map { it.toIso() }.let { Offset(it.map { p -> p.x }.average().toFloat(), it.map { p -> p.y }.average().toFloat()) }
    
    val path = Path().apply {
        moveTo(startPos.x, startPos.y)
        // Punto intermedio en el pasillo para hacer la ruta en "L" isométrica
        lineTo(startPos.x, endPos.y)
        lineTo(endPos.x, endPos.y)
    }
    
    drawPath(path, MadridGold.copy(alpha = alpha), style = Stroke(width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), alpha * 100f)))
}

fun DrawScope.drawRoomHighlight(room: RoomShape, pulse: Float) {
    val path = Path().apply {
        if (room.vertices2D.isNotEmpty()) {
            val first = room.vertices2D[0].toIso(10f)
            moveTo(first.x, first.y)
            room.vertices2D.forEach { p -> val iso = p.toIso(10f); lineTo(iso.x, iso.y) }
            close()
        }
    }
    drawPath(path, MadridGold.copy(alpha = pulse), style = Stroke(width = 25f, cap = StrokeCap.Round))
}

fun DrawScope.drawRoomIcon(room: RoomShape, floatOffset: Float, painter: VectorPainter) {
    val center = room.vertices2D.map { it.toIso() }.let { pts ->
        Offset(pts.map { it.x }.average().toFloat(), pts.map { it.y }.average().toFloat())
    }
    val iconSize = 140f
    withTransform({ translate(center.x - iconSize / 2, center.y - 220f + floatOffset) }) {
        with(painter) { draw(size = Size(iconSize, iconSize), colorFilter = ColorFilter.tint(MadridGold)) }
    }
}

fun DrawScope.drawProGround() {
    val xBounds = 4000f; val yBounds = 9000f
    val points = listOf(Point2D(-xBounds, yBounds), Point2D(xBounds, yBounds), Point2D(xBounds, -yBounds), Point2D(-xBounds, -yBounds))
    val path = Path().apply {
        val first = points[0].toIso(); moveTo(first.x, first.y)
        points.forEach { p -> val iso = p.toIso(); lineTo(iso.x, iso.y) }; close()
    }
    drawPath(path, Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF020617))))
}

fun DrawScope.drawRoomShadow(room: RoomShape) {
    val shadowOffset = 40f
    val path = Path().apply {
        val first = room.vertices2D[0].toIso(shadowOffset); moveTo(first.x, first.y)
        room.vertices2D.forEach { p -> val iso = p.toIso(shadowOffset); lineTo(iso.x, iso.y) }; close()
    }
    drawPath(path, Color.Black.copy(alpha = 0.3f))
}

fun DrawScope.drawProWalls(room: RoomShape) {
    val wallHeight = 130f
    val pts = room.vertices2D.map { it.toIso() }
    for (i in pts.indices) {
        val p1 = pts[i]; val p2 = pts[(i + 1) % pts.size]
        val isSouthWall = p2.x > p1.x
        val wallBrush = if (isSouthWall) Brush.verticalGradient(listOf(room.wallColor, room.wallColor.copy(alpha = 0.7f)))
                        else Brush.verticalGradient(listOf(room.wallColor.copy(alpha = 0.9f), room.wallColor.copy(alpha = 0.5f)))
        val wallPath = Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p2.x, p2.y + wallHeight); lineTo(p1.x, p1.y + wallHeight); close() }
        drawPath(wallPath, wallBrush)
    }
}

fun DrawScope.drawProRoof(room: RoomShape) {
    val path = Path().apply {
        if (room.vertices2D.isNotEmpty()) {
            val first = room.vertices2D[0].toIso(); moveTo(first.x, first.y)
            room.vertices2D.forEach { p -> val iso = p.toIso(); lineTo(iso.x, iso.y) }; close()
        }
    }
    drawPath(path, Brush.linearGradient(listOf(room.roofColor, room.roofColor.copy(alpha = 0.8f))))
}

fun DrawScope.drawRoomLabel(room: RoomShape) {
    if (room.name.isEmpty()) return
    val center = room.vertices2D.map { it.toIso() }.let { pts ->
        Offset(pts.map { it.x }.average().toFloat(), pts.map { it.y }.average().toFloat())
    }
    drawContext.canvas.nativeCanvas.drawText(room.name.uppercase(), center.x, center.y + 15f, android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 65f; textAlign = android.graphics.Paint.Align.CENTER; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); setShadowLayer(10f, 0f, 0f, android.graphics.Color.BLACK) })
}

fun Point2D.toIso(z: Float = 0f): Offset = Offset(x - y, (x + y) / 2f + z)
fun Offset.toIso(z: Float = 0f): Offset = Offset(x - y, (x + y) / 2f + z)

fun isPointInIsoRoom(isoPoint: Offset, vertices2D: List<Point2D>): Boolean {
    val isoVertices = vertices2D.map { it.toIso() }
    var intersectCount = 0
    for (i in isoVertices.indices) {
        val v1 = isoVertices[i]; val v2 = isoVertices[(i + 1) % isoVertices.size]
        if ((v1.y > isoPoint.y) != (v2.y > isoPoint.y) && (isoPoint.x < (v2.x - v1.x) * (isoPoint.y - v1.y) / (v2.y - v1.y) + v1.x)) intersectCount++
    }
    return intersectCount % 2 != 0
}

fun getProMapStructure(names: List<String>): List<RoomShape> {
    val u = 500f; val hw = 0.4f * u; val gap = 1.3f * u; val pw = 0.25f * u
    val bRoof = MadridBlue; val bWall = Color(0xFF001A4D); val rRoof = Color(0xFFE74C3C); val rWall = Color(0xFF922B21)
    return listOf(
        RoomShape(names[0], listOf(Point2D(-hw, 8f*u), Point2D(hw, 8f*u), Point2D(hw, -8f*u), Point2D(-hw, -8f*u)), bRoof, bWall),
        RoomShape(names[1], listOf(Point2D(-hw, 10f*u), Point2D(hw, 10f*u), Point2D(hw, 8f*u), Point2D(-hw, 8f*u)), bRoof, bWall),
        RoomShape(names[2], listOf(Point2D(-hw, -8f*u), Point2D(hw, -8f*u), Point2D(hw, -10f*u), Point2D(-hw, -10f*u)), rRoof, rWall),
        RoomShape(names[3], listOf(Point2D(-3.5f*u, 7f*u), Point2D(-gap, 7f*u), Point2D(-gap, 4.5f*u), Point2D(-3.5f*u, 4.5f*u)), bRoof, bWall, Icons.Default.EmojiEvents),
        RoomShape(names[4], listOf(Point2D(gap, 7f*u), Point2D(3.5f*u, 7f*u), Point2D(3.5f*u, 4.5f*u), Point2D(gap, 4.5f*u)), bRoof, bWall, Icons.Default.History),
        RoomShape(names[5], listOf(Point2D(-4.5f*u, 2.5f*u), Point2D(-gap, 2.5f*u), Point2D(-gap, -1.5f*u), Point2D(-4.5f*u, -1.5f*u)), bRoof, bWall, Icons.Default.Stadium),
        RoomShape(names[6], listOf(Point2D(-3.5f*u, -3f*u), Point2D(-gap, -3f*u), Point2D(-gap, -5.5f*u), Point2D(-3.5f*u, -5.5f*u)), bRoof, bWall, Icons.Default.Games),
        RoomShape(names[7], listOf(Point2D(gap, -3f*u), Point2D(3.5f*u, -3f*u), Point2D(3.5f*u, -5.5f*u), Point2D(gap, -5.5f*u)), bRoof, bWall, Icons.Default.Group),
        RoomShape("", listOf(Point2D(-gap, 5.75f*u+pw), Point2D(-hw, 5.75f*u+pw), Point2D(-hw, 5.75f*u-pw), Point2D(-gap, 5.75f*u-pw)), bRoof, bWall),
        RoomShape("", listOf(Point2D(hw, 5.75f*u+pw), Point2D(gap, 5.75f*u+pw), Point2D(gap, 5.75f*u-pw), Point2D(hw, 5.75f*u-pw)), bRoof, bWall),
        RoomShape("", listOf(Point2D(-gap, 0.5f*u+pw), Point2D(-hw, 0.5f*u+pw), Point2D(-hw, 0.5f*u-pw), Point2D(-gap, 0.5f*u-pw)), bRoof, bWall),
        RoomShape("", listOf(Point2D(-gap, -4.25f*u+pw), Point2D(-hw, -4.25f*u+pw), Point2D(-hw, -4.25f*u-pw), Point2D(-gap, -4.25f*u-pw)), bRoof, bWall),
        RoomShape("", listOf(Point2D(hw, -4.25f*u+pw), Point2D(gap, -4.25f*u+pw), Point2D(gap, -4.25f*u-pw), Point2D(hw, -4.25f*u-pw)), bRoof, bWall)
    )
}