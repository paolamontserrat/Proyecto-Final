package com.example.notasymedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// ViewModel: maneja estado y logica de negocio
class NotaViewModel(private val context: android.content.Context) : ViewModel() {

    private val repository = NotaRepositoryFactory.crear(context)

    // Estados observables: usa StateFlow para Compose
    val todasNotas = repository.obtenerTodas()
    val notas = repository.obtenerNotas()
    val tareas = repository.obtenerTareas()
    val completadas = repository.obtenerCompletadas()


    // Insertar nueva nota/tarea
    fun insertarNueva(titulo: String, descripcion: String, tipo: TipoNota, fechaVencimiento: java.util.Date? = null) {
        viewModelScope.launch {
            val nuevaNota = NotaEntity(
                titulo = titulo,
                descripcion = descripcion,
                tipo = tipo,
                fechaCreacion = java.util.Date(),
                fechaVencimiento = fechaVencimiento // Solo para tareas
            )
            repository.insertar(nuevaNota)
        }
    }

    // Obtener por ID (para detalle o editar)
    suspend fun obtenerPorId(id: Int): NotaEntity? {
        return repository.obtenerPorId(id) // Ya es suspend en repo, lo pasamos
    }

    // Marcar completada (solo para tareas)
    fun marcarCompletada(id: Int, esCompletada: Boolean) {
        viewModelScope.launch {
            val nota = repository.obtenerPorId(id)
            if (nota?.tipo == TipoNota.TAREA) { // Solo si es tarea
                val actualizada = nota.copy(esCompletada = esCompletada)
                repository.actualizar(actualizada)
            }
        }
    }

    // Eliminar
    fun eliminar(id: Int) {
        viewModelScope.launch {
            repository.eliminarPorId(id)
        }
    }

    // Actualizar (para editar)
    fun actualizar(nota: NotaEntity) {
        viewModelScope.launch {
            repository.actualizar(nota)
        }
    }
}