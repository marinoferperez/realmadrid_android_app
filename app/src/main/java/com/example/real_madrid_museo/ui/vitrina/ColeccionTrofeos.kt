package com.example.real_madrid_museo.ui.vitrina

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
fun ColeccionTrofeos(onBack: () -> Unit) {
    val context = LocalContext.current
    val progreso = remember { TrofeoManager.obtenerProgreso(context) }

    // Matriz para poner imágenes en blanco y negro
    val grayScaleMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Álbum de Trofeos", fontWeight = FontWeight.Bold, color = MadridBlue) },
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
            // 1. BARRA DE PROGRESO
            Text("Tu Progreso: ${(progreso * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MadridBlue)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = MadridGold,
                trackColor = Color.LightGray.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. CUADRÍCULA DE TROFEOS
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaTrofeos.size) { index ->
                    val trofeo = listaTrofeos[index]
                    val desbloqueado = TrofeoManager.estaDesbloqueado(context, index)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = trofeo.imagenRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Fit,
                                    // Si no está desbloqueado, lo ponemos en blanco y negro y transparente
                                    colorFilter = if (!desbloqueado) ColorFilter.colorMatrix(grayScaleMatrix) else null,
                                    alpha = if (desbloqueado) 1f else 0.3f
                                )

                                // Candado si está bloqueado
                                if (!desbloqueado) {
                                    Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.DarkGray, modifier = Modifier.size(30.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                trofeo.nombre,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (desbloqueado) MadridBlue else Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // 3. LA RECOMPENSA
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = if (progreso == 1f) MadridGold else Color.LightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (progreso == 1f) "¡RECOMPENSA DESBLOQUEADA!" else "Completa la colección para desbloquear",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (progreso == 1f) MadridBlue else Color.DarkGray
                    )
                    Text("20% DE DESCUENTO EN TIENDA", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}