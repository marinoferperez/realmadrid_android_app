package com.example.real_madrid_museo.ui.Prueba

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold

@Composable
fun PruebaScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior simple
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MadridBlue)
                .padding(top = 40.dp, bottom = 20.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(
                text = "SECCIÓN ESPECIAL",
                modifier = Modifier.align(Alignment.Center),
                color = MadridGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Contenido Central
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MadridGold
        )

        Text(
            text = "PRUEBA",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = MadridBlue
        )

        Text(
            text = "Has escaneado el código de Leyenda",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1.2f))
    }
}