package com.example.real_madrid_museo.home

import android.content.Intent
import android.widget.Toast // Para avisos rápidos de códigos escaneados
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.DatabaseHelper
import com.example.real_madrid_museo.ui.ScannerScreen.ScannerScreen
import com.example.real_madrid_museo.ui.comun.idiomas.LanguageToggle
import com.example.real_madrid_museo.ui.comun.idiomas.cambiarIdioma
import com.example.real_madrid_museo.ui.comun.idiomas.obtenerIdioma
import com.example.real_madrid_museo.ui.map.MapScreen
import com.example.real_madrid_museo.kahoot.KahootActivity
import com.example.real_madrid_museo.ui.vitrina.ColeccionTrofeos
import com.example.real_madrid_museo.ui.vitrina.TrofeoActivity
import com.example.real_madrid_museo.ui.vitrina.TrofeoManager

// Colores oficiales
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

@Composable
fun MainScreen(nombre: String, perfil: String, esInvitado: Boolean, visitas: Int, puntos: Int, ranking: Int, email: String?) {
    val context = LocalContext.current
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        stringResource(R.string.nav_home),
        stringResource(R.string.nav_map),
        stringResource(R.string.nav_camera),
        stringResource(R.string.nav_profile)
    )
    val icons = listOf(Icons.Default.Home, Icons.Default.Map, Icons.Default.QrCodeScanner, Icons.Default.Person)

    var mostrarColeccion by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp).height(72.dp),
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
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MadridBlue.copy(alpha = 0.1f), Color.White)))) {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when (selectedItem) {
                    0 -> DashboardInicio(nombre)
                    1 -> MapScreen()
                    2 -> {
                        // --- ESCÁNER ESCALABLE ---
                        ScannerScreen(onResultFound = { resultado ->
                            when (resultado) {
                                "7" -> {
                                    // El código 7 lanza el Kahoot
                                    val intent = Intent(context, KahootActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "1", "2", "3", "4", "5", "6", "8", "9", "10" -> {
                                    // Aquí puedes definir acciones para los otros números
                                    Toast.makeText(context, "Código $resultado detectado (Próximamente)", Toast.LENGTH_SHORT).show()
                                }
                                "11" -> {
                                    // 1. Marcamos el trofeo como visto (índice 0 es Champions)
                                    // Esto guardará el progreso y lanzará la notificación si completas todos
                                    TrofeoManager.marcarTrofeoVisto(context, 0)

                                    // 2. Abrimos la actividad (Tu código de antes)
                                    val intent = Intent(context, TrofeoActivity::class.java)
                                    intent.putExtra("INDICE_TROFEO", 0)
                                    context.startActivity(intent)
                                }
                                else -> {
                                    // Cualquier otro QR no registrado
                                    Toast.makeText(context, "QR no reconocido: $resultado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                    3 -> {
                        if (mostrarColeccion) {
                            // Si la variable es true, mostramos el Álbum
                            ColeccionTrofeos(onBack = { mostrarColeccion = false })
                        } else {
                            // Si no, mostramos el Perfil normal y le pasamos la función para abrir el álbum
                            PerfilContent(
                                nombre, perfil, esInvitado, visitas, puntos, ranking, email,
                                onAbrirColeccion = { mostrarColeccion = true } // <--- Pasamos esto
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardInicio(nombre: String) {
    LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(stringResource(R.string.dashboard_hello, nombre), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MadridBlue)
            Text(stringResource(R.string.dashboard_subtitle), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(stringResource(R.string.dashboard_next_match), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MadridBlue)
            ) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.dashboard_team_1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("VS", color = MadridGold, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.dashboard_team_2), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(stringResource(R.string.dashboard_latest_news), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val listaImagenes = listOf(
            R.drawable.bernabeu,
            R.drawable.vitrina,
            R.drawable.vestuario
        )

        items(listaImagenes.size) { index ->
            NewsCard(index, listaImagenes[index])
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsCard(index: Int, imageRes: Int) {
    val titles = listOf(
        stringResource(R.string.news_title_1),
        stringResource(R.string.news_title_2),
        stringResource(R.string.news_title_3)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(15.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(titles.getOrElse(index) { "Noticia Real Madrid" }, fontWeight = FontWeight.Bold, maxLines = 2, color = MadridBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.news_time), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PerfilContent(
    nombre: String,
    perfil: String,
    esInvitado: Boolean,
    visitasIniciales: Int,
    puntosIniciales: Int,
    rankingInicial: Int,
    email: String?,
    onAbrirColeccion: () -> Unit
) {
    val context = LocalContext.current
    var visitasActuales by remember { mutableIntStateOf(visitasIniciales) }
    var puntosActuales by remember { mutableIntStateOf(puntosIniciales) }
    var rankingActual by remember { mutableIntStateOf(rankingInicial) }

    LaunchedEffect(Unit) {
        if (!esInvitado && email != null) {
            val db = DatabaseHelper(context)
            val userData = db.getUserDetails(email)
            if (userData != null) {
                visitasActuales = userData["visits"] as? Int ?: visitasIniciales
                puntosActuales = userData["points"] as? Int ?: puntosIniciales
                rankingActual = userData["ranking"] as? Int ?: rankingInicial
            }
        }
    }

    val perfilLocalizado = when (perfil) {
        "ADULTO" -> stringResource(R.string.profile_type_adult)
        "NIÑO" -> stringResource(R.string.profile_type_child)
        "INVITADO" -> stringResource(R.string.profile_type_guest)
        else -> perfil
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(shape = CircleShape, color = MadridBlue.copy(alpha = 0.1f), modifier = Modifier.size(80.dp)) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = if (esInvitado) Color.Gray else MadridBlue, modifier = Modifier.padding(15.dp).fillMaxSize())
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MadridBlue)
                        Text(text = if (esInvitado) stringResource(R.string.profile_guest_title) else stringResource(R.string.profile_member_title), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = if (esInvitado) Color.LightGray else MadridGold, shape = RoundedCornerShape(50), modifier = Modifier.height(24.dp)) {
                            Text(text = " $perfilLocalizado ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MadridBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
                LanguageToggle(currentLanguage = obtenerIdioma(context), onToggle = { cambiarIdioma(context, if (obtenerIdioma(context) == "es") "en" else "es") })
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (esInvitado) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MadridBlue.copy(alpha = 0.05f))) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MadridBlue)
                        Text(stringResource(R.string.profile_register_prompt), textAlign = TextAlign.Center, color = MadridBlue)
                    }
                }
            }
        } else {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MadridBlue), elevation = CardDefaults.cardElevation(8.dp)) {
                    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        EstadisticaItem(stringResource(R.string.stats_visits), visitasActuales.toString())
                        EstadisticaItem(stringResource(R.string.stats_points), puntosActuales.toString())
                        EstadisticaItem(stringResource(R.string.stats_ranking), if (rankingActual > 0) "#$rankingActual" else "--")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text(stringResource(R.string.achievements_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MadridBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                        LogroItem(Icons.Default.EmojiEvents, stringResource(R.string.achievement_welcome), true)
                        Column(
                            modifier = Modifier.clickable { onAbrirColeccion() },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val tieneAlguno = remember { TrofeoManager.obtenerProgreso(context) > 0 }

                            LogroItem(
                                icono = Icons.Default.EmojiEvents, // O Icons.Default.Collections
                                nombre = "Coleccionista", // O stringResource
                                desbloqueado = tieneAlguno
                            )
                        }
                        LogroItem(Icons.Default.Map, stringResource(R.string.achievement_explorer), false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text(stringResource(R.string.rewards_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if(esInvitado) Color.LightGray else MadridGold)) {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = if(esInvitado) stringResource(R.string.reward_coming_soon) else stringResource(R.string.reward_discount), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MadridBlue)
                        Text(stringResource(R.string.reward_store), fontWeight = FontWeight.Bold, color = if(esInvitado) MadridBlue else Color.White)
                    }
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MadridBlue, modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
fun EstadisticaItem(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MadridGold)
        Text(titulo, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}

@Composable
fun LogroItem(icono: ImageVector, nombre: String, desbloqueado: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(50.dp).background(color = if (desbloqueado) MadridBlue.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f), shape = CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icono, contentDescription = null, tint = if (desbloqueado) MadridBlue else Color.Gray)
        }
        Text(nombre, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if(desbloqueado) Color.Black else Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Vista Socio")
@Composable
fun MainScreenSocioPreview() {
    MainScreen("Javier Madridista", "ADULTO", false, 5, 250, 1, "javier@madrid.com")
}