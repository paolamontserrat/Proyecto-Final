package com.example.notasymedia.receivers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notasymedia.MainActivity
import com.example.notasymedia.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "onReceive: Alarma disparada!")
        val taskId = intent.getIntExtra("taskId", -1)
        val title = intent.getStringExtra("title") ?: "Tarea pendiente"
        val description = intent.getStringExtra("description") ?: "Tienes una tarea por vencer"

        if (taskId == -1) {
            Log.e("AlarmReceiver", "Error: taskId no recibido en el intent.")
        }

        createNotificationChannel(context)
        showNotification(context, taskId, title, description)
    }

    private fun showNotification(context: Context, taskId: Int, title: String, description: String) {
        Log.d("AlarmReceiver", "Intentando mostrar notificación para tarea $taskId")
        
        // Intent para abrir la app al tocar la notificacion
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId) 
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            taskId, 
            openAppIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de sistema seguro
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("AlarmReceiver", "Permiso POST_NOTIFICATIONS no concedido.")
                return
            }
        }
        
        try {
            with(NotificationManagerCompat.from(context)) {
                 notify(taskId, builder.build()) 
                 Log.d("AlarmReceiver", "Notificación enviada al sistema.")
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error al enviar notificación", e)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Tareas"
            val descriptionText = "Notificaciones para tareas programadas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("AlarmReceiver", "Canal de notificaciones creado/verificado.")
        }
    }

    companion object {
        const val CHANNEL_ID = "task_reminders_channel"
    }
}
