package com.example.notasymedia.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.R
import com.example.notasymedia.providers.MiFileProviderMultimedia
import com.example.notasymedia.viewmodel.NotaViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EntryFormScreen(
    modifier: Modifier = Modifier,
    itemId: Int = -1,
    onNavigateBack: () -> Unit,
    viewModel: NotaViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }
    var showAudioRecorder by rememberSaveable { mutableStateOf(false) }

    // --- LAUNCHERS ---
    
    // Launcher Foto
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val uri = MiFileProviderMultimedia.getLastTakenUri(context)
            uri?.let {
                viewModel.addMultimedia("FOTO", it.toString())
                MiFileProviderMultimedia.clearLastUri()
            }
        }
    }

    // Launcher Video
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) {
            val uri = MiFileProviderMultimedia.getLastTakenUri(context)
            uri?.let {
                viewModel.addMultimedia("VIDEO", it.toString())
                MiFileProviderMultimedia.clearLastUri()
            }
        }
    }

    // Launcher Archivos (Múltiples tipos)
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            // Persistir permiso de lectura para la URI (importante para reiniciar la app)
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                Log.e("EntryForm", "No se pudo persistir permisos URI: $e")
            }
            
            val type = context.contentResolver.getType(it)
            val category = when {
                type?.startsWith("image/") == true -> "FOTO"
                type?.startsWith("video/") == true -> "VIDEO"
                type?.startsWith("audio/") == true -> "AUDIO"
                else -> "ARCHIVO"
            }
            viewModel.addMultimedia(category, it.toString())
        }
    }

    LaunchedEffect(itemId) { viewModel.loadNota(itemId) }
    
    val statusNoSeleccionada = stringResource(R.string.status_no_seleccionada)
    val fechaHoraTexto by remember(formState.fechaVencimiento, formState.horaVencimiento, formState.minutoVencimiento) {
        derivedStateOf {
            if (formState.fechaVencimiento != null && formState.horaVencimiento != null && formState.minutoVencimiento != null) {
                val cal = Calendar.getInstance().apply {
                    time = formState.fechaVencimiento!!
                    set(Calendar.HOUR_OF_DAY, formState.horaVencimiento!!)
                    set(Calendar.MINUTE, formState.minutoVencimiento!!)
                }
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(cal.time)
            } else {
                statusNoSeleccionada
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == -1) stringResource(R.string.title_nueva_nota) else stringResource(R.string.title_actualizar_nota)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_volver))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (itemId == -1) viewModel.guardar() else viewModel.actualizar()
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.action_guardar))
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
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // TITULO
            OutlinedTextField(
                value = formState.titulo,
                onValueChange = { viewModel.updateTitulo(it) },
                label = { Text(stringResource(R.string.label_titulo)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // DESCRIPCION
            OutlinedTextField(
                value = formState.descripcion,
                onValueChange = { viewModel.updateDescripcion(it) },
                label = { Text(stringResource(R.string.label_descripcion)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            Spacer(Modifier.height(16.dp))

            // TIPO (NOTA/TAREA)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = !formState.isTask, onCheckedChange = { viewModel.updateIsTask(false) })
                    Text(stringResource(R.string.label_nota))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = formState.isTask, onCheckedChange = { viewModel.updateIsTask(true) })
                    Text(stringResource(R.string.label_tarea))
                }
            }
            Spacer(Modifier.height(16.dp))

            // FECHA/HORA TAREA
            if (formState.isTask) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_fecha_vencimiento))
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(fechaHoraTexto)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // SECCIÓN DE ADJUNTOS
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.label_adjuntos), style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { showMediaSheet = true }) {
                            Icon(Icons.Filled.AttachFile, contentDescription = "Adjuntar")
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))

                    if (formState.multimedia.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sin archivos adjuntos", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        // LISTA VERTICAL DE ADJUNTOS
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            formState.multimedia.forEach { item ->
                                MultimediaItemView(
                                    item = item,
                                    onRemove = { viewModel.removeMultimedia(item) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(80.dp)) // Espacio extra al final
        }
    }

    // DIALOGOS
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton({
                    datePickerState.selectedDateMillis?.let { viewModel.updateFechaVencimiento(Date(it)) }
                    showDatePicker = false
                }) { Text(stringResource(R.string.action_confirmar)) }
            },
            dismissButton = { TextButton({ showDatePicker = false }) { Text(stringResource(R.string.action_cancelar)) } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        TimePickerDialog(context, { _, h, m ->
            viewModel.updateHoraVencimiento(h)
            viewModel.updateMinutoVencimiento(m)
        }, 12, 0, true).show()
        showTimePicker = false
    }

    if (showMediaSheet) {
        MediaPickerBottomSheet(
            onDismissRequest = { showMediaSheet = false },
            onTakePhoto = {
                val uri = MiFileProviderMultimedia.getImageUri(context)
                photoLauncher.launch(uri)
                showMediaSheet = false
            },
            onTakeVideo = {
                val uri = MiFileProviderMultimedia.getVideoUri(context)
                videoLauncher.launch(uri)
                showMediaSheet = false
            },
            onPickFile = {
                fileLauncher.launch(arrayOf("*/*"))
                showMediaSheet = false
            },
            onStartAudioRecording = {
                showAudioRecorder = true
                showMediaSheet = false
            }
        )
    }

    if (showAudioRecorder) {
        AudioRecorderDialog(
            onAudioRecorded = { uri ->
                viewModel.addMultimedia("AUDIO", uri.toString())
                showAudioRecorder = false
            },
            onCancel = { showAudioRecorder = false }
        )
    }
}

// --- COMPONENTES AUXILIARES (Bottom Sheets y Dialogs) ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MediaPickerBottomSheet(
    onDismissRequest: () -> Unit,
    onTakePhoto: () -> Unit,
    onTakeVideo: () -> Unit,
    onPickFile: () -> Unit,
    onStartAudioRecording: () -> Unit
) {
    val cameraPerm = rememberPermissionState(Manifest.permission.CAMERA)
    val audioPerm = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.title_selecciona_fuente), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                if (cameraPerm.status.isGranted) {
                    onTakePhoto()
                } else {
                    cameraPerm.launchPermissionRequest()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tomar Foto")
            }
            Spacer(Modifier.height(8.dp))
            
            Button(onClick = {
                 if (cameraPerm.status.isGranted) {
                    onTakeVideo()
                } else {
                    cameraPerm.launchPermissionRequest()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Videocam, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Grabar Video")
            }
             Spacer(Modifier.height(8.dp))

            Button(onClick = {
                if (audioPerm.status.isGranted) {
                    onStartAudioRecording()
                } else {
                    audioPerm.launchPermissionRequest()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Mic, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_grabar_audio))
            }
            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                onPickFile()
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.FolderOpen, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_seleccionar_archivo_bs))
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun AudioRecorderDialog(onAudioRecorded: (Uri) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var hasFinishedRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    val recorder = remember { MediaRecorder() }
    val file = remember(Unit) { File(context.cacheDir, "audio_${System.currentTimeMillis()}.m4a") }

    val formattedTime = remember(elapsedSeconds) {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            elapsedSeconds = 0
            while (isRecording) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    DisposableEffect(Unit) {
        try {
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
            }
        } catch (e: Exception) {
            Log.e("Audio", "Error de inicialización", e)
            onCancel()
        }

        onDispose {
            try { recorder.release() } catch (_: Exception) {}
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            try {
                recorder.start()
            } catch (e: Exception) {
                Log.e("Audio", "Error al iniciar", e)
                isRecording = false
            }
        } else if (hasFinishedRecording && file.exists()) {
            try {
                recorder.stop()
                val uri = MiFileProviderMultimedia.getUriForFile(context, file) ?: Uri.fromFile(file)
                onAudioRecorded(uri)
            } catch (e: Exception) {
                Log.e("Audio", "Error al detener o guardar", e)
            }
        }
    }

    val titleText = when {
        hasFinishedRecording -> "Grabación finalizada"
        isRecording -> "Grabando audio..."
        else -> "Grabar nota de voz"
    }

    val bodyText = when {
        hasFinishedRecording -> "Audio guardado"
        isRecording -> "Tiempo: $formattedTime"
        else -> "Pulsa INICIAR GRABACIÓN para comenzar"
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(titleText) },
        text = { Text(bodyText) },
        confirmButton = {
            when {
                !isRecording && !hasFinishedRecording -> {
                    Button(onClick = { isRecording = true }) { Text("Iniciar Grabación") }
                }
                isRecording -> {
                    Button(onClick = {
                        isRecording = false
                        hasFinishedRecording = true
                    }) { Text("Detener") }
                }
                else -> {
                    Button(onClick = onCancel) { Text("Aceptar") }
                }
            }
        },
        dismissButton = if (hasFinishedRecording) null else {
            { Button(onClick = onCancel) { Text("Cancelar") } }
        }
    )
}
