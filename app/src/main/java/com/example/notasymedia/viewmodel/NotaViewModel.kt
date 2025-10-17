package com.example.notasymedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import kotlinx.coroutines.launch

// ViewModel: maneja estado y logica de negocio
class NotaViewModel(private val context: android.content.Context) : ViewModel() {

    private val repository = NotaRepositoryFactory.crear(context)

    // Estados observables: usa StateFlow para Compose
    val todasNotas = repository.obtenerTodas()
    val notas = repository.obtenerNotas()
    val tareas = repository.obtenerTareas()
    val completadas = repository.obtenerCompletadas()

    // Insertar nueva
    fun insertarNueva(titulo: String, descripcion: String, tipo: TipoNota) {
        viewModelScope.launch {
            val nuevaNota = NotaEntity(
                titulo = titulo,
                descripcion = descripcion,
                tipo = tipo,
                fechaCreacion = java.util.Date()
            )
            repository.insertar(nuevaNota)
        }
    }

    // Actualizar completada
    fun marcarCompletada(id: Int, esCompletada: Boolean) {
        viewModelScope.launch {
            val nota = repository.obtenerPorId(id)
            nota?.let {
                val actualizada = it.copy(esCompletada = esCompletada)
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
}