package com.example.notasymedia.data.repository

import android.content.Context
import com.example.notasymedia.data.AppDatabase
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import kotlinx.coroutines.flow.Flow

class OfflineNotaRepository(private val notaDao: NotaDao) : NotaRepository {
    override suspend fun insertar(nota: NotaEntity) {
        notaDao.insertar(nota)
    }

    //Actualizar
    override suspend fun actualizar(nota: NotaEntity) {
        notaDao.actualizar(nota)
    }

    //Eliminar por ID
    override suspend fun eliminarPorId(id: Int) {
        notaDao.eliminarPorId(id)
    }

    //Obtener todas
    override fun obtenerTodas(): Flow<List<NotaEntity>> = notaDao.obtenerTodas()

    //Obtener notas
    override fun obtenerNotas(): Flow<List<NotaEntity>> = notaDao.obtenerNotas(TipoNota.NOTA)

    //Obtener tareas
    override fun obtenerTareas(): Flow<List<NotaEntity>> = notaDao.obtenerTareas(TipoNota.TAREA)

    //Obtener completadas
    override fun obtenerCompletadas(): Flow<List<NotaEntity>> = notaDao.obtenerCompletadas()

    //Obtener por ID
    override suspend fun obtenerPorId(id: Int): NotaEntity? = notaDao.obtenerPorId(id)
}
object NotaRepositoryFactory {
    fun crear(context: Context): NotaRepository {
        val db = AppDatabase.obtenerInstancia(context)
        return OfflineNotaRepository(db.notaDao())
    }
}