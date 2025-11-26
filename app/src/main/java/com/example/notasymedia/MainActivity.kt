package com.example.notasymedia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotasYMediaTheme {
                // Solicitar permiso de notificaciones en Android 13+ con manejo de configuración
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
            val context = LocalContext.current
            var showSettingsDialog by remember { mutableStateOf(false) }

            // Usamos rememberLauncherForActivityResult para manejar la respuesta en Compose
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    val activity = context as? Activity
                    // Si el permiso es denegado y shouldShowRequestPermissionRationale es false,
                    // significa que se denegó permanentemente (o por segunda vez).
                    if (activity != null && !activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        showSettingsDialog = true
                    }
                }
            }
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // Diálogo que dirige a Configuración
            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    title = { Text(stringResource(R.string.permiso_necesario_titulo)) },
                    text = { Text(stringResource(R.string.permiso_necesario_descripcion)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showSettingsDialog = false
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        }) {
                            Text(stringResource(R.string.ir_a_configuracion))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text(stringResource(R.string.action_cancelar))
                        }
                    }
                )
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
