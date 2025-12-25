package com.example.real_madrid_museo.ui.comun.idiomas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.real_madrid_museo.R

@Composable
fun LanguageToggle(
    currentLanguage: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = if (currentLanguage == "es")
                R.drawable.reino_unido_bandera
            else
                R.drawable.espania_bandera
        ),
        contentDescription = "Change language",
        modifier = modifier
            .size(40.dp) // puedes ajustar tamaño si quieres
            .clickable { onToggle() }
    )
}