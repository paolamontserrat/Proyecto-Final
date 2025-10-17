package com.example.notasymedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import com.example.notasymedia.ui.screens.DetailScreen
import com.example.notasymedia.ui.screens.EntryFormScreen
import com.example.notasymedia.ui.screens.MainScreen
import com.example.notasymedia.ui.screens.MasterDetailLayout
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // ViewModel para pruebas (temporal)
    private lateinit var testViewModel: NotaViewModel

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crea ViewModel para pruebas

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
    // 1. Crear el controlador de navegacion
    val navController = rememberNavController()
    if (widthSizeClass == WindowWidthSizeClass.Expanded) {
        MasterDetailLayout(
            selectedItemId = -1,
            onNavigateToEdit = { navController.navigate("entry_form/-1") },
            onNavigateToDetail = { id -> navController.navigate("detail/$id") },
            navController = navController
        )
    } else {
        // 2. Definir las Rutas
        NavHost(
            navController = navController,
            startDestination = "main_list" // La pantalla de inicio
        ) {
            // Ruta 1: Pantalla Principal (Lista)
            composable("main_list") {
                // Pasa las acciones de navegacion con tipos explicitos (Int para ID)
                MainScreen(
                    onNavigateToForm = { navController.navigate("entry_form/$id") },
                    onNavigateToDetail = { id: Int -> navController.navigate("detail/$id") }
                )
            }
            // Ruta 2: Pantalla de Detalle
            composable(
                route = "detail/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
                DetailScreen(
                    itemId = itemId, // Parametro con nombre explicit
                    onNavigateToEdit = { navController.navigate("entry_form/$itemId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Ruta 3: Pantalla de Creacion/Edicion (Formulario)
            composable(
                route = "entry_form/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: -1
                EntryFormScreen(
                    itemId = itemId, // Parametro con nombre explicit
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}