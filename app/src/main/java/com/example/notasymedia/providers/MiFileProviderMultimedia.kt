package com.example.notasymedia.providers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class MiFileProviderMultimedia : FileProvider() {

    companion object {
        private var ultimaUri: Uri? = null  // Guardamos la última URI creada

        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File.createTempFile("IMG_${System.currentTimeMillis()}", ".jpg", directory)
            val authority = "${context.packageName}.mediafileprovider"
            ultimaUri = FileProvider.getUriForFile(context, authority, file)
            return ultimaUri!!
        }

        fun getVideoUri(context: Context): Uri {
            val directory = File(context.cacheDir, "videos").apply { mkdirs() }
            val file = File.createTempFile("VID_${System.currentTimeMillis()}", ".mp4", directory)
            val authority = "${context.packageName}.mediafileprovider"
            ultimaUri = FileProvider.getUriForFile(context, authority, file)
            return ultimaUri!!
        }

        fun getLastTakenUri(context: Context): Uri? = ultimaUri

        fun clearLastUri() {
            ultimaUri = null
        }

        fun getUriForFile(context: Context, file: File): Uri {
            return FileProvider.getUriForFile(context, context.packageName + ".mediafileprovider", file)
        }
    }
}