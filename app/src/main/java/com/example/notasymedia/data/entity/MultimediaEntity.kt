package com.example.notasymedia.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "multimedia_table",
    foreignKeys = [
        ForeignKey(
            entity = NotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["notaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MultimediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notaId: Int,
    val tipo: String, // FOTO, VIDEO, AUDIO, ARCHIVO
    val uri: String,
    val descripcion: String = ""
)
