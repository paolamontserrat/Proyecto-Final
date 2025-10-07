package com.example.notasymedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.notasymedia.ui.screens.MainScreen
import com.example.notasymedia.ui.theme.NotasYMediaTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notasymedia.ui.screens.EntryFormScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotasYMediaTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                AppContent(windowSizeClass.widthSizeClass)
            }
        }
    }
}



@Composable
fun AppContent(widthSizeClass: WindowWidthSizeClass) {
    // 1. Crear el controlador de navegación
    val navController = rememberNavController()

    // 2. Definir las Rutas
    NavHost(
        navController = navController,
        startDestination = "main_list" // La pantalla de inicio
    ) {
        // Ruta 1: Pantalla Principal (Lista)
        composable("main_list") {
            // Aquí se pasa la acción de navegación (navController.navigate)
            MainScreen(
                onNavigateToForm = { navController.navigate("entry_form") }
            )
        }

        // Ruta 2: Pantalla de Creación/Edición (Formulario)
        composable("entry_form") {
            EntryFormScreen(
                // Opción para volver atrás desde el formulario
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}