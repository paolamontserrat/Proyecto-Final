package com.example.notasymedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notasymedia.ui.screens.DetailScreen
import com.example.notasymedia.ui.screens.EntryFormScreen
import com.example.notasymedia.ui.screens.MainScreen
import com.example.notasymedia.ui.screens.MasterDetailLayout
import com.example.notasymedia.ui.theme.NotasYMediaTheme

class MainActivity : ComponentActivity() {

    // Launcher para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido
        } else {
            // Permiso denegado, podrías mostrar un mensaje
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotasYMediaTheme {
                // Solicitar permiso de notificaciones en Android 13+
                RequestNotificationPermission()

                val windowSizeClass = calculateWindowSizeClass(this)
                AppContent(windowSizeClass.widthSizeClass, intent)
            }
        }
    }

    @Composable
    private fun RequestNotificationPermission() {
        // La solicitud solo es necesaria para SDK 33 (Android 13) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LaunchedEffect(Unit) {
                when {
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // El permiso ya está concedido
                    }
                    else -> {
                        // Solicitar el permiso
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent(widthSizeClass: WindowWidthSizeClass, intent: Intent?) {
    //Crear el controlador de navegacion
    val navController = rememberNavController()
    
    // Manejo de notificación al inicio (deep link manual)
    LaunchedEffect(intent) {
        if (intent != null && intent.hasExtra("taskId")) {
            val taskId = intent.getIntExtra("taskId", -1)
            if (taskId != -1) {
                navController.navigate("detail/$taskId")
            }
        }
    }

    if (widthSizeClass == WindowWidthSizeClass.Expanded) {
        MasterDetailLayout(
            onNavigateToEdit = { id ->
                if (id == -1) {
                    navController.navigate("entry_form/-1")
                } else {
                    navController.navigate("entry_form/$id")
                }
            }
        )
    } else {
        //Definir las Rutas
        NavHost(
            navController = navController,
            startDestination = "main_list" // La pantalla de inicio
        ) {
            //Pantalla Principal (Lista)
            composable("main_list") {
                MainScreen(
                    onNavigateToForm = { id: Int ->
                        navController.navigate("entry_form/$id")
                    },
                    onNavigateToDetail = { id: Int ->
                        navController.navigate("detail/$id")
                    }
                )
            }
            //Pantalla de Detalle
            composable(
                route = "detail/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
                DetailScreen(
                    itemId = itemId,
                    onNavigateToEdit = { navController.navigate("entry_form/$itemId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            //Pantalla de Creacion/Edicion (Formulario)
            composable(
                route = "entry_form/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: -1
                EntryFormScreen(
                    itemId = itemId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
