package com.example.notasymedia.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.notasymedia.ui.theme.NotasYMediaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(modifier: Modifier = Modifier) {
    // Estado para manejar si es NOTA o TAREA (RF-06)
    var isTask by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { FormToolbar(isTask) },
    ) { paddingValues ->
        Column(modifier = modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()) { // Asegura que el Column sea deslizable si el contenido es mucho

            // Selector Nota/Tarea
            ClassificationSwitch(isTask) { isTask = it }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

            // Campos de Fecha/Hora (solo si es Tarea)
            if (isTask) {
                Spacer(Modifier.height(16.dp))
                // Placeholder para el selector de fecha/hora
                Text("Fecha de Vencimiento: (Custom Date/Time Picker Composable)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(24.dp))
            Text("Adjuntar Multimedia (RF-02)", style = MaterialTheme.typography.titleMedium)

            // Botones para adjuntar
            MediaTypeSelector(onAttachClicked = { showMediaSheet = true })

            Text("--- Placeholder: ScrollableRow de miniaturas (AttachmentRow) ---")

        }
    }

    if (showMediaSheet) {
        MediaPickerBottomSheet(onDismiss = { showMediaSheet = false })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormToolbar(isTask: Boolean) {
    CenterAlignedTopAppBar (
        title = { Text(if (isTask) "Nueva Tarea" else "Nueva Nota") },
        navigationIcon = {
            IconButton(onClick = { /* Volver atrás */ }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = {
            // Botón de Guardar
            Button(onClick = { /* Guardar Datos */ }) {
                Text("GUARDAR")
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
        EntryFormScreen()
    }
}