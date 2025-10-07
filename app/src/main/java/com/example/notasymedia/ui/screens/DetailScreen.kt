package com.example.notasymedia.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import androidx.compose.ui.graphics.Color


@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    itemId: Int, // El ID recibido
    onNavigateToEdit: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = { DetailToolbar(onNavigateToEdit, onNavigateBack) },
        bottomBar = { TaskActionsBottomBar() }
    ) { paddingValues ->
        // Usamos Column y verticalScroll para que el contenido sea deslizable
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Contenido de la Tarea/Nota (Basado en Prototipo 4)

            // Título (Placeholder)
            Text(
                text = "Detalle de Tarea con ID: $itemId", // Muestra el ID recibido
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Indicador de Estado/Vencimiento
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Completado",
                    tint = Color.Green, // Usar un color claro de success
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Pagado el 15/03/2024 a las 18:00",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Descripción completa de la tarea. Lorem ipsum Lonem, dor amet, consectetur adipiscing elit. Sed do eiusmod tempor t labore et dolore magna aliqua.",
                style = MaterialTheme.typography.bodyLarge
            )

            // --- SECCIÓN: ARCHIVOS ADJUNTOS (RF-12) ---
            Spacer(Modifier.height(24.dp))
            Text("Archivos Adjuntos", style = MaterialTheme.typography.titleMedium)
            AttachmentRow() // Composable para la fila de miniaturas

            // --- SECCIÓN: RECORDATORIOS (RF-08) ---
            Spacer(Modifier.height(24.dp))
            Text("Recordatorios Programados", style = MaterialTheme.typography.titleMedium)

            // Placeholder de recordatorios
            Column {
                Text("14/2024 10:00 - Notificado")
                Text("15/2024 17:00 - Notificado")
                // Botón para agregar recordatorio (RF-08)
                Button(onClick = { /* Abrir diálogo de recordatorio */ },
                    modifier = Modifier.align(Alignment.End)) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Recordatorio")
                }
            }

            Spacer(Modifier.height(32.dp)) // Espacio final
        }
    }
}


// Barra Superior para la vista de Detalle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToolbar(onNavigateToEdit: () -> Unit, onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("") }, // El título generalmente es la propia nota en el contenido
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = {
            // RF-11: Botón de Edición
            IconButton(onClick = onNavigateToEdit) {
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