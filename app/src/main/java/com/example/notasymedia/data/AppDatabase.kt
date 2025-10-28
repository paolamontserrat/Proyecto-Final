package com.example.notasymedia.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.entity.NotaEntity
import androidx.room.TypeConverters

// Clase base de la BD Room
@Database(
    entities = [NotaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Abstrae el DAO
    abstract fun notaDao(): NotaDao

    // Patron para instancia unica de BD
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notas_database" // Nombre del archivo DB
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}