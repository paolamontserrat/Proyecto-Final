package com.example.notasymedia.providers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.notasymedia.R
import java.io.File

class MediaFileProvider : FileProvider(R.xml.file_paths) {
    companion object {
        fun getPhotoUri(ctx: Context): Uri {
            val dir = File(ctx.cacheDir, "media/photos")
            dir.mkdirs()
            val file = File.createTempFile("photo_", ".jpg", dir)
            val auth = ctx.packageName + ".mediafileprovider"
            return FileProvider.getUriForFile(ctx, auth, file)
        }

        fun getVideoUri(ctx: Context): Uri {
            val dir = File(ctx.cacheDir, "media/videos")
            dir.mkdirs()
            val file = File.createTempFile("video_", ".mp4", dir)
            val auth = ctx.packageName + ".mediafileprovider"
            return FileProvider.getUriForFile(ctx, auth, file)
        }

        fun getAudioUri(ctx: Context): Uri {
            val dir = File(ctx.cacheDir, "media/audio")
            dir.mkdirs()
            val file = File.createTempFile("audio_", ".m4a", dir)
            val auth = ctx.packageName + ".mediafileprovider"
            return FileProvider.getUriForFile(ctx, auth, file)
        }
    }
}