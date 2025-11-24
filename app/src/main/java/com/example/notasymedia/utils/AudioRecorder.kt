package com.example.notasymedia.utils

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AndroidAudioRecorder(private val context: Context) {  // Fix: Toma Context como param
    private var recorder: MediaRecorder? = null

    fun start(outputFile: File) {  // Fix: Toma File directamente
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    fun stop() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}