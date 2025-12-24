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
fun MapScreen() {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD1D1D1))
            .transformable(state = state)
            .pointerInput(Unit) {
                detectTapGestures {
                    // Calculamos la posición en el mundo 2D
                    val worldPos = screenToWorld(it, size, offset, scale)
                    
                    // Buscamos si el toque cae dentro del BOUNDING BOX de alguna sala
                    val clickedRoom = mapStructure.find { room -> 
                        isPointInBoundingBox(worldPos, room.vertices2D) 
                    }

                    if (clickedRoom != null) {
                        // Feedback visual para saber qué se ha tocado
                        Toast.makeText(context, "Sala: ${clickedRoom.name}", Toast.LENGTH_SHORT).show()

                        if (clickedRoom.name.equals(gameRoomName, ignoreCase = true)) {
                            val intent = Intent(context, KahootActivity::class.java)
                            context.startActivity(intent)
                        }
                    } else {
                        // Opcional: Feedback si tocamos el suelo vacío para depurar coordenadas
                        // Toast.makeText(context, "Suelo: ${worldPos.x.toInt()}, ${worldPos.y.toInt()}", Toast.LENGTH_SHORT).show()
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

// --- Funciones de Dibujo y Lógica ---

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
    val xBounds = 3500f
    val yBounds = 8000f
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

// --- Funciones de Detección de Click ---

fun screenToWorld(screenPos: Offset, screenSize: androidx.compose.ui.unit.IntSize, offset: Offset, scale: Float): Point2D {
    val screenCenterX = screenSize.width / 2f
    val screenCenterY = screenSize.height / 2f

    val worldX = (screenPos.x - screenCenterX - offset.x) / scale
    val worldY = (screenPos.y - screenCenterY - offset.y) / scale

    return isoToTwoD(Offset(worldX, worldY))
}

fun isoToTwoD(isoPos: Offset): Point2D {
    val x = (isoPos.x + 2 * isoPos.y) / 2
    val y = (2 * isoPos.y - isoPos.x) / 2
    return Point2D(x, y)
}

// NUEVA FUNCIÓN: Detección por caja delimitadora (más robusta y fácil de acertar)
fun isPointInBoundingBox(point: Point2D, vertices: List<Point2D>): Boolean {
    if (vertices.isEmpty()) return false
    
    // Calculamos los límites de la sala
    val minX = vertices.minOf { it.x }
    val maxX = vertices.maxOf { it.x }
    val minY = vertices.minOf { it.y }
    val maxY = vertices.maxOf { it.y }
    
    // Margen de tolerancia (padding) para hacer el click más fácil
    val padding = 200f 
    
    return point.x >= (minX - padding) && point.x <= (maxX + padding) &&
           point.y >= (minY - padding) && point.y <= (maxY + padding)
}

// --- Estructura del Museo ---

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MapPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        MapScreen()
    }
}
