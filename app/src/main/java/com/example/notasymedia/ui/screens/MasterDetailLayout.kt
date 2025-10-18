package com.example.notasymedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Importar 'remember' y 'mutableStateOf' aquí
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.notasymedia.ui.theme.NotasYMediaTheme


/**
 * Un Layout Master/Detail (Maestro/Detalle) para pantallas grandes o tablets.
 * Muestra la lista de notas (Master) a la izquierda y los detalles (Detail) a la derecha.
 *
 * @param onNavigateToEdit Acción para ir a EntryFormScreen (para editar o crear).
 */
@Composable
fun MasterDetailLayout(
    modifier: Modifier = Modifier,
    onNavigateToEdit: (Int) -> Unit // Acción para ir a EntryFormScreen
    // Eliminamos selectedItemId y navController ya que el estado se maneja internamente
) {
    // 💡 Paso clave: El estado de la nota/tarea seleccionada.
    // Inicializamos a -1 (sin selección)
    var currentSelectedItem by remember { mutableStateOf(-1) }

    Row(modifier = modifier.fillMaxSize()) {

        // ========= 1. PANEL MAESTRO (MainScreen) =========
        Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToForm = onNavigateToEdit,
                // 💡 Al hacer clic en una TaskCard, actualizamos el estado local.
                onNavigateToDetail = { id -> currentSelectedItem = id }
            )
        }

        // Separador visual
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // ========= 2. PANEL DETALLE (DetailScreen o Placeholder) =========
        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            if (currentSelectedItem != -1) {
                // Muestra la DetailScreen real, utilizando el ID del estado.
                // Se asume que esta DetailScreen acepta itemId y onNavigateToEdit.
                DetailScreen(
                    itemId = currentSelectedItem,
                    onNavigateToEdit = { onNavigateToEdit(currentSelectedItem) },
                    onNavigateBack = { currentSelectedItem = -1 } // Opcional: permite deseleccionar
                )
            } else {
                // Muestra un mensaje de marcador de posición si no hay nada seleccionado.
                PlaceholderDetailScreen()
            }
        }
    }
}


/**
 * Marcador de posición para el panel de detalle cuando no hay ninguna nota seleccionada.
 */
@Composable
fun PlaceholderDetailScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Selecciona una Nota o Tarea", style = MaterialTheme.typography.headlineSmall)
            Text("Para ver el detalle en este panel")
        }
    }
}

// ⚠️ NOTA IMPORTANTE: La función DetailScreen duplicada que tenías con parámetros vacíos
// ha sido reemplazada por la función PlaceholderDetailScreen.
// Debes asegurarte de que la DetailScreen real (con la lógica de carga de datos)
// esté disponible y tenga esta firma:
// @Composable fun DetailScreen(itemId: Int, onNavigateToEdit: (Int) -> Unit, onNavigateBack: () -> Unit)


@Preview(
    widthDp = 1024,
    heightDp = 720,
    showBackground = true
)
@Composable
fun PreviewMasterDetailLayout() {
    NotasYMediaTheme {
        MasterDetailLayout(
            onNavigateToEdit = {}
            // Ya no se necesitan selectedItemId, onNavigateToDetail ni navController aquí
        )
    }
}