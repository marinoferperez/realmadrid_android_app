package com.example.real_madrid_museo.kahoot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.real_madrid_museo.R
import com.example.real_madrid_museo.ui.onboarding.FondoAnimadoKahoot
import com.example.real_madrid_museo.ui.theme.RealMadridBlue
import com.example.real_madrid_museo.ui.theme.RealMadridGold

@Composable
fun KahootInstructionsScreen(onStart: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FondoAnimadoKahoot()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RealMadridBlue.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, RealMadridGold),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.kahoot_instructions_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = RealMadridGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // NUEVA INSTRUCCIÓN TÁCTIL
                    InstructionItem(
                        icon = Icons.Default.TouchApp,
                        title = stringResource(R.string.kahoot_instructions_touch_title),
                        desc = stringResource(R.string.kahoot_instructions_touch_desc)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    InstructionItem(
                        icon = Icons.Default.Mic,
                        title = stringResource(R.string.kahoot_instructions_voice_title),
                        desc = stringResource(R.string.kahoot_instructions_voice_desc)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    InstructionItem(
                        icon = Icons.Default.Vibration,
                        title = stringResource(R.string.kahoot_instructions_shake_title),
                        desc = stringResource(R.string.kahoot_instructions_shake_desc)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = RealMadridGold),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.kahoot_instructions_start_button),
                            color = RealMadridBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = RealMadridGold.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RealMadridGold,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
