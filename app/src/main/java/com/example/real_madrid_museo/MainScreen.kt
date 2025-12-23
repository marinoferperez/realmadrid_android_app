package com.example.real_madrid_museo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.remote.creation.second
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores oficiales para un diseño valorado positivamente [cite: 27]
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Inicio", "Mapa", "Cámara", "Perfil")
    val icons = listOf(Icons.Default.Home, Icons.Default.Map, Icons.Default.QrCodeScanner, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            // Barra inferior flotante y moderna
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .height(72.dp),
                shape = RoundedCornerShape(35.dp),
                shadowElevation = 12.dp,
                color = Color.White.copy(alpha = 0.95f)
            ) {
                NavigationBar(containerColor = Color.Transparent) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item, modifier = Modifier.size(26.dp)) },
                            label = { Text(item, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MadridBlue,
                                indicatorColor = MadridGold.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // Fondo con degradado profesional
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MadridBlue.copy(alpha = 0.1f), Color.White)))) {

            Column(modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()) {

                when (selectedItem) {
                    0 -> DashboardInicio() // La pantalla estética de noticias/partidos
                    //1 -> PantallaMapa()
                    //2 -> PantallaScanner()
                    3 -> PerfilContent()
                }
            }
        }
    }
}

@Composable
fun DashboardInicio() {
    LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("¡Hola, Madridista!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MadridBlue)
            Text("Prepárate para tu visita nivel Experto", style = MaterialTheme.typography.bodyLarge, color = Color.Gray) // Basado en
            Spacer(modifier = Modifier.height(24.dp))
        }

        // SECCIÓN: PRÓXIMO PARTIDO (Card Estética)
        item {
            Text("Próximo Encuentro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MadridBlue)
            ) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Real Madrid", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("VS", color = MadridGold, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Villacarrillo", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // SECCIÓN: NOTICIAS (Tarjetas con imágenes)
        item {
            Text("Últimas Noticias", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(3) { index ->
            NewsCard(index)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsCard(index: Int) {
    val titles = listOf("El nuevo Bernabéu abre sus puertas", "15 Champions: La vitrina se actualiza", "Tour VIP: Descubre los vestuarios")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(15.dp)).background(Color.LightGray)) // Espacio para imagen
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(titles[index], fontWeight = FontWeight.Bold, maxLines = 2, color = MadridBlue)
                Text("Hace 2 horas", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

/*
@Composable
fun PantallaMapa() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Mapa del Museo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MadridBlue)
            Text("Encuentra salas y servicios rápidamente", color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

            // CONTENEDOR DEL MAPA (Aquí iría la API de OpenStreetMap más adelante)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.Map, contentDescription = null, size = 60.dp, tint = MadridBlue.copy(alpha = 0.5f))
                    Text("Cargando Mapa Interactivo...", modifier = Modifier.padding(top = 80.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Servicios del Museo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Lista de servicios obligatorios por el guion
        val servicios = listOf(
            "Sala de Trofeos" to Icons.Default.EmojiEvents,
            "Tienda Oficial" to Icons.Default.ShoppingBag,
            "Cafetería Bernabéu" to Icons.Default.Coffee,
            "Aseos y Ascensores" to Icons.Default.Accessible
        )

        items(servicios) { servicio ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(servicio.second, contentDescription = null, tint = MadridGold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(servicio.first, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun PantallaScanner() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Simulación de fondo de cámara
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Text(
                "Escaneando...",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // INTERFAZ SOBREPUESTA (Overlay)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Enfoca el código QR del tótem",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp)
            )

            // Cuadro de enfoque estético
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Transparent)
                    .padding(2.dp)
            ) {
                // Aquí iría el visor de la cámara real
            }

            // BOTONES DE CONTROL (Sensores/Accesibilidad)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de Linterna/Flash
                FilledIconButton(
                    onClick = { /* Acción sensor luz */ },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MadridGold)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Encender Flash", tint = MadridBlue)
                }

                // Ayuda Oral (Requisito de sistema de diálogo)
                FilledIconButton(
                    onClick = { /* Activar audio-guía */ },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MadridBlue)
                ) {
                    Icon(Icons.Default.Hearing, contentDescription = "Ayuda por voz", tint = Color.White)
                }
            }
        }
    }
}
*/
@Composable
fun PerfilContent() {
    Column {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, color = MadridBlue)
        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de logros (fomenta volver al museo )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MadridGold.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🏆 Logros Desbloqueados", style = MaterialTheme.typography.titleMedium)
                Text("• Visitante Estrella: 5 salas escaneadas")
                Text("• Experto en Trofeos: 100% Vitrina")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sección de descuentos (requisito de vuestro índice)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MadridBlue.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🎫 Mis Descuentos", style = MaterialTheme.typography.titleMedium)
                Text("Tienes un 10% de descuento en la tienda oficial")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    // Aquí llamamos a tu función para que la IA la dibuje
    MainScreen()
}