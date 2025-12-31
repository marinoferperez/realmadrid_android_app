package com.example.real_madrid_museo.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.window.Dialog
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
import com.example.real_madrid_museo.ui.linea.SalaHistorica
import com.example.real_madrid_museo.ui.linea.PuzzleHistoricoScreen
import com.example.real_madrid_museo.ui.linea.EraManager

// Colores oficiales
val MadridBlue = Color(0xFF002D72)
val MadridGold = Color(0xFFFEBE10)

@Composable
fun MainScreen(nombre: String, perfil: String, esInvitado: Boolean, visitas: Int, puntos: Int, ranking: Int, email: String?) {
    val context = LocalContext.current
    val historyRoomName = stringResource(R.string.map_history)
    
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var mostrarColeccion by rememberSaveable { mutableStateOf(false) }
    var mostrarSalaHistorica by rememberSaveable { mutableStateOf(false) }
    var mostrarPuzzle by rememberSaveable { mutableStateOf(false) }

    val items = listOf(
        stringResource(R.string.nav_home),
        stringResource(R.string.nav_map),
        stringResource(R.string.nav_camera),
        stringResource(R.string.nav_profile)
    )
    val icons = listOf(Icons.Default.Home, Icons.Default.Map, Icons.Default.QrCodeScanner, Icons.Default.Person)

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
                            onClick = { 
                                selectedItem = index 
                                mostrarColeccion = false
                                mostrarSalaHistorica = false
                                mostrarPuzzle = false
                            },
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
                    0 -> {
                        if (mostrarSalaHistorica) {
                            SalaHistorica(email = email ?: "invitado", onBack = { mostrarSalaHistorica = false })
                        } else {
                            DashboardInicio(nombre)
                        }
                    }
                    1 -> MapScreen(onNavigate = { nombreSala ->
                        if (nombreSala.equals(historyRoomName, ignoreCase = true)) {
                            selectedItem = 0
                            mostrarSalaHistorica = true
                        }
                    })
                    2 -> {
                        ScannerScreen(onResultFound = { resultado ->
                            when (resultado) {
                                "7" -> {
                                    val intent = Intent(context, KahootActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "1", "2", "3", "4", "5", "6", "8", "9", "10" -> {
                                    val numPieza = when(resultado) {
                                        "1" -> 1; "2" -> 2; "3" -> 3; "4" -> 4; "5" -> 5
                                        "6" -> 6; "8" -> 7; "9" -> 8; "10" -> 9; else -> 0
                                    }
                                    if (numPieza > 0) {
                                        EraManager.desbloquearEra(context, email ?: "invitado", numPieza - 1)
                                        Toast.makeText(context, "¡Pieza número $numPieza desbloqueada!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "11" -> {
                                    TrofeoManager.marcarTrofeoVisto(context, 0)
                                    val intent = Intent(context, TrofeoActivity::class.java)
                                    intent.putExtra("INDICE_TROFEO", 0)
                                    context.startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(context, "QR no reconocido: $resultado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                    3 -> {
                        if (mostrarColeccion) {
                            ColeccionTrofeos(onBack = { mostrarColeccion = false })
                        }
                        else if (mostrarPuzzle) {
                            PuzzleHistoricoScreen(email = email ?: "invitado", onBack = { mostrarPuzzle = false })
                        }
                        else {
                            PerfilContent(
                                nombre, perfil, esInvitado, visitas, puntos, ranking, email,
                                onAbrirColeccion = { mostrarColeccion = true },
                                onAbrirPuzzle = { mostrarPuzzle = true }
                            )
                        }
                    }
                }
            }

            // --- LÓGICA DE VISIBILIDAD DEL SELECTOR DE IDIOMA ---
            // Se muestra en INICIO (0), PERFIL (3) y SALA HISTÓRICA
            // Se oculta en MAPA (1) y CÁMARA (2)
            val mostrarSelectorGlobal = (selectedItem == 0 || selectedItem == 3 || (selectedItem == 0 && mostrarSalaHistorica)) && 
                                       !mostrarColeccion && !mostrarPuzzle

            if (mostrarSelectorGlobal) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 16.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    LanguageToggle(
                        currentLanguage = obtenerIdioma(context),
                        onToggle = { cambiarIdioma(context, if (obtenerIdioma(context) == "es") "en" else "es") }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardInicio(nombre: String) {
    val context = LocalContext.current
    
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
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(25.dp),
                elevation = CardDefaults.elevatedCardElevation(10.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.santiago_bernabeu),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MadridBlue.copy(alpha = 0.6f),
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(80.dp),
                                shadowElevation = 4.dp
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.real_madrid),
                                    contentDescription = "Real Madrid",
                                    modifier = Modifier.padding(10.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.dashboard_team_1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("VS", color = MadridGold, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                            Surface(color = MadridGold, shape = RoundedCornerShape(10.dp)) {
                                Text("DOM 21:00", color = MadridBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(70.dp),
                                shadowElevation = 4.dp
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.villacarrillo),
                                    contentDescription = "Villacarrillo",
                                    modifier = Modifier.padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.dashboard_team_2), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(stringResource(R.string.dashboard_latest_news), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val listaUrls = listOf(
            "https://www.realmadrid.com/es-ES/noticias/futbol/primer-equipo/actualidad/asi-avanzan-las-obras-del-santiago-bernabeu-21-02-2024",
            "https://www.realmadrid.com/es-ES/noticias/futbol/primer-equipo/actualidad/mbappe-visita-el-museo-real-madrid",
            "https://www.realmadrid.com/es-ES/tienda"
        )
        val listaImagenes = listOf(R.drawable.bernabeu, R.drawable.vitrina, R.drawable.vestuario)

        items(listaImagenes.size) { index ->
            NewsCard(index, listaImagenes[index], listaUrls.getOrElse(index) { "https://www.realmadrid.com" })
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.dashboard_social_networks), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MadridBlue)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SocialIconItem(iconRes = R.drawable.tiktok, name = "TikTok", url = "https://www.tiktok.com/@realmadrid")
                    SocialIconItem(iconRes = R.drawable.ig, name = "Instagram", url = "https://www.instagram.com/realmadrid")
                    SocialIconItem(iconRes = R.drawable.twitter, name = "Twitter", url = "https://twitter.com/realmadrid")
                    SocialIconItem(iconRes = R.drawable.facebook, name = "Facebook", url = "https://www.facebook.com/RealMadrid")
                }
            }
        }
    }
}

@Composable
fun SocialIconItem(iconRes: Int, name: String, url: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    ) {
        Surface(shape = CircleShape, modifier = Modifier.size(50.dp), color = Color.Transparent) {
            Image(painter = painterResource(id = iconRes), contentDescription = name, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 10.sp, color = MadridBlue, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun NewsCard(index: Int, imageRes: Int, url: String) {
    val context = LocalContext.current
    val titles = listOf(
        stringResource(R.string.news_title_1),
        stringResource(R.string.news_title_2),
        stringResource(R.string.news_title_3)
    )

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
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
    onAbrirColeccion: () -> Unit,
    onAbrirPuzzle: () -> Unit
) {
    val context = LocalContext.current
    var visitasActuales by remember { mutableIntStateOf(visitasIniciales) }
    var puntosActuales by remember { mutableIntStateOf(puntosIniciales) }
    var rankingActual by remember { mutableIntStateOf(rankingInicial) }
    var mostrarRankingDialog by remember { mutableStateOf(false) }

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

    if (mostrarRankingDialog) {
        RankingDialog(onDismiss = { mostrarRankingDialog = false })
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
                        EstadisticaItem(
                            stringResource(R.string.stats_ranking),
                            if (rankingActual > 0) "#$rankingActual" else "--",
                            onClick = { mostrarRankingDialog = true }
                        )
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
                                icono = Icons.Default.EmojiEvents,
                                nombre = "Coleccionista",
                                desbloqueado = tieneAlguno
                            )
                        }
                        Column(
                            modifier = Modifier.clickable { onAbrirPuzzle() },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val tieneEras = remember { com.example.real_madrid_museo.ui.linea.EraManager.obtenerProgreso(context,email ?: "invitado") > 0 }
                            LogroItem(
                                icono = Icons.Default.Extension,
                                nombre = "Puzzle",
                                desbloqueado = tieneEras
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
fun RankingDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var rankingList by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val db = DatabaseHelper(context)
        rankingList = db.getAllUsersRanking()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(max = 600.dp)
        ) {
            Column {
                // Cabecera
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(MadridBlue, MadridBlue.copy(alpha = 0.8f))))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MadridGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.ranking_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Lista
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(rankingList) { index, item ->
                        val rank = index + 1
                        val isTop3 = rank <= 3
                        val backgroundColor = if (rank % 2 == 0) Color(0xFFF5F5F5) else Color.White
                        
                        // Determinar color de medalla/rank
                        val rankColor = when (rank) {
                            1 -> MadridGold // Oro
                            2 -> Color(0xFFC0C0C0) // Plata
                            3 -> Color(0xFFCD7F32) // Bronce
                            else -> MadridBlue
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            elevation = if (isTop3) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Posición / Medalla
                                Surface(
                                    shape = CircleShape,
                                    color = if (isTop3) rankColor.copy(alpha = 0.2f) else Color.Transparent,
                                    modifier = Modifier.size(40.dp),
                                    contentColor = rankColor
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#$rank",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Nombre
                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MadridBlue,
                                    modifier = Modifier.weight(1f)
                                )

                                // Puntos
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MadridBlue,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = "${item.second} pts",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Botón cerrar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MadridBlue),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(stringResource(R.string.ranking_close), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticaItem(titulo: String, valor: String, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
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
