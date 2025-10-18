package com.example.notasymedia.data

import androidx.room.TypeConverter
import com.example.notasymedia.data.entity.TipoNota
import java.util.Date

// Clase de conversores para tipos no primitivos en Room
class Converters {
    // Converter para el enum TipoNota: usa name() para String (más legible que ordinal())
    @TypeConverter
    fun fromTipoNota(tipo: TipoNota): String = tipo.name

    @TypeConverter
    fun toTipoNota(value: String): TipoNota = enumValueOf(value)

    // Opcional: Para Date (Room lo soporta nativo, pero agrega si hay issues)
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(value: Long?): Date? = value?.let { Date(it) }
}