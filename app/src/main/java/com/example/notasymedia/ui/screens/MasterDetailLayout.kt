package com.example.notasymedia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.notasymedia.ui.theme.NotasYMediaTheme



@Composable
fun MasterDetailLayout(
    modifier: Modifier = Modifier,
    selectedItemId: Int, // El ID de la nota/tarea seleccionada
    onNavigateToEdit: (Int) -> Unit, // Acción para ir a EntryFormScreen
    onNavigateToDetail: (Int) -> Unit, // Nueva función para navegar a detalle
    navController: NavController // Añadimos navController como parámetro
) {
    Row(modifier = modifier.fillMaxSize()) {
        // Contiene la lista principal
        Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToForm = onNavigateToEdit,
                onNavigateToDetail = onNavigateToDetail
            )
        }
        // Separador visual
        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())
        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            DetailScreen(
                itemId = selectedItemId,
                onNavigateToEdit = { onNavigateToEdit(selectedItemId)}
            )
        }
    }
}


@Composable
fun DetailScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DETALLE DE NOTA/TAREA", style = MaterialTheme.typography.headlineSmall)
            Text("Prototipo 4 - Panel Detalle")
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
            selectedItemId = 101,
            onNavigateToEdit = {},
            onNavigateToDetail = {},
            navController = rememberNavController() // Usamos un navController de prueba
        )
    }
}