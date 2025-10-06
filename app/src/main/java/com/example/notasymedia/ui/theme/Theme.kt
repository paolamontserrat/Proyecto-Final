package com.example.notasymedia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de Colores Clara (LightColorPalette)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE054F3),       // Botones y Elementos Activos
    secondary = Color(0xFFA278A6),    // Clasificadores y Filtros (RF-06)
    tertiary = Color(0xFF705B2E),
    background = Color(0xFFF9FFF9),   // Fondo Claro
    surface = Color(0xFFFFFFFF),      // Fondo de Cards/Tarjetas
    error = Color(0xFFBA1A1A),
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1C1A)
)

// Paleta de Colores Oscura (DarkColorPalette)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF63DDAB),
    secondary = Color(0xFFB4CCBB),
    tertiary = Color(0xFFDEC598),
    background = Color(0xFF1A1C1A),   // Fondo Oscuro
    surface = Color(0xFF232824),      // Fondo de Cards/Tarjetas Oscuro
    error = Color(0xFFBA1A1A),
    onPrimary = Color(0xFF003820),
    onBackground = Color(0xFFE2E3DF)
)

@Composable
fun NotasYMediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la tipografía definida
        shapes = Shapes,         // Usa las formas definidas
        content = content
    )
}

