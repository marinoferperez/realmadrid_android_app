package com.example.real_madrid_museo.ui.linea

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold

@Composable
fun SalaHistorica(email: String,onBack: () -> Unit) {
    val context = LocalContext.current


    // Configuramos el Pager para las 9 épocas
    val pagerState = rememberPagerState(pageCount = { listaEras.size })

    // Estado para forzar el refresco de la UI al desbloquear
    var refreshKey by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            MadridBlue.copy(alpha = 0.05f)
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "SALA HISTÓRICA",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MadridBlue
            )

            Text(
                text = "Desliza para viajar en el tiempo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // EL SWIPE DE CARTAS
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 45.dp),
                pageSpacing = 20.dp
            ) { page ->
                val era = listaEras[page]
                // Comprobamos si está desbloqueada (reaccionando al refreshKey)
                val estaDesbloqueada = remember(page, refreshKey) {
                    EraManager.estaDesbloqueada(context,email, era.id)
                }

                CartaEra(
                    era = era,
                    desbloqueada = estaDesbloqueada,
                    onSaberMas = {
                        // AQUÍ SIMULAMOS EL DESBLOQUEO PARA PROBAR
                        // En el futuro, esto se llamará tras escanear el QR
                        EraManager.desbloquearEra(context,email, era.id)
                        Toast.makeText(context, "¡Pieza número ${era.id + 1} desbloqueada!", Toast.LENGTH_SHORT).show()
                        refreshKey++ // Esto hace que la UI se actualice y se abra el candado
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // EL BOTÓN DEBE ESTAR AQUÍ (DENTRO DEL BOX)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MadridBlue,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun CartaEra(era: EraReal, desbloqueada: Boolean, onSaberMas: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(if (desbloqueada) 12.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column {
                // IMAGEN DE LA ÉPOCA
                Image(
                    painter = painterResource(id = era.imagenRes),
                    contentDescription = era.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = if (desbloqueada) 1f else 0.4f // Más oscura si está bloqueada
                )

                // TEXTOS
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = era.periodo,
                        fontWeight = FontWeight.Bold,
                        color = MadridGold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = era.titulo,
                        fontWeight = FontWeight.Black,
                        color = MadridBlue,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (desbloqueada) era.infoCorta
                        else "Contenido Bloqueado.\nEscanea el QR para desbloquear esta carta.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = if (desbloqueada) Color.DarkGray else Color.Gray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // BOTÓN DE ACCIÓN (LUPA)
                    Button(
                        onClick = onSaberMas,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (desbloqueada) MadridBlue else Color.Gray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (desbloqueada) "¿Quieres saber más?" else "Escanear QR")
                    }
                }
            }

            // --- EL CANDADO (Esquina superior derecha) ---
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = CircleShape,
                color = if (desbloqueada) MadridGold else Color.Gray.copy(alpha = 0.8f),
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = if (desbloqueada) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}
