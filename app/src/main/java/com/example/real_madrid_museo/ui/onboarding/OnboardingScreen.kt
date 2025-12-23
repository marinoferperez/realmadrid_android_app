package com.example.real_madrid_museo.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.comun.LanguageToggle
import com.example.real_madrid_museo.ui.comun.cambiarIdioma
import com.example.real_madrid_museo.ui.comun.obtenerIdioma

@Composable
fun OnboardingScreen(
    slides: List<OnboardingSlide>,
    onFinish: () -> Unit
) {
    val context = LocalContext.current

    // 🔥 FUENTE DE VERDAD DEL IDIOMA
    val currentLanguage by remember {
        mutableStateOf(obtenerIdioma(context))
    }

    val pagerState = rememberPagerState { slides.size }

    Box(modifier = Modifier.fillMaxSize()) {

        FondoAnimado()

        LanguageToggle(
            currentLanguage = currentLanguage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onToggle = {
                val newLanguage = if (currentLanguage == "es") "en" else "es"
                cambiarIdioma(context, newLanguage)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                pageSpacing = 22.dp,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) { page ->

                val slide = slides[page]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Image(
                        painter = painterResource(id = slide.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = stringResource(slide.title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(slide.description),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            IndicadorPagina(
                totalDots = slides.size,
                selectedIndex = pagerState.currentPage
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = pagerState.currentPage > 1) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.start),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}