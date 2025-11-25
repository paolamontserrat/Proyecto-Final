package com.example.notasymedia.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo reiniciado. Reprogramando alarmas...")
            
            val repository = NotaRepositoryFactory.crear(context)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val tareas = repository.obtenerTareasList() // Necesitamos una funcion suspend que retorne List<NotaEntity> directo, no Flow
                val now = System.currentTimeMillis()

                tareas.forEach { tarea ->
                    if (tarea.tipo == TipoNota.TAREA && !tarea.esCompletada && tarea.fechaVencimiento != null) {
                        val time = tarea.fechaVencimiento!!.time
                        if (time > now) {
                            scheduleAlarm(context, tarea.id, time, tarea.titulo, tarea.descripcion)
                        }
                    }
                }
            }
        }
    }

    private fun scheduleAlarm(context: Context, taskId: Int, timeInMillis: Long, title: String, description: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("taskId", taskId)
            putExtra("title", title)
            putExtra("description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                // Fallback a alarma inexacta si no hay permiso (aunque deberiamos pedirlo en UI)
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
        
        Log.d("BootReceiver", "Alarma reprogramada para tarea $taskId a las $timeInMillis")
    }
}
