package com.example.notasymedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource // <-- ¡IMPORTANTE para localización!
import com.example.notasymedia.R // <-- ¡IMPORTANTE para recursos!
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
) {
    // 💡 Paso clave: El estado de la nota/tarea seleccionada.
    var currentSelectedItem by remember { mutableStateOf(-1) }

    Row(modifier = modifier.fillMaxSize()) {

        // ========= 1. PANEL MAESTRO (MainScreen) =========
        Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToForm = onNavigateToEdit,
                onNavigateToDetail = { id -> currentSelectedItem = id }
            )
        }

        // Separador visual
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                // APLICACIÓN DEL COLOR ROSA/MORADO VIBRANTE (primary)
                .background(MaterialTheme.colorScheme.primary)
        )

        // ========= 2. PANEL DETALLE (DetailScreen o Placeholder) =========
        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            if (currentSelectedItem != -1) {
                DetailScreen(
                    itemId = currentSelectedItem,
                    onNavigateToEdit = { onNavigateToEdit(currentSelectedItem) },
                    onNavigateBack = { currentSelectedItem = -1 }
                )
            } else {
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
            Text(
                // Localización del título
                stringResource(R.string.placeholder_detail_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                // Localización del subtítulo
                stringResource(R.string.placeholder_detail_subtitle)
            )
        }
    }
}


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
        )
    }
}