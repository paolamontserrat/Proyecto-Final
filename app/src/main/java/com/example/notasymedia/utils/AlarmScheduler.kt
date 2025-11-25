package com.example.notasymedia.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.notasymedia.receivers.AlarmReceiver

object AlarmScheduler {

    fun scheduleAlarm(context: Context, taskId: Int, timeInMillis: Long, title: String, description: String) {
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

        // Cancelar alarma previa si existe para este ID (update)
        alarmManager.cancel(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
        
        Log.d("AlarmScheduler", "Alarma programada para tarea $taskId a las $timeInMillis")
    }

    fun cancelAlarm(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Alarma cancelada para tarea $taskId")
    }
}
