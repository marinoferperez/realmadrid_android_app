package com.example.real_madrid_museo.ui.map

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.kahoot.KahootActivity

// --- Modelos de Datos ---
data class Point2D(val x: Float, val y: Float)
data class RoomShape(val name: String, val vertices2D: List<Point2D>, val roofColor: Color, val wallColor: Color)

@Composable
fun MapScreen(onNavigate: (String) -> Unit = {}) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(0.3f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 0.15f
    val maxScale = 1.0f
    val maxPanX = 5000f
    val maxPanY = 12000f

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(minScale, maxScale)
        val newOffset = offset + offsetChange
        offset = Offset(
            x = newOffset.x.coerceIn(-maxPanX * scale, maxPanX * scale),
            y = newOffset.y.coerceIn(-maxPanY * scale, maxPanY * scale)
        )
    }

    val blueRoof = Color(0xFF3E50B4)
    val blueWall = Color(0xFF1A237E)
    val exitRed = Color(0xFFD32F2F)
    val exitRedWall = Color(0xFFB71C1C)
    val groundColor = Color(0xFFF5F5F5)

    val roomNames = listOf(
        stringResource(R.string.map_hall),
        stringResource(R.string.map_entrance),
        stringResource(R.string.map_exit),
        stringResource(R.string.map_showcase),
        stringResource(R.string.map_history),
        stringResource(R.string.map_stadium),
        stringResource(R.string.map_game),
        stringResource(R.string.map_players)
    )

    val mapStructure = remember(roomNames) { getMegaMapStructure(roomNames, blueRoof, blueWall, exitRed, exitRedWall) }
    val gameRoomName = stringResource(R.string.map_game)
    val historyRoomName = stringResource(R.string.map_history)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD1D1D1))
            .transformable(state = state)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    // 1. Convertimos el tap a coordenadas de "mundo isométrico"
                    val worldIsoPos = screenToWorldIso(tapOffset, size, offset, scale)
                    
                    // 2. Buscamos la sala comprobando si el click está dentro de su forma dibujada
                    val clickedRoom = mapStructure.find { room ->
                        isPointInIsoRoom(worldIsoPos, room.vertices2D)
                    }

                    if (clickedRoom != null) {
                        if (clickedRoom.name.equals(gameRoomName, ignoreCase = true)) {
                            val intent = Intent(context, KahootActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            onNavigate(clickedRoom.name)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform({
                translate(size.width / 2 + offset.x, size.height / 2 + offset.y)
                scale(scale, scale)
            }) {
                val wallHeight = 115f
                drawMegaGroundBase(groundColor)
                mapStructure.forEach { room -> drawWallsOnly(room, wallHeight) }
                mapStructure.forEach { room -> drawRoofOnly(room) }
                mapStructure.forEach { room -> drawTextOnly(room) }
            }
        }
    }
}

// --- Funciones de Detección Corregidas ---

fun screenToWorldIso(screenPos: Offset, screenSize: androidx.compose.ui.unit.IntSize, offset: Offset, scale: Float): Offset {
    val centerX = screenSize.width / 2f
    val centerY = screenSize.height / 2f
    return Offset(
        x = (screenPos.x - centerX - offset.x) / scale,
        y = (screenPos.y - centerY - offset.y) / scale
    )
}

fun isPointInIsoRoom(isoPoint: Offset, vertices2D: List<Point2D>): Boolean {
    // Transformamos los vértices 2D a Isométrico (tal cual se dibujan)
    val isoVertices = vertices2D.map { Offset(it.x - it.y, (it.x + it.y) / 2f) }
    
    var intersectCount = 0
    for (i in isoVertices.indices) {
        val v1 = isoVertices[i]
        val v2 = isoVertices[(i + 1) % isoVertices.size]
        
        // Algoritmo Ray-casting para polígonos
        if ((v1.y > isoPoint.y) != (v2.y > isoPoint.y) &&
            (isoPoint.x < (v2.x - v1.x) * (isoPoint.y - v1.y) / (v2.y - v1.y) + v1.x)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 != 0
}

// --- Funciones de Dibujo ---

fun DrawScope.drawWallsOnly(room: RoomShape, wallHeight: Float) {
    val ceilingPoints = room.vertices2D.map { Offset(it.x - it.y, (it.x + it.y) / 2f) }
    for (i in ceilingPoints.indices) {
        val p1 = ceilingPoints[i]
        val p2 = ceilingPoints[(i + 1) % ceilingPoints.size]
        val wallPath = Path().apply {
            moveTo(p1.x, p1.y); lineTo(p2.x, p2.y)
            lineTo(p2.x, p2.y + wallHeight); lineTo(p1.x, p1.y + wallHeight); close()
        }
        drawPath(wallPath, room.wallColor)
    }
}

fun DrawScope.drawRoofOnly(room: RoomShape) {
    val ceilingPoints = room.vertices2D.map { Offset(it.x - it.y, (it.x + it.y) / 2f) }
    val roofPath = Path().apply {
        if (ceilingPoints.isNotEmpty()) {
            moveTo(ceilingPoints[0].x, ceilingPoints[0].y)
            ceilingPoints.forEach { lineTo(it.x, it.y) }
            close()
        }
    }
    drawPath(roofPath, room.roofColor)
}

fun DrawScope.drawTextOnly(room: RoomShape) {
    if (room.name.isEmpty()) return
    val ceilingPoints = room.vertices2D.map { Offset(it.x - it.y, (it.x + it.y) / 2f) }
    val centerX = ceilingPoints.map { it.x }.average().toFloat()
    val centerY = ceilingPoints.map { it.y }.average().toFloat()
    drawContext.canvas.nativeCanvas.drawText(
        room.name.uppercase(), centerX, centerY + 10f,
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 75f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    )
}

fun DrawScope.drawMegaGroundBase(color: Color) {
    val xBounds = 3500f
    val yBounds = 8000f
    val groundVertices = listOf(
        Point2D(-xBounds, yBounds), Point2D(xBounds, yBounds),
        Point2D(xBounds, -yBounds), Point2D(-xBounds, -yBounds)
    ).map { Offset(it.x - it.y, (it.x + it.y) / 2f) }
    val topPath = Path().apply {
        moveTo(groundVertices[0].x, groundVertices[0].y)
        groundVertices.forEach { lineTo(it.x, it.y) }; close()
    }
    drawPath(topPath, color)
}

fun getMegaMapStructure(names: List<String>, blue: Color, blueDark: Color, red: Color, redDark: Color): List<RoomShape> {
    val u = 500f
    val hw = 0.4f * u
    val gap = 1.3f * u
    val pw = 0.25f * u

    val pasillo = RoomShape(names[0], listOf(Point2D(-hw, 8f*u), Point2D(hw, 8f*u), Point2D(hw, -8f*u), Point2D(-hw, -8f*u)), blue, blueDark)
    val entrada = RoomShape(names[1], listOf(Point2D(-hw, 10f*u), Point2D(hw, 10f*u), Point2D(hw, 8f*u), Point2D(-hw, 8f*u)), blue, blueDark)
    val salida = RoomShape(names[2], listOf(Point2D(-hw, -8f*u), Point2D(hw, -8f*u), Point2D(hw, -10f*u), Point2D(-hw, -10f*u)), red, redDark)
    val vitrina = RoomShape(names[3], listOf(Point2D(-3.5f*u, 7f*u), Point2D(-gap, 7f*u), Point2D(-gap, 4.5f*u), Point2D(-3.5f*u, 4.5f*u)), blue, blueDark)
    val historia = RoomShape(names[4], listOf(Point2D(gap, 7f*u), Point2D(3.5f*u, 7f*u), Point2D(3.5f*u, 4.5f*u), Point2D(gap, 4.5f*u)), blue, blueDark)
    val estadio = RoomShape(names[5], listOf(Point2D(-4.5f*u, 2.5f*u), Point2D(-gap, 2.5f*u), Point2D(-gap, -1.5f*u), Point2D(-4.5f*u, -1.5f*u)), blue, blueDark)
    val juego = RoomShape(names[6], listOf(Point2D(-3.5f*u, -3f*u), Point2D(-gap, -3f*u), Point2D(-gap, -5.5f*u), Point2D(-3.5f*u, -5.5f*u)), blue, blueDark)
    val jugadores = RoomShape(names[7], listOf(Point2D(gap, -3f*u), Point2D(3.5f*u, -3f*u), Point2D(3.5f*u, -5.5f*u), Point2D(gap, -5.5f*u)), blue, blueDark)

    val puentes = listOf(
        RoomShape("", listOf(Point2D(-gap, 6f*u+pw), Point2D(-hw, 6f*u+pw), Point2D(-hw, 6f*u-pw), Point2D(-gap, 6f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(hw, 6f*u+pw), Point2D(gap, 6f*u+pw), Point2D(gap, 6f*u-pw), Point2D(hw, 6f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(-gap, 0.5f*u+pw), Point2D(-hw, 0.5f*u+pw), Point2D(-hw, 0.5f*u-pw), Point2D(-gap, 0.5f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(-gap, -4.2f*u+pw), Point2D(-hw, -4.2f*u+pw), Point2D(-hw, -4.2f*u-pw), Point2D(-gap, -4.2f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(hw, -4.2f*u+pw), Point2D(gap, -4.2f*u+pw), Point2D(gap, -4.2f*u-pw), Point2D(hw, -4.2f*u-pw)), blue, blueDark)
    )

    return listOf(pasillo, entrada, salida, vitrina, historia, estadio, juego, jugadores) + puentes
}
