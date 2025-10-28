package com.example.notasymedia.data

import android.content.Context
import com.example.notasymedia.data.repository.NotaRepository
import com.example.notasymedia.data.repository.OfflineNotaRepository
import com.example.notasymedia.data.dao.NotaDao
import kotlin.getValue

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val notaRepository: NotaRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [NotaRepository]
     */
    override val notaRepository: NotaRepository by lazy {
        OfflineNotaRepository(AppDatabase.obtenerInstancia(context).notaDao())
    }
}