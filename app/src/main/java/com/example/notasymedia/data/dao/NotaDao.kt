package com.example.notasymedia.data.dao

import androidx.room.*
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    // Insertar una nueva nota/tarea
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(nota: NotaEntity): Long // Retorna el ID insertado

    // Actualizar una nota/tarea existente
    @Update
    suspend fun actualizar(nota: NotaEntity)

    // Eliminar por ID
    @Delete
    suspend fun eliminar(nota: NotaEntity)

    // Eliminar por ID
    @Query("DELETE FROM notas_table WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    // Obtener todas las notas/tareas (para pantalla principal)
    @Query("SELECT * FROM notas_table ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<NotaEntity>>

    // Obtener solo notas
    @Query("SELECT * FROM notas_table WHERE tipo = :tipo ORDER BY fechaCreacion DESC")
    fun obtenerNotas(tipo: TipoNota = TipoNota.NOTA): Flow<List<NotaEntity>>

    // Obtener solo tareas
    @Query("SELECT * FROM notas_table WHERE tipo = :tipo ORDER BY fechaCreacion DESC")
    fun obtenerTareas(tipo: TipoNota = TipoNota.TAREA): Flow<List<NotaEntity>>

    // Obtener completadas (tareas hechas)
    @Query("SELECT * FROM notas_table WHERE esCompletada = 1 ORDER BY fechaCreacion DESC")
    fun obtenerCompletadas(): Flow<List<NotaEntity>>

    // Obtener por ID (para detalle)
    @Query("SELECT * FROM notas_table WHERE id = :id")
    suspend fun obtenerPorId(id: Int): NotaEntity?
}