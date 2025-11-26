package com.example.notasymedia.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import com.example.notasymedia.utils.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo reiniciado. Reprogramando alarmas...")
            
            val repository = NotaRepositoryFactory.crear(context)
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val now = System.currentTimeMillis()
                // Usamos el nuevo método que busca en la tabla de recordatorios
                val recordatoriosFuturos = repository.obtenerTodosLosRecordatoriosFuturos(now)

                Log.d("BootReceiver", "Encontrados ${recordatoriosFuturos.size} recordatorios futuros.")

                recordatoriosFuturos.forEach { recordatorio ->
                    // Necesitamos la información de la tarea padre para el título y descripción
                    val tareaPadre = repository.obtenerPorId(recordatorio.notaId)
                    
                    if (tareaPadre != null && !tareaPadre.esCompletada) {
                        // Reprogramar usando AlarmScheduler para mantener consistencia
                        AlarmScheduler.scheduleAlarm(
                            context, 
                            recordatorio.id, // Usamos ID del recordatorio, NO de la tarea
                            recordatorio.fechaHora, 
                            tareaPadre.titulo, 
                            tareaPadre.descripcion
                        )
                        Log.d("BootReceiver", "Alarma reprogramada: ${tareaPadre.titulo} para ${recordatorio.fechaHora}")
                    }
                }
            }
        }
    }
}
