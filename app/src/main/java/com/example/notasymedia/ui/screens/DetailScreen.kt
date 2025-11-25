package com.example.notasymedia.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.notasymedia.R
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.viewmodel.MultimediaState
import com.example.notasymedia.viewmodel.NotaViewModel
import com.example.notasymedia.viewmodel.toState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: Int,
    onNavigateToEdit: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: NotaViewModel = viewModel()
) {
    var nota by remember { mutableStateOf<NotaEntity?>(null) }
    var multimedia by remember { mutableStateOf<List<MultimediaState>>(emptyList()) }

    LaunchedEffect(itemId) {
        nota = viewModel.obtenerPorId(itemId)
        multimedia = viewModel.obtenerMultimedia(itemId).map { it.toState() }
    }

    Scaffold(
        topBar = {
            DetailToolbar(
                itemId = itemId,
                onNavigateToEdit = onNavigateToEdit,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = { if (nota?.tipo == TipoNota.TAREA) TaskActionsBottomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (nota == null) {
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
                            contentDescription = stringResource(R.string.status_completada),
                            tint = if (nota!!.esCompletada) Color.Green else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    if (nota!!.tipo == TipoNota.TAREA && nota!!.fechaVencimiento != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaHora = formatter.format(nota!!.fechaVencimiento!!)
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
                Text(stringResource(R.string.label_adjuntos_multimedia), style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                if (multimedia.isEmpty()) {
                    Text("Sin archivos adjuntos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        multimedia.forEach { item ->
                             // Reimplementación local para DetailScreen para asegurar que se vea bien
                             // y evitar problemas de importación/dependencias de EntryFormScreen si MultimediaItemView no es perfecto
                             // También para ajustar tamaños (imagen completa)
                             MultimediaItemViewDetail(item)
                        }
                    }
                }
                
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MultimediaItemViewDetail(item: MultimediaState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), // Permitir que crezca para ver imagen completa
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            when (item.tipo) {
                "FOTO" -> {
                    // Mostrar imagen completa con relación de aspecto original si es posible, o fija pero grande
                    AsyncImage(
                        model = Uri.parse(item.uri),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth, // Llenar ancho, ajustar alto
                        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp)
                    )
                }
                "VIDEO" -> {
                    // En detalle queremos reproducir el video
                    VideoPlayerView(uri = Uri.parse(item.uri))
                }
                "AUDIO" -> {
                     AudioPlayerView(uri = Uri.parse(item.uri))
                }
                else -> {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.InsertDriveFile, null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = item.uri.substringAfterLast("/"))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToolbar(
    itemId: Int,
    onNavigateToEdit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Detalle") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        actions = {
            IconButton(onClick = onNavigateToEdit) {
                Icon(Icons.Filled.Edit, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun TaskActionsBottomBar() {
    BottomAppBar(
        actions = {
            Button(onClick = { /* Posponer - Implementar logica */ }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Timer, null)
                Spacer(Modifier.width(4.dp))
                Text("Posponer")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { /* Eliminar - Implementar logica */ }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Delete, null)
                Spacer(Modifier.width(4.dp))
                Text("Eliminar")
            }
        },
        modifier = Modifier.fillMaxWidth().height(64.dp)
    )
}
