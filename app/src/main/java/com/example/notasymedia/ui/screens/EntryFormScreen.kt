package com.example.notasymedia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(
    modifier: Modifier = Modifier,
    itemId: Int = -1, // -1 para nuevo, ID positivo para editar
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: NotaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotaViewModel(context) as T
            }
        }
    )

    // Estados para campos
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(false) }
    var fechaVencimiento by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar datos si es edición
    LaunchedEffect(itemId) {
        if (itemId != -1) {
            val nota = viewModel.obtenerPorId(itemId)
            nota?.let {
                titulo = it.titulo
                descripcion = it.descripcion
                isTask = it.tipo == TipoNota.TAREA
                fechaVencimiento = it.fechaVencimiento
            } ?: run {
                // Si no se encuentra la nota, inicializar vacio
                titulo = ""
                descripcion = ""
                isTask = false
                fechaVencimiento = null
            }
        }
    }

    Scaffold(
        topBar = { FormToolbar(isTask, onNavigateBack) },
        bottomBar = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val nota = NotaEntity(
                            id = if (itemId != -1) itemId else 0, // 0 para nuevo (Room auto-genera)
                            titulo = titulo,
                            descripcion = descripcion,
                            tipo = if (isTask) TipoNota.TAREA else TipoNota.NOTA,
                            fechaCreacion = Date(),
                            fechaVencimiento = if (isTask) fechaVencimiento else null,
                            esCompletada = false
                        )
                        if (itemId != -1) {
                            viewModel.actualizar(nota)
                        } else {
                            viewModel.insertarNueva(titulo, descripcion, nota.tipo, fechaVencimiento)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (itemId != -1) "Actualizar" else "Guardar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ClassificationSwitch(isTask) { isTask = it }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            if (isTask) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Fecha de Vencimiento: ${fechaVencimiento?.let { "${it.date}/${it.month + 1}/${it.year + 1900}" } ?: "No seleccionada"}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showDatePicker = true }) {
                        Text("Seleccionar")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Adjuntar Multimedia (RF-02)", style = MaterialTheme.typography.titleMedium)
            MediaTypeSelector(onAttachClicked = { showMediaSheet = true })

            Text("--- Placeholder: ScrollableRow de miniaturas (AttachmentRow) ---")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaVencimiento = Date() // Aquí deberías ajustar con la fecha seleccionada
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            // Placeholder para DatePicker (necesita implementación real)
            Text("Selecciona una fecha", modifier = Modifier.padding(16.dp))
        }
    }

    if (showMediaSheet) {
        MediaPickerBottomSheet(onDismiss = { showMediaSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormToolbar(isTask: Boolean, onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar (
        title = { Text(if (isTask) "Nueva Tarea" else "Nueva Nota") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
    )
}

@Composable
fun ClassificationSwitch(isTask: Boolean, onToggle: (Boolean) -> Unit) {
    // Implementación del selector Nota/Tarea
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón NOTA
        Button(
            onClick = { onToggle(false) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isTask) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text("Nota")
        }
        Spacer(Modifier.width(8.dp))
        // Botón TAREA
        Button(
            onClick = { onToggle(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTask) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text("Tarea")
        }
    }
}

@Composable
fun MediaTypeSelector(onAttachClicked: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {

        // FOTO / VIDEO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Camera, contentDescription = "Foto/Video", modifier = Modifier.size(32.dp))
                Text("Foto/Video", style = MaterialTheme.typography.bodySmall)
            }
        }

        // GRABAR AUDIO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Mic, contentDescription = "Grabar Audio", modifier = Modifier.size(32.dp))
                Text("Grabar Audio", style = MaterialTheme.typography.bodySmall)
            }
        }

        // ELECCIONAR ARCHIVO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Folder, contentDescription = "Seleccionar Archivo", modifier = Modifier.size(32.dp))
                Text("Seleccionar Archivo", style = MaterialTheme.typography.bodySmall)
            }
        }

        // ESCRIBIR DESCRIPCIÓN
        IconButton(onClick = {}) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Edit, contentDescription = "Escribir Descripción", modifier = Modifier.size(32.dp))
                Text("Escribir descripción", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona Fuente Multimedia", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            // Opciones de los Botones grandes para Usabilidad
            Button(onClick = { /* Tomar Foto/Video */ }, modifier = Modifier.fillMaxWidth()) { Text("Tomar Foto/Video") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* Grabar Audio */ }, modifier = Modifier.fillMaxWidth()) { Text("Grabar Audio") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* Seleccionar Archivo */ }, modifier = Modifier.fillMaxWidth()) { Text("Seleccionar Archivo") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntryFormScreen() {
    NotasYMediaTheme {
        EntryFormScreen(onNavigateBack = {})
    }
}