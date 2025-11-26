package com.example.notasymedia.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recordatorios_table",
    foreignKeys = [
        ForeignKey(
            entity = NotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["notaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["notaId"])]
)
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notaId: Int,
    val fechaHora: Long, // Tiempo en milisegundos para la alarma
    val activo: Boolean = true
)
