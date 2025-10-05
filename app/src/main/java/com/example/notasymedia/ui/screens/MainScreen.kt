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
import androidx.compose.ui.unit.dp
import com.example.notasymedia.ui.theme.NotasYMediaTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { AppToolbar() }, // Barra Superior Búsqueda/Orden
        floatingActionButton = { CustomFab(onClick = {}) }
    ) { paddingValues ->
        // Contiene la lista y las pestañas
        Column(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            FilterTabs() // Pestañas para Todos/Notas/Tareas/Cumplidas

            // Simulación de la lista de notas/tareas
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                // Generación de 5 tarjetas de ejemplo
                items(5) {
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
fun FilterTabs() {
    // Implementación del Segmented Control (Tabs)
    TabRow(selectedTabIndex = 0) {
        listOf("Todas", "Notas", "Tareas", "Cumplidas").forEachIndexed { index, title ->
            Tab(
                selected = index == 0,
                onClick = { /* Cambiar filtro */ },
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
        MainScreen()
    }
}