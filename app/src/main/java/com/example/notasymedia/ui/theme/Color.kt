package com.example.notasymedia.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)



// Paleta de Colores Clara (LightColorPalette)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE054F3),
    onPrimary = Color.White,
    secondary = Color(0xFFA278A6),
    onSecondary = Color.White,
    tertiary = Color(0xFF705B2E),
    background = Color(0xFFF9FFF9),
    onBackground = Color(0xFF1A1C1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1A),
    error = Color(0xFFBA1A1A)
)

// Paleta de Colores Oscura (DarkColorPalette)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF4ADFF),
    onPrimary = Color(0xFF5A007F),
    secondary = Color(0xFFCFB3D0),
    onSecondary = Color(0xFF381E3B),
    tertiary = Color(0xFFE0C9A5),
    onTertiary = Color(0xFF3A2E00),
    background = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE2E3DF),
    surface = Color(0xFF232824),
    onSurface = Color(0xFFE2E3DF),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)