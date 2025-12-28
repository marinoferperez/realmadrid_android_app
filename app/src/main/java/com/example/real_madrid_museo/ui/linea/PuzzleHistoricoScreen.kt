package com.example.real_madrid_museo.ui.linea

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleHistoricoScreen(email: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val progreso = EraManager.obtenerProgreso(context,email)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Puzzle Histórico", fontWeight = FontWeight.Bold, color = MadridBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MadridBlue)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Completa las 9 épocas para ver la imagen final", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

            // REJILLA 3x3
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.aspectRatio(1f).fillMaxWidth().border(2.dp, MadridBlue, RoundedCornerShape(8.dp)),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(9) { index ->
                    val era = listaEras[index]
                    val desbloqueada = EraManager.estaDesbloqueada(context,email, era.id)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (desbloqueada) Color.Transparent else Color.LightGray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (desbloqueada) {
                            Image(
                                painter = painterResource(id = era.imagenPuzzleRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MadridBlue.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // MENSAJE DE RECOMPENSA
            if (progreso == 1f) {
                Card(colors = CardDefaults.cardColors(containerColor = MadridGold)) {
                    Text(
                        "🏆 ¡HAS COMPLETADO EL PUZZLE! ERES UN EXPERTO REAL",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        color = MadridBlue
                    )
                }
            }
        }
    }
}