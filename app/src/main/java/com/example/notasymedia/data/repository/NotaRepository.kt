package com.example.notasymedia.data.repository

import com.example.notasymedia.data.entity.MultimediaEntity
import com.example.notasymedia.data.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

//Repository: abstrae acceso a datos (BD, etc.)
interface NotaRepository{

    //Insertar nueva nota/tarea
    suspend fun insertar(nota: NotaEntity): Long

    //Actualizar
    suspend fun actualizar(nota: NotaEntity)

    //Eliminar por ID
    suspend fun eliminarPorId(id: Int)

    //Obtener todas
    fun obtenerTodas(): Flow<List<NotaEntity>>

    //Obtener notas
    fun obtenerNotas(): Flow<List<NotaEntity>>

    //Obtener tareas
    fun obtenerTareas(): Flow<List<NotaEntity>>

    // Obtener lista de tareas (suspend, para Background/Boot)
    suspend fun obtenerTareasList(): List<NotaEntity>

    //Obtener completadas
    fun obtenerCompletadas(): Flow<List<NotaEntity>>

    //Obtener por ID
    suspend fun obtenerPorId(id: Int): NotaEntity?

    // Multimedia
    suspend fun insertarMultimedia(multimedia: List<MultimediaEntity>)
    suspend fun obtenerMultimediaPorNotaId(notaId: Int): List<MultimediaEntity>
    suspend fun eliminarMultimediaPorNotaId(notaId: Int)
    suspend fun eliminarMultimedia(multimedia: MultimediaEntity)
}
