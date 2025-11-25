package com.example.notasymedia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel
import androidx.compose.ui.res.stringResource
import com.example.notasymedia.R
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToForm: (Int) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: NotaViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val todasNotas by viewModel.todasNotas.collectAsState(initial = emptyList())
    val notas by viewModel.notas.collectAsState(initial = emptyList())
    val tareas by viewModel.tareas.collectAsState(initial = emptyList())
    val completadas by viewModel.completadas.collectAsState(initial = emptyList())

    // Determinar la lista base según la pestaña seleccionada
    val baseList = when (selectedTabIndex) {
        0 -> todasNotas
        1 -> notas
        2 -> tareas
        3 -> completadas
        else -> emptyList()
    }

    // Aplicar filtro de búsqueda si está activo
    val listaNotas = remember(baseList, searchQuery, isSearchActive) {
        if (isSearchActive && searchQuery.isNotBlank()) {
            baseList.filter { nota ->
                nota.titulo.contains(searchQuery, ignoreCase = true) ||
                nota.descripcion.contains(searchQuery, ignoreCase = true)
            }
        } else {
            baseList
        }
    }

    Scaffold(
        topBar = { 
            if (isSearchActive) {
                SearchToolbar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onCloseSearch = { 
                        isSearchActive = false 
                        searchQuery = ""
                    }
                )
            } else {
                AppToolbar(
                    titleResId = R.string.title_notas_tareas,
                    onSearchClick = { isSearchActive = true }
                ) 
            }
        },
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

            if (listaNotas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron resultados" else "No hay elementos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Cerrar búsqueda")
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Limpiar")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun TaskCard(
    nota: NotaEntity,
    modifier: Modifier = Modifier,
    onAbrir: () -> Unit = {},
    onCompletar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaTexto = formatter.format(nota.fechaCreacion)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onAbrir),
        colors = CardDefaults.cardColors(
            containerColor = if (nota.esCompletada) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (nota.tipo == TipoNota.TAREA) {
                val contentDesc = stringResource(
                    if (nota.esCompletada) R.string.action_desmarcar else R.string.action_completar
                )
                RadioButton(
                    selected = nota.esCompletada,
                    onClick = onCompletar,
                    modifier = Modifier.semantics {
                        this.contentDescription = contentDesc
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = fechaTexto,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = nota.descripcion.take(50) + if (nota.descripcion.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.label_tipo, nota.tipo.name),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_eliminar))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    titleResId: Int,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(titleResId),
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
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.action_buscar))
            }
            IconButton(onClick = { /* Acción de menú */ }) {
                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.action_menu))
            }
        }
    )
}

@Composable
fun FilterTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabTitleResourceIds = listOf(
        R.string.tab_todas,
        R.string.tab_notas,
        R.string.tab_tareas,
        R.string.tab_cumplidas
    )

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabTitleResourceIds.forEachIndexed { index, titleResId ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) },
                text = { Text(stringResource(titleResId)) }
            )
        }
    }
}

@Composable
fun CustomFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_crear_nuevo))
    }
}

@Preview(showBackground = true, locale = "fr")
@Composable
fun PreviewMainScreen() {
    NotasYMediaTheme {
        MainScreen(onNavigateToForm = {},
            onNavigateToDetail = {}
        )
    }
}
