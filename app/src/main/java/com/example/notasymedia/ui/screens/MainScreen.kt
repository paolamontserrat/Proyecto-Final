package com.example.notasymedia.ui.screens

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
import androidx.compose.ui.res.stringResource
import com.example.notasymedia.R
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToForm: (Int) -> Unit,
    onNavigateToDetail: (Int) -> Unit,

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
        // 1. AppToolbar: Le pasamos el ID del recurso
        topBar = { AppToolbar(titleResId = R.string.title_notas_tareas) },
        // 2. CustomFab: No se cambia aquí, se cambia en su definición
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = nota.descripcion.take(50) + if (nota.descripcion.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodySmall
                )
                // 3. Localización de "Tipo: "
                Text(
                    text = stringResource(R.string.label_tipo, nota.tipo.name),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (nota.esCompletada && nota.tipo == TipoNota.TAREA) {
                // 4. Localización de contentDescription
                Icon(imageVector = Icons.Filled.Check, contentDescription = stringResource(R.string.status_completada))
            }
            if (nota.tipo == TipoNota.TAREA) {
                IconButton(onClick = onCompletar) {
                    // 5. Localización condicional de contentDescription
                    Icon(
                        imageVector = if (nota.esCompletada) Icons.Filled.Undo else Icons.Filled.Check,
                        contentDescription = stringResource(
                            if (nota.esCompletada) R.string.action_desmarcar else R.string.action_completar
                        )
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                // 6. Localización de contentDescription
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_eliminar))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    // 7. Cambio de String a Int Resource ID
    titleResId: Int,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                // 8. Uso de stringResource para el título
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
            IconButton(onClick = { /* Acción de búsqueda */ }) {
                // 9. Uso de stringResource para contentDescription
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.action_buscar))
            }
            IconButton(onClick = { /* Acción de menú */ }) {
                // 10. Uso de stringResource para contentDescription
                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.action_menu))
            }
        }
    )
}

@Composable
fun FilterTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    // 11. Reemplazamos las cadenas fijas por IDs de recursos
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
                // 12. Uso de stringResource para el texto de la pestaña
                text = { Text(stringResource(titleResId)) }
            )
        }
    }
}

@Composable
fun CustomFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        // 13. Uso de stringResource para contentDescription
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
