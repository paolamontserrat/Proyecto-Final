package com.example.notasymedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToForm: (Int) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: NotaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotaViewModel(context) as T
            }
        }
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    val listaNotas by produceState<List<NotaEntity>>(
        initialValue = emptyList(),
        key1 = selectedTabIndex,
        producer = {
            when (selectedTabIndex) {
                0 -> viewModel.todasNotas.collect { value = it }
                1 -> viewModel.notas.collect { value = it }
                2 -> viewModel.tareas.collect { value = it }
                3 -> viewModel.completadas.collect { value = it }
                else -> {}
            }
        }
    )

    Scaffold(
        topBar = { AppToolbar(title = "Notas y Tareas") },
        floatingActionButton = { CustomFab(onClick = { onNavigateToForm(-1) }) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            FilterTabs(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(listaNotas) { nota ->
                    TaskCard(
                        nota = nota,
                        modifier = Modifier.padding(bottom = 8.dp),
                        onAbrir = { onNavigateToDetail(nota.id) },
                        onCompletar = { viewModel.marcarCompletada(nota.id, !nota.esCompletada) },
                        onEliminar = { viewModel.eliminar(nota.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    nota: NotaEntity,
    modifier: Modifier = Modifier,
    onAbrir: () -> Unit = {},
    onCompletar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onAbrir), // Hacer la Card clickable para abrir
        colors = CardDefaults.cardColors(
            containerColor = if (nota.esCompletada) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = nota.descripcion.take(50) + if (nota.descripcion.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = "Tipo: ${nota.tipo.name}", style = MaterialTheme.typography.bodySmall)
            }
            if (nota.esCompletada && nota.tipo == TipoNota.TAREA) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "Completada")
            }
            if (nota.tipo == TipoNota.TAREA) {
                IconButton(onClick = onCompletar) {
                    Icon(
                        imageVector = if (nota.esCompletada) Icons.Filled.Undo else Icons.Filled.Check,
                        contentDescription = if (nota.esCompletada) "Desmarcar" else "Completar"
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    title: String,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
            navigationIconContentColor = contentColor
        ),
        actions = {
            IconButton(onClick = { /* Acción de búsqueda */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
            IconButton(onClick = { /* Acción de menú */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menú")
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
        MainScreen(onNavigateToForm = {}, onNavigateToDetail = {})
    }
}