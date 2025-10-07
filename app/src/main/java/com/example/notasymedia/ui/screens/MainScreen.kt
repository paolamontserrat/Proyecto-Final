package com.example.notasymedia.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun MainScreen(modifier: Modifier = Modifier, onNavigateToForm: () -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) } // <-- Nuevo estado

    Scaffold(
        topBar = { AppToolbar() },
        floatingActionButton = { CustomFab(onClick = onNavigateToForm ) }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).fillMaxSize()) {

            // 2. Pasar el estado y el setter a FilterTabs
            FilterTabs(selectedTabIndex = selectedTabIndex, onTabSelected = { selectedTabIndex = it })

            // Simulación de la lista de notas/tareas
            LazyColumn(contentPadding = PaddingValues(16.dp)) {

                // 3. Simular el filtrado de la lista:
                val listCount = when (selectedTabIndex) {
                    0 -> 5 // Todas: 5 elementos de ejemplo
                    1 -> 3 // Notas: 3 elementos de ejemplo
                    2 -> 2 // Tareas: 2 elementos de ejemplo
                    3 -> 1 // Cumplidas: 1 elemento de ejemplo
                    else -> 0
                }

                items(count = listCount) {
                    TaskCard(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
    }
}


@Composable
fun TaskCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tarea/Nota de Ejemplo",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar() {
    CenterAlignedTopAppBar (
        title = { Text("Notas y Tareas") },
        actions = {
            // Búsqueda
            IconButton(onClick = { /* Iniciar Búsqueda */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            }
            // Ordenamiento
            IconButton(onClick = { /* Mostrar Opciones de Orden */ }) {
                Icon(Icons.Filled.Sort, contentDescription = "Ordenar")
            }
        }
    )
}

@Composable
fun FilterTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabTitles = listOf("Todas", "Notas", "Tareas", "Cumplidas")

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun CustomFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Filled.Add, contentDescription = "Crear Nuevo")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    NotasYMediaTheme {
        MainScreen(onNavigateToForm = {})
    }
}