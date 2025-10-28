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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.R
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(
    modifier: Modifier = Modifier,
    itemId: Int = -1,
    onNavigateBack: () -> Unit,
    viewModel: NotaViewModel = viewModel()
) {

    //Observar el estado del formulario
    val formState by viewModel.formState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    //Cargar nota si es edición
    LaunchedEffect(key1 = itemId) {
        viewModel.loadNota(itemId)
    }

    val noSeleccionada = stringResource(R.string.status_no_seleccionada)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaHoraTexto by remember {
        derivedStateOf {
            if (formState.fechaVencimiento != null && formState.horaVencimiento != null && formState.minutoVencimiento != null) {
                val cal = Calendar.getInstance().apply {
                    time = formState.fechaVencimiento!!
                    set(Calendar.HOUR_OF_DAY, formState.horaVencimiento!!)
                    set(Calendar.MINUTE, formState.minutoVencimiento!!)
                }
                formatter.format(cal.time)
            } else if (formState.fechaVencimiento != null) {
                val cal = Calendar.getInstance().apply {
                    time = formState.fechaVencimiento!!
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                }
                formatter.format(cal.time)
            } else {
                noSeleccionada
            }
        }
    }

    val onGuardarClick = {
        coroutineScope.launch {
            if (itemId == -1) {
                viewModel.insertarNueva()
            } else {
                viewModel.actualizar()
            }
            onNavigateBack()
        }
        Unit
    }

    Scaffold(
        topBar = {
            FormToolbar(
                isTask = formState.isTask,
                onNavigateBack = onNavigateBack,
                onGuardar = onGuardarClick,
                itemId = itemId
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ClassificationSwitch(
                isTask = formState.isTask,
                onToggle = { viewModel.updateIsTask(it) }
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = formState.titulo,
                onValueChange = { viewModel.updateTitulo(it) },
                label = { Text(stringResource(R.string.label_titulo)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.descripcion,
                onValueChange = { viewModel.updateDescripcion(it) },
                label = { Text(stringResource(R.string.label_descripcion)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            if (formState.isTask) {
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.label_fecha_vencimiento, fechaHoraTexto),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showDatePicker = true }) {
                        Text(stringResource(R.string.button_seleccionar_fecha))
                    }
                    if (formState.fechaVencimiento != null) {
                        Button(onClick = { showTimePicker = true }) {
                            Text(stringResource(R.string.button_seleccionar_hora))
                        }
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = formState.fechaVencimiento?.time ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.updateFechaVencimiento(Date(millis))
                        Log.d("EntryFormScreen", "Fecha seleccionada: $millis")
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.button_ok)) }
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
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = formState.horaVencimiento ?: 0,
            initialMinute = formState.minutoVencimiento ?: 0
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateHoraVencimiento(timePickerState.hour, timePickerState.minute)
                    Log.d("EntryFormScreen", "Hora seleccionada: ${timePickerState.hour}:${timePickerState.minute}")
                    showTimePicker = false
                }) {
                    Text(stringResource(R.string.button_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.button_cancelar))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (showMediaSheet) {
        MediaPickerBottomSheet(onDismiss = { showMediaSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = {
            content()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormToolbar(
    isTask: Boolean,
    onNavigateBack: () -> Unit,
    onGuardar: () -> Unit,
    itemId: Int
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    CenterAlignedTopAppBar (
        title = {
            if (itemId == -1) {
                Text(
                    // Localización condicional del título "Nueva Tarea" / "Nueva Nota"
                    text = stringResource(
                        if (isTask) R.string.title_nueva_tarea else R.string.title_nueva_nota
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            else{
                Text(
                    // Localización condicional del título "Nueva Tarea" / "Nueva Nota"
                    text = stringResource(
                        if (isTask) R.string.title_actualizar_tarea else R.string.title_actualizar_nota
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_volver)
                )
            }
        },
        actions = {
            IconButton(onClick = onGuardar) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(
                        if (itemId != -1) R.string.button_actualizar else R.string.button_guardar
                    ),
                    tint = contentColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
            navigationIconContentColor = contentColor
        )
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
            // Localización del texto "Nota"
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
            // Localización del texto "Tarea"
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
                // Localización de contentDescription y texto "Foto/Video"
                Icon(Icons.Filled.Camera, contentDescription = stringResource(R.string.action_adjunto_foto), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_foto), style = MaterialTheme.typography.bodySmall)
            }
        }

        // GRABAR AUDIO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Localización de contentDescription y texto "Grabar Audio"
                Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.action_adjunto_audio), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_audio), style = MaterialTheme.typography.bodySmall)
            }
        }

        // ELECCIONAR ARCHIVO
        IconButton(onClick = onAttachClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Localización de contentDescription y texto "Seleccionar Archivo"
                Icon(Icons.Filled.Folder, contentDescription = stringResource(R.string.action_adjunto_archivo), modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.action_adjunto_archivo), style = MaterialTheme.typography.bodySmall)
            }
        }

        // ESCRIBIR DESCRIPCIÓN
        IconButton(onClick = {}) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //Localización de contentDescription y texto "Escribir descripción"
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
            // Localización del título "Selecciona Fuente Multimedia"
            Text(stringResource(R.string.title_selecciona_fuente), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            // Localización de los botones grandes
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