package com.example.real_madrid_museo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.real_madrid_museo.ui.ScannerScreen
import com.example.real_madrid_museo.ui.Prueba.PruebaScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "scanner"
    ) {

        composable("scanner") {
            ScannerScreen(
                onNavigateToPrueba = {
                    navController.navigate("prueba") {
                        popUpTo("scanner") { inclusive = true }
                    }
                }
            )
        }

        composable("prueba") {
            PruebaScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
