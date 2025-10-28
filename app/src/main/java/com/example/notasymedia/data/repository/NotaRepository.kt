package com.example.notasymedia.data.repository

import android.content.Context
import com.example.notasymedia.data.AppDatabase
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import kotlinx.coroutines.flow.Flow

//Repository: abstrae acceso a datos (BD, etc.)
interface NotaRepository{

    //Insertar nueva nota/tarea
    suspend fun insertar(nota: NotaEntity) {}

    //Actualizar
    suspend fun actualizar(nota: NotaEntity) {}

    //Eliminar por ID
    suspend fun eliminarPorId(id: Int) {}

    //Obtener todas
    fun obtenerTodas(): Flow<List<NotaEntity>>

    //Obtener notas
    fun obtenerNotas(): Flow<List<NotaEntity>>

    //Obtener tareas
    fun obtenerTareas(): Flow<List<NotaEntity>>

    //Obtener completadas
    fun obtenerCompletadas(): Flow<List<NotaEntity>>

    //Obtener por ID
    suspend fun obtenerPorId(id: Int): NotaEntity?
}