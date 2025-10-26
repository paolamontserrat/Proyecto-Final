package com.example.notasymedia.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource // <-- ¡IMPORTANTE!
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.viewmodel.NotaViewModel
import com.example.notasymedia.R // <-- ¡IMPORTANTE!
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    itemId: Int,
    onNavigateToEdit: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: NotaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotaViewModel(context) as T
            }
        }
    )

    var nota by remember { mutableStateOf<NotaEntity?>(null) }

    LaunchedEffect(itemId) {
        val loadedNota = viewModel.obtenerPorId(itemId)
        Log.d("DetailScreen", "Cargando nota con ID $itemId: $loadedNota")
        nota = loadedNota
    }

    Scaffold(
        topBar = { DetailToolbar(itemId = itemId, onNavigateToEdit = onNavigateToEdit, onNavigateBack = onNavigateBack) },
        bottomBar = { if (nota?.tipo == TipoNota.TAREA) TaskActionsBottomBar() } // Solo mostrar si es tarea
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (nota == null) {
                //Localización: "Cargando nota..."
                Text(stringResource(R.string.status_cargando_nota), style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = nota!!.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (nota!!.tipo == TipoNota.TAREA) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            // Localización: contentDescription
                            contentDescription = stringResource(R.string.status_completada),
                            tint = if (nota!!.esCompletada) Color.Green else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    if (nota!!.tipo == TipoNota.TAREA && nota!!.fechaVencimiento != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaHora = formatter.format(nota!!.fechaVencimiento)
                        Text(
                            text = stringResource(R.string.label_fecha_vencimiento, fechaHora),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (nota!!.esCompletada) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = nota!!.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(24.dp))
                // Localización: Encabezado "Adjuntos Multimedia"
                Text(stringResource(R.string.label_adjuntos_multimedia), style = MaterialTheme.typography.titleMedium)
                AttachmentRow()
            }
        }
    }
}

// Barra Superior para la vista de Detalle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToolbar(
    itemId: Int,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    TopAppBar(
        title = {
            Text(
                text = "",
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                // 6. Localización: contentDescription "Volver"
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_volver))
            }
        },
        actions = {
            IconButton(onClick = { onNavigateToEdit(itemId) }) {
                // 7. Localización: contentDescription "Editar"
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_editar))
            }
        }
    )
}

// Barra Inferior con Acciones de Tarea (RF-08)
@Composable
fun TaskActionsBottomBar() {
    BottomAppBar(
        actions = {
            // Posponer Tarea
            Button(onClick = { /* Posponer */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.weight(1f)) {
                // Localización: contentDescription y texto
                Icon(Icons.Filled.Timer, contentDescription = stringResource(R.string.action_posponer_tarea))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.action_posponer_tarea))
            }

            Spacer(Modifier.width(8.dp))

            // Eliminar Tarea
            Button(onClick = { /* Eliminar */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.weight(1f)) {
                //Localización: contentDescription y texto
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_eliminar_tarea))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.action_eliminar_tarea))
            }
        },
        modifier = Modifier.fillMaxWidth().height(64.dp)
    )
}

// Fila de miniaturas de adjuntos (RF-12)
@Composable
fun AttachmentRow() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(3) { // 3 miniaturas de placeholder
            AttachmentThumbnail()
        }
    }
}

// Miniatura individual
@Composable
fun AttachmentThumbnail() {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 10. Localización: contentDescription "Adjunto"
            Icon(Icons.Filled.Image, contentDescription = stringResource(R.string.action_adjunto_generico), modifier = Modifier.size(40.dp))
            // 11. Localización: Texto "Archivo"
            Text(stringResource(R.string.label_archivo), style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Preview(showBackground = true, locale = "en")
@Composable
fun PreviewDetailScreen() {
    NotasYMediaTheme {
        DetailScreen(itemId = 42

        )
    }
}