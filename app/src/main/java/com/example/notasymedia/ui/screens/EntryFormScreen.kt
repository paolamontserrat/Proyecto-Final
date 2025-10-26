package com.example.notasymedia.ui.screens

import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

import androidx.compose.ui.res.stringResource // <-- ¡IMPORTANTE!
import com.example.notasymedia.R // <-- ¡IMPORTANTE!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(
    modifier: Modifier = Modifier,
    itemId: Int = -1, // -1 para nuevo, ID positivo para editar
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: NotaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotaViewModel(context) as T
            }
        }
    )

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(false) }
    var fechaVencimiento by remember { mutableStateOf<Date?>(null) }
    var nota by remember { mutableStateOf<NotaEntity?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Carga la nota si es edición
    LaunchedEffect(itemId) {
        if (itemId != -1) {
            val loadedNota = viewModel.obtenerPorId(itemId)
            Log.d("EntryFormScreen", "Cargando nota con ID $itemId: $loadedNota")
            nota = loadedNota
            loadedNota?.let {
                titulo = it.titulo
                descripcion = it.descripcion
                isTask = it.tipo == TipoNota.TAREA
                fechaVencimiento = it.fechaVencimiento
            } ?: run {
                Log.d("EntryFormScreen", "Nota no encontrada para ID $itemId")
                // Reset states si no existe
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
                        val tipo = if (isTask) TipoNota.TAREA else TipoNota.NOTA
                        if (nota != null) {
                            // EDICIÓN: Copia la original y actualiza solo lo necesario (preserva fechaCreacion)
                            val updatedNota = nota!!.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                tipo = tipo,
                                fechaVencimiento = if (isTask) fechaVencimiento else null
                                // esCompletada se mantiene igual
                            )
                            viewModel.actualizar(updatedNota)
                        } else {
                            // NUEVA: Crea desde cero
                            viewModel.insertarNueva(titulo, descripcion, tipo, if (isTask) fechaVencimiento else null)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    stringResource(
                        if (itemId != -1) R.string.button_actualizar else R.string.button_guardar
                    )
                )
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
                label = { Text(stringResource(R.string.label_titulo)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(R.string.label_descripcion)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            if (isTask) {
                Spacer(Modifier.height(16.dp))
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.label_fecha_vencimiento,
                            fechaVencimiento?.let { formatter.format(it) } ?: stringResource(R.string.status_no_seleccionada)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showDatePicker = true }) {
                        Text(stringResource(R.string.button_seleccionar_fecha))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.label_adjuntar_multimedia), style = MaterialTheme.typography.titleMedium)
            MediaTypeSelector(onAttachClicked = { showMediaSheet = true })

            Text(stringResource(R.string.placeholder_miniaturas))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        fechaVencimiento = Date(millis)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.button_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.button_cancelar))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showMediaSheet) {
        MediaPickerBottomSheet(onDismiss = { showMediaSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormToolbar(isTask: Boolean, onNavigateBack: () -> Unit) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    CenterAlignedTopAppBar (
        title = {
            Text(
                // 10. Localización condicional del título "Nueva Tarea" / "Nueva Nota"
                text = stringResource(
                    if (isTask) R.string.title_nueva_tarea else R.string.title_nueva_nota
                ),
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                // 11. Localización del contentDescription "Volver"
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_volver))
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
            // 12. Localización del texto "Nota"
            Text(stringResource(R.string.label_nota))
        }
        Spacer(Modifier.width(8.dp))
        // Botón TAREA
        Button(
            onClick = { onToggle(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTask) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            // 13. Localización del texto "Tarea"
            Text(stringResource(R.string.label_tarea))
        }
    }
}

@Composable
fun MediaTypeSelector(onAttachClicked: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {

        // FOTO / VIDEO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 14. Localización de contentDescription y texto "Foto/Video"
                Icon(Icons.Filled.Camera, contentDescription = stringResource(R.string.action_adjunto_foto), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_foto), style = MaterialTheme.typography.bodySmall)
            }
        }

        // GRABAR AUDIO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 15. Localización de contentDescription y texto "Grabar Audio"
                Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.action_adjunto_audio), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_audio), style = MaterialTheme.typography.bodySmall)
            }
        }

        // ELECCIONAR ARCHIVO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 16. Localización de contentDescription y texto "Seleccionar Archivo"
                Icon(Icons.Filled.Folder, contentDescription = stringResource(R.string.action_adjunto_archivo), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_archivo), style = MaterialTheme.typography.bodySmall)
            }
        }

        // ESCRIBIR DESCRIPCIÓN
        IconButton(onClick = {}) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 17. Localización de contentDescription y texto "Escribir descripción"
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_adjunto_descripcion), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_descripcion), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 18. Localización del título "Selecciona Fuente Multimedia"
            Text(stringResource(R.string.title_selecciona_fuente), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            // 19. Localización de los botones grandes
            Button(onClick = { /* Tomar Foto/Video */ }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.button_tomar_foto_video)) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* Grabar Audio */ }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.button_grabar_audio)) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* Seleccionar Archivo */ }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.button_seleccionar_archivo_bs)) }
        }
    }
}

@Preview(showBackground = true, locale = "de")
@Composable
fun PreviewEntryFormScreen() {
    NotasYMediaTheme {
        EntryFormScreen(onNavigateBack = {}

        )
    }
}