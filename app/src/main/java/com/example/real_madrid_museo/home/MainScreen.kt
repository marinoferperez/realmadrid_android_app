package com.example.real_madrid_museo.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.map.MapScreen


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
                    1 -> MapScreen()
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

        // Creamos una lista con los IDs de las fotos que acabas de guardar
        val listaImagenes = listOf(
            R.drawable.bernabeu, // Asegúrate de que estos nombres coinciden con tus archivos
            R.drawable.vitrina,
            R.drawable.vestuario
        )

        items(3) { index ->
            NewsCard(index, listaImagenes.getOrElse(index) { R.drawable.ic_launcher_background })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsCard(index: Int, imageRes: Int) { // <--- Ahora recibe el ID de la imagen
    val titles = listOf("El nuevo Bernabéu abre sus puertas", "Mbappé visita el museo", "Nueva camiseta edición especial")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = imageRes), // Carga la imagen desde drawable
                contentDescription = "Imagen noticia",
                contentScale = ContentScale.Crop, // Recorta la imagen para llenar el cuadrado sin deformarse
                modifier = Modifier
                    .size(80.dp) // Tamaño fijo cuadrado
                    .clip(RoundedCornerShape(15.dp)) // Bordes redondeados
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(titles.getOrElse(index) { "Noticia Real Madrid" }, fontWeight = FontWeight.Bold, maxLines = 2, color = MadridBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Hace 2 horas", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PerfilContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp) // Espacio extra al final
    ) {
        // 1. CABECERA DEL PERFIL
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar (Usamos un icono por defecto para evitar errores, pero podrías poner una foto)
                Surface(
                    shape = CircleShape,
                    color = MadridBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = MadridBlue,
                        modifier = Modifier.padding(15.dp).fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Javier Madridista", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MadridBlue)
                    Text("Socio Nº 1902", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    // Etiqueta de Nivel
                    Surface(
                        color = MadridGold,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = " NIVEL EXPERTO ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MadridBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. RESUMEN DE ESTADÍSTICAS (Gamificación)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MadridBlue),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EstadisticaItem("Visitas", "12")
                    EstadisticaItem("Puntos", "850")
                    EstadisticaItem("Ranking", "#45")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. LOGROS (Diseño Visual)
        item {
            Text("Tus Logros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        LogroItem(Icons.Default.EmojiEvents, "Copas", true)
                        LogroItem(Icons.Default.Star, "MVP", true)
                        LogroItem(Icons.Default.Map, "Explorador", false) // Bloqueado
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Barra de progreso del museo
                    Text("Progreso del Museo: 65%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = 0.65f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MadridGold,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. CUPÓN DESCUENTO (Estilo Ticket Dorado)
        item {
            Text("Recompensas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MadridGold) // Fondo dorado llamativo
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("10% DTO.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MadridBlue)
                        Text("TIENDA OFICIAL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MadridBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

// Sub-componente para las estadísticas (Numeritos blancos arriba)
@Composable
fun EstadisticaItem(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MadridGold)
        Text(titulo, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}

// Sub-componente para los iconos de logros
@Composable
fun LogroItem(icono: androidx.compose.ui.graphics.vector.ImageVector, nombre: String, desbloqueado: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = if (desbloqueado) MadridBlue.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = nombre,
                tint = if (desbloqueado) MadridBlue else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(nombre, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if(desbloqueado) Color.Black else Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    // Aquí llamamos a tu función para que la IA la dibuje
    //MainScreen()
    PerfilContent()
}