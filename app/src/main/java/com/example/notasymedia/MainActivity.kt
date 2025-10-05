// Archivo: MainActivity.kt

package com.example.notasymedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import com.example.notasymedia.ui.screens.MainScreen
import com.example.notasymedia.ui.screens.MasterDetailLayout // Lo crearemos abajo
import com.example.notasymedia.ui.theme.NotasYMediaTheme

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
    // Implementación del RF-17: Soporte Tablet/Smartphone
    if (widthSizeClass == WindowWidthSizeClass.Expanded) {
        // Pantalla Ancha (Tablet o Escritorio)
        MasterDetailLayout()
    } else {
        // Pantalla Estrecha (Smartphone)
        MainScreen()
    }
}