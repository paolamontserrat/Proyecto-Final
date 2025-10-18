package com.example.notasymedia.ui.screens



import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.viewmodel.NotaViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    itemId: Int,
    onNavigateToEdit: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
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
        Log.d("DetailScreen", "Cargando nota con ID $itemId: $loadedNota")  // Log para depuración
        nota = loadedNota
    }

    Scaffold(
        topBar = { DetailToolbar(itemId = itemId, onNavigateToEdit = onNavigateToEdit, onNavigateBack = onNavigateBack) },
        bottomBar = { TaskActionsBottomBar() }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (nota == null) {
                Text("Cargando nota...", style = MaterialTheme.typography.bodyMedium)
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
                            contentDescription = "Completado",
                            tint = if (nota!!.esCompletada) Color.Green else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (nota!!.esCompletada) "Completada el ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(nota!!.fechaCreacion)}" else "Pendiente",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (nota!!.fechaVencimiento != null) {
                            Text(
                                text = "Vence: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(nota!!.fechaVencimiento!!)}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = nota!!.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(24.dp))
                Text("Adjuntos Multimedia", style = MaterialTheme.typography.titleMedium)
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
            navigationIconContentColor = contentColor, // Flecha de regreso (blanca)
            actionIconContentColor = contentColor // Ícono de Edición (blanco)
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = {
            IconButton(onClick = { onNavigateToEdit(itemId) }) {  // Pasar itemId
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
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
                Icon(Icons.Filled.Timer, contentDescription = "Posponer Tarea")
                Spacer(Modifier.width(4.dp))
                Text("Posponer Tarea")
            }

            Spacer(Modifier.width(8.dp))

            // Eliminar Tarea
            Button(onClick = { /* Eliminar */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar Tarea")
                Spacer(Modifier.width(4.dp))
                Text("Eliminar Tarea")
            }
        },
        modifier = Modifier.fillMaxWidth().height(64.dp)
    )
}

// Fila de miniaturas de adjuntos (RF-12)
@Composable
fun AttachmentRow() {
    // LazyRow permite desplazamiento horizontal
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
        shape = MaterialTheme.shapes.medium, // Usa la forma que definiste en Shapes.kt
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Image, contentDescription = "Adjunto", modifier = Modifier.size(40.dp))
            Text("Archivo", style = MaterialTheme.typography.bodySmall)
        }
    }
}



@Preview(showBackground = true, name = "Detail Screen Preview")
@Composable
fun PreviewDetailScreen() {
    NotasYMediaTheme {
        DetailScreen(itemId = 42)
    }
}