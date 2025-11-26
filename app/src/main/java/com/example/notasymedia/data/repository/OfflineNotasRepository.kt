package com.example.notasymedia.data.repository

import android.content.Context
import com.example.notasymedia.data.AppDatabase
import com.example.notasymedia.data.dao.MultimediaDao
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.dao.RecordatorioDao
import com.example.notasymedia.data.entity.MultimediaEntity
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.RecordatorioEntity
import com.example.notasymedia.data.entity.TipoNota
import kotlinx.coroutines.flow.Flow

class OfflineNotaRepository(
    private val notaDao: NotaDao,
    private val multimediaDao: MultimediaDao,
    private val recordatorioDao: RecordatorioDao
) : NotaRepository {
    override suspend fun insertar(nota: NotaEntity): Long {
        return notaDao.insertar(nota)
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

    // Obtener lista de tareas (suspend)
    override suspend fun obtenerTareasList(): List<NotaEntity> = notaDao.obtenerTareasList()

    //Obtener completadas
    override fun obtenerCompletadas(): Flow<List<NotaEntity>> = notaDao.obtenerCompletadas()

    //Obtener por ID
    override suspend fun obtenerPorId(id: Int): NotaEntity? = notaDao.obtenerPorId(id)

    // Multimedia
    override suspend fun insertarMultimedia(multimedia: List<MultimediaEntity>) {
        multimediaDao.insertarLista(multimedia)
    }

    override suspend fun obtenerMultimediaPorNotaId(notaId: Int): List<MultimediaEntity> {
        return multimediaDao.obtenerListaPorNotaId(notaId)
    }

    override suspend fun eliminarMultimediaPorNotaId(notaId: Int) {
        multimediaDao.eliminarPorNotaId(notaId)
    }

    override suspend fun eliminarMultimedia(multimedia: MultimediaEntity) {
        multimediaDao.eliminar(multimedia)
    }

    // Recordatorios
    override suspend fun insertarRecordatorio(recordatorio: RecordatorioEntity): Long {
        return recordatorioDao.insertar(recordatorio)
    }

    override suspend fun eliminarRecordatorio(recordatorio: RecordatorioEntity) {
        recordatorioDao.eliminar(recordatorio)
    }

    override suspend fun eliminarRecordatoriosPorNotaId(notaId: Int) {
        recordatorioDao.eliminarPorNotaId(notaId)
    }

    override suspend fun obtenerRecordatoriosPorNotaId(notaId: Int): List<RecordatorioEntity> {
        return recordatorioDao.obtenerPorNotaId(notaId)
    }

    override fun obtenerRecordatoriosPorNotaIdFlow(notaId: Int): Flow<List<RecordatorioEntity>> {
        return recordatorioDao.obtenerPorNotaIdFlow(notaId)
    }

    override suspend fun obtenerTodosLosRecordatoriosFuturos(currentTime: Long): List<RecordatorioEntity> {
        return recordatorioDao.obtenerTodosFuturos(currentTime)
    }
}

object NotaRepositoryFactory {
    fun crear(context: Context): NotaRepository {
        val db = AppDatabase.obtenerInstancia(context)
        return OfflineNotaRepository(db.notaDao(), db.multimediaDao(), db.recordatorioDao())
    }
}
