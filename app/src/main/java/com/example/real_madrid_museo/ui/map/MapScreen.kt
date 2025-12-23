package com.example.real_madrid_museo.ui.map

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

// --- Modelos de Datos ---
data class Point2D(val x: Float, val y: Float)
data class RoomShape(val name: String, val vertices2D: List<Point2D>, val roofColor: Color, val wallColor: Color)

@Composable
fun MapScreen() {
    var scale by remember { mutableStateOf(0.3f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // LÍMITES CORREGIDOS: Aumentamos el margen vertical significativamente
    val minScale = 0.15f
    val maxScale = 1.0f
    val maxPanX = 5000f
    val maxPanY = 12000f // Suficiente para ver la salida completa y más allá

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

    val mapStructure = remember { getMegaMapStructure(blueRoof, blueWall, exitRed, exitRedWall) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD1D1D1))
            .transformable(state = state)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform({
                translate(size.width / 2 + offset.x, size.height / 2 + offset.y)
                scale(scale, scale)
            }) {
                val wallHeight = 115f

                // 1. Suelo (Ahora más largo para albergar la salida)
                drawMegaGroundBase(groundColor)

                // 2. Capa de Paredes
                mapStructure.forEach { room -> drawWallsOnly(room, wallHeight) }

                // 3. Capa de Techos
                mapStructure.forEach { room -> drawRoofOnly(room) }

                // 4. Capa de Texto
                mapStructure.forEach { room -> drawTextOnly(room) }
            }
        }
    }
}

// --- Funciones de Dibujo ---

fun DrawScope.drawWallsOnly(room: RoomShape, wallHeight: Float) {
    val ceilingPoints = room.vertices2D.map { twoDToIso(it) }
    if (ceilingPoints.size < 3) return
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
    val ceilingPoints = room.vertices2D.map { twoDToIso(it) }
    if (ceilingPoints.size < 3) return
    val roofPath = Path().apply {
        moveTo(ceilingPoints[0].x, ceilingPoints[0].y)
        ceilingPoints.forEach { lineTo(it.x, it.y) }; close()
    }
    drawPath(roofPath, room.roofColor)
}

fun DrawScope.drawTextOnly(room: RoomShape) {
    if (room.name.isEmpty()) return
    val ceilingPoints = room.vertices2D.map { twoDToIso(it) }
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
    // Ampliamos el suelo para que la salida no se vea cortada
    val xBounds = 3500f
    val yBounds = 8000f // Mucho más margen hacia abajo
    val groundVertices = listOf(
        Point2D(-xBounds, yBounds), Point2D(xBounds, yBounds),
        Point2D(xBounds, -yBounds), Point2D(-xBounds, -yBounds)
    ).map { twoDToIso(it) }
    val topPath = Path().apply {
        moveTo(groundVertices[0].x, groundVertices[0].y)
        groundVertices.forEach { lineTo(it.x, it.y) }; close()
    }
    drawPath(topPath, color)
}

fun twoDToIso(point: Point2D): Offset = Offset(point.x - point.y, (point.x + point.y) / 2f)

// --- Estructura del Museo ---

fun getMegaMapStructure(blue: Color, blueDark: Color, red: Color, redDark: Color): List<RoomShape> {
    val u = 500f
    val hw = 0.4f * u
    val gap = 1.3f * u
    val pw = 0.25f * u

    val pasillo = RoomShape("Pasillo", listOf(Point2D(-hw, 8f*u), Point2D(hw, 8f*u), Point2D(hw, -8f*u), Point2D(-hw, -8f*u)), blue, blueDark)

    // Entrada (Fondo)
    val entrada = RoomShape("Entrada", listOf(Point2D(-hw, 10f*u), Point2D(hw, 10f*u), Point2D(hw, 8f*u), Point2D(-hw, 8f*u)), blue, blueDark)

    // Salida (Frente/Abajo) - Color Rojo
    val salida = RoomShape("Salida", listOf(Point2D(-hw, -8f*u), Point2D(hw, -8f*u), Point2D(hw, -10f*u), Point2D(-hw, -10f*u)), red, redDark)

    // Salas Principales
    val vitrina = RoomShape("Vitrina", listOf(Point2D(-3.5f*u, 7f*u), Point2D(-gap, 7f*u), Point2D(-gap, 4.5f*u), Point2D(-3.5f*u, 4.5f*u)), blue, blueDark)
    val historia = RoomShape("Historia", listOf(Point2D(gap, 7f*u), Point2D(3.5f*u, 7f*u), Point2D(3.5f*u, 4.5f*u), Point2D(gap, 4.5f*u)), blue, blueDark)
    val estadio = RoomShape("Estadio", listOf(Point2D(-4.5f*u, 2.5f*u), Point2D(-gap, 2.5f*u), Point2D(-gap, -1.5f*u), Point2D(-4.5f*u, -1.5f*u)), blue, blueDark)
    val juego = RoomShape("Juego", listOf(Point2D(-3.5f*u, -3f*u), Point2D(-gap, -3f*u), Point2D(-gap, -5.5f*u), Point2D(-3.5f*u, -5.5f*u)), blue, blueDark)
    val jugadores = RoomShape("Jugadores", listOf(Point2D(gap, -3f*u), Point2D(3.5f*u, -3f*u), Point2D(3.5f*u, -5.5f*u), Point2D(gap, -5.5f*u)), blue, blueDark)

    // Pasillitos (Puentes)
    val puentes = listOf(
        RoomShape("", listOf(Point2D(-gap, 6f*u+pw), Point2D(-hw, 6f*u+pw), Point2D(-hw, 6f*u-pw), Point2D(-gap, 6f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(hw, 6f*u+pw), Point2D(gap, 6f*u+pw), Point2D(gap, 6f*u-pw), Point2D(hw, 6f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(-gap, 0.5f*u+pw), Point2D(-hw, 0.5f*u+pw), Point2D(-hw, 0.5f*u-pw), Point2D(-gap, 0.5f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(-gap, -4.2f*u+pw), Point2D(-hw, -4.2f*u+pw), Point2D(-hw, -4.2f*u-pw), Point2D(-gap, -4.2f*u-pw)), blue, blueDark),
        RoomShape("", listOf(Point2D(hw, -4.2f*u+pw), Point2D(gap, -4.2f*u+pw), Point2D(gap, -4.2f*u-pw), Point2D(hw, -4.2f*u-pw)), blue, blueDark)
    )

    return listOf(pasillo, entrada, salida, vitrina, historia, estadio, juego, jugadores) + puentes
}