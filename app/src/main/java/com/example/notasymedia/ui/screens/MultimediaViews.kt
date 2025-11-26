package com.example.notasymedia.ui.screens

import android.net.Uri
import android.util.Log
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.notasymedia.viewmodel.MultimediaState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun MultimediaItemView(
    item: MultimediaState,
    onRemove: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (item.tipo == "AUDIO") 120.dp else 250.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Contenido principal
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    when (item.tipo) {
                        "FOTO" -> {
                            AsyncImage(
                                model = item.uri,
                                contentDescription = "Foto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        "VIDEO" -> {
                            VideoPlayerView(uri = Uri.parse(item.uri))
                        }
                        "AUDIO" -> {
                             AudioPlayerView(uri = Uri.parse(item.uri))
                        }
                        else -> { // ARCHIVO
                             Column(
                                 modifier = Modifier.fillMaxSize(),
                                 verticalArrangement = Arrangement.Center,
                                 horizontalAlignment = Alignment.CenterHorizontally
                             ) {
                                 Icon(Icons.Filled.InsertDriveFile, contentDescription = "Archivo", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                 Text("Archivo", style = MaterialTheme.typography.bodyMedium)
                             }
                        }
                    }
                }
            }
            
            // Botón Eliminar (Superpuesto)
            if (onRemove != null) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Eliminar", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun VideoPlayerView(uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Configurar AudioAttributes para asegurar que se trate como contenido multimedia (Música/Video)
            val audioAttributes = com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                .setUsage(com.google.android.exoplayer2.C.USAGE_MEDIA)
                .setContentType(com.google.android.exoplayer2.C.AUDIO_CONTENT_TYPE_MOVIE)
                .build()
            setAudioAttributes(audioAttributes, true)
            
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AudioPlayerView(uri: Uri) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var isReady by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf(false) }

    DisposableEffect(uri) {
        try {
            // Configurar AudioAttributes para que suene como Multimedia y no como Notificación
            mediaPlayer.setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { mp ->
                duration = mp.duration
                isReady = true
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                currentPosition = 0
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                loadError = true
                true
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading audio", e)
            loadError = true
        }

        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) { Log.e("AudioPlayer", "Error releasing", e) }
        }
    }

    // Actualizar progreso
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition
            delay(500) // Actualizar cada medio segundo
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loadError) {
            Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            Text("Error", style = MaterialTheme.typography.labelSmall)
        } else {
            IconButton(
                onClick = {
                    if (isReady) {
                        if (isPlaying) {
                            mediaPlayer.pause()
                            isPlaying = false
                        } else {
                            mediaPlayer.start()
                            isPlaying = true
                        }
                    }
                },
                enabled = isReady
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Barra de progreso
            if (duration > 0) {
                LinearProgressIndicator(
                    progress = { currentPosition.toFloat() / duration.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(currentPosition), style = MaterialTheme.typography.labelSmall)
                    Text(formatTime(duration), style = MaterialTheme.typography.labelSmall)
                }
            } else {
                 Text(if (isReady) "Listo" else "Cargando...", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

fun formatTime(millis: Int): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
