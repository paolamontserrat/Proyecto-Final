package com.example.notasymedia.data.repository

import android.content.Context
import com.example.notasymedia.data.AppDatabase
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import kotlinx.coroutines.flow.Flow

// Repository: abstrae acceso a datos (BD, etc.)
class NotaRepository(private val notaDao: NotaDao) {

    // Insertar nueva nota/tarea
    suspend fun insertar(nota: NotaEntity) {
        notaDao.insertar(nota)
    }

    // Actualizar
    suspend fun actualizar(nota: NotaEntity) {
        notaDao.actualizar(nota)
    }

    // Eliminar por ID
    suspend fun eliminarPorId(id: Int) {
        notaDao.eliminarPorId(id)
    }

    // Obtener todas
    fun obtenerTodas(): Flow<List<NotaEntity>> = notaDao.obtenerTodas()

    // Obtener notas
    fun obtenerNotas(): Flow<List<NotaEntity>> = notaDao.obtenerNotas(TipoNota.NOTA)

    // Obtener tareas
    fun obtenerTareas(): Flow<List<NotaEntity>> = notaDao.obtenerTareas(TipoNota.TAREA)

    // Obtener completadas
    fun obtenerCompletadas(): Flow<List<NotaEntity>> = notaDao.obtenerCompletadas()

    // Obtener por ID
    suspend fun obtenerPorId(id: Int): NotaEntity? = notaDao.obtenerPorId(id)
}

// Factory para crear el repo con DB
object NotaRepositoryFactory {
    fun crear(context: Context): NotaRepository {
        val db = AppDatabase.obtenerInstancia(context)
        return NotaRepository(db.notaDao())
    }
}