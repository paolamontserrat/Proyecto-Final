package com.example.notasymedia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notasymedia.data.entity.RecordatorioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(recordatorio: RecordatorioEntity): Long

    @Update
    suspend fun actualizar(recordatorio: RecordatorioEntity)

    @Delete
    suspend fun eliminar(recordatorio: RecordatorioEntity)

    @Query("DELETE FROM recordatorios_table WHERE notaId = :notaId")
    suspend fun eliminarPorNotaId(notaId: Int)

    @Query("SELECT * FROM recordatorios_table WHERE notaId = :notaId")
    suspend fun obtenerPorNotaId(notaId: Int): List<RecordatorioEntity>
    
    @Query("SELECT * FROM recordatorios_table WHERE notaId = :notaId")
    fun obtenerPorNotaIdFlow(notaId: Int): Flow<List<RecordatorioEntity>>

    // Método añadido para BootReceiver: Obtener todos los recordatorios activos futuros
    @Query("SELECT * FROM recordatorios_table WHERE fechaHora > :currentTime")
    suspend fun obtenerTodosFuturos(currentTime: Long): List<RecordatorioEntity>
}
