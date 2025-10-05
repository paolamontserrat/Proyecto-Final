// Archivo: ui.theme/Shape.kt

package com.example.notasymedia.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // Pequeños elementos
    small = RoundedCornerShape(8.dp),      // Elementos de botón
    medium = RoundedCornerShape(12.dp),    // Tarjetas (como TaskCard)
    large = RoundedCornerShape(0.dp),      // Pantallas o contenedores grandes
    extraLarge = RoundedCornerShape(0.dp)  // Elementos muy grandes
)