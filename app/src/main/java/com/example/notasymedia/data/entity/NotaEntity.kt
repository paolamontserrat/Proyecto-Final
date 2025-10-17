package com.example.notasymedia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// Entidad para Room: representa una nota o tarea en la BD
@Entity(tableName = "notas_table")
data class NotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID auto-generado
    val titulo: String, // Titulo de la nota/tarea
    val descripcion: String, // Descripcion detallada
    val tipo: TipoNota, // Enum: NOTA o TAREA (para filtrar)
    val fechaCreacion: Date, // Fecha de creacion
    val fechaVencimiento: Date? = null, // Solo para tareas, opcional
    val esCompletada: Boolean = false, // Para marcar como hecha (tareas)
    val rutaAdjuntos: String? = null // Ruta o lista de adjuntos (por ahora string simple)
)

// Enum para tipo: nota o tarea
enum class TipoNota {
    NOTA, TAREA
}