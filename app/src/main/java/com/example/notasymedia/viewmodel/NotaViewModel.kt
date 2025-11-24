package com.example.notasymedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasymedia.data.entity.MultimediaEntity
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

data class FormState(
    val id: Int = -1,
    val titulo: String = "",
    val descripcion: String = "",
    val isTask: Boolean = false,
    val fechaVencimiento: Date? = null,
    val horaVencimiento: Int? = null,
    val minutoVencimiento: Int? = null,
    val multimedia: List<MultimediaState> = emptyList()
)

data class MultimediaState(
    val id: Int = 0,
    val tipo: String, // FOTO, VIDEO, AUDIO, ARCHIVO
    val uri: String,
    val descripcion: String = ""
)

fun MultimediaEntity.toState() = MultimediaState(id, tipo, uri, descripcion)

class NotaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotaRepositoryFactory.crear(application)

    // Listas observables
    val todasNotas = repository.obtenerTodas()
    val notas = repository.obtenerNotas()
    val tareas = repository.obtenerTareas()
    val completadas = repository.obtenerCompletadas()

    // Estado del formulario
    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // Cargar nota para edición
    fun loadNota(id: Int) {
        viewModelScope.launch {
            val nota = if (id != -1) repository.obtenerPorId(id) else null
            val multimediaList = if (id != -1) repository.obtenerMultimediaPorNotaId(id).map { it.toState() } else emptyList()

            _formState.value = if (nota != null) {
                FormState(
                    id = nota.id,
                    titulo = nota.titulo,
                    descripcion = nota.descripcion,
                    isTask = nota.tipo == TipoNota.TAREA,
                    fechaVencimiento = nota.fechaVencimiento,
                    horaVencimiento = nota.fechaVencimiento?.let {
                        Calendar.getInstance().apply { time = it }.get(Calendar.HOUR_OF_DAY)
                    },
                    minutoVencimiento = nota.fechaVencimiento?.let {
                        Calendar.getInstance().apply { time = it }.get(Calendar.MINUTE)
                    },
                    multimedia = multimediaList
                )
            } else {
                FormState()
            }
        }
    }

    // Actualizadores del formulario
    fun updateTitulo(titulo: String) {
        _formState.value = _formState.value.copy(titulo = titulo)
    }

    fun updateDescripcion(descripcion: String) {
        _formState.value = _formState.value.copy(descripcion = descripcion)
    }

    fun updateIsTask(isTask: Boolean) {
        _formState.value = _formState.value.copy(isTask = isTask)
    }

    fun updateFechaVencimiento(fecha: Date?) {
        _formState.value = _formState.value.copy(fechaVencimiento = fecha)
    }

    fun updateHoraVencimiento(hora: Int?) {
        _formState.value = _formState.value.copy(horaVencimiento = hora)
    }

    fun updateMinutoVencimiento(minuto: Int?) {
        _formState.value = _formState.value.copy(minutoVencimiento = minuto)
    }

    fun addMultimedia(tipo: String, uri: String) {
        val currentList = _formState.value.multimedia.toMutableList()
        currentList.add(MultimediaState(tipo = tipo, uri = uri))
        _formState.value = _formState.value.copy(multimedia = currentList)
    }

    fun removeMultimedia(item: MultimediaState) {
        val currentList = _formState.value.multimedia.toMutableList()
        currentList.remove(item)
        _formState.value = _formState.value.copy(multimedia = currentList)
    }

    // GUARDAR nueva nota
    fun guardar() {
        viewModelScope.launch {
            val state = _formState.value
            val vencimientoFinal: Date? = if (state.isTask && state.fechaVencimiento != null) {
                Calendar.getInstance().apply {
                    time = state.fechaVencimiento!!
                    set(Calendar.HOUR_OF_DAY, state.horaVencimiento ?: 0)
                    set(Calendar.MINUTE, state.minutoVencimiento ?: 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            } else null

            val nuevaNota = NotaEntity(
                titulo = state.titulo.ifBlank { "Sin título" },
                descripcion = state.descripcion,
                tipo = if (state.isTask) TipoNota.TAREA else TipoNota.NOTA,
                fechaCreacion = Date(),
                fechaVencimiento = vencimientoFinal,
                esCompletada = false,
                // Campos antiguos nulos
                tipoMultimedia = null,
                multimediaUri = null
            )
            val notaId = repository.insertar(nuevaNota).toInt()

            // Guardar multimedia
            if (state.multimedia.isNotEmpty()) {
                val entities = state.multimedia.map {
                    MultimediaEntity(notaId = notaId, tipo = it.tipo, uri = it.uri, descripcion = it.descripcion)
                }
                repository.insertarMultimedia(entities)
            }

            resetForm()
        }
    }

    // ACTUALIZAR nota existente
    fun actualizar() {
        viewModelScope.launch {
            val state = _formState.value
            if (state.id != -1) {
                val notaOriginal = repository.obtenerPorId(state.id) ?: return@launch

                val vencimientoFinal: Date? = if (state.isTask && state.fechaVencimiento != null) {
                    Calendar.getInstance().apply {
                        time = state.fechaVencimiento!!
                        set(Calendar.HOUR_OF_DAY, state.horaVencimiento ?: 0)
                        set(Calendar.MINUTE, state.minutoVencimiento ?: 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                } else null

                val notaActualizada = notaOriginal.copy(
                    titulo = state.titulo.ifBlank { "Sin título" },
                    descripcion = state.descripcion,
                    tipo = if (state.isTask) TipoNota.TAREA else TipoNota.NOTA,
                    fechaVencimiento = vencimientoFinal
                )
                repository.actualizar(notaActualizada)

                // Actualizar multimedia: estrategia simple -> borrar todo y reinsertar
                repository.eliminarMultimediaPorNotaId(state.id)
                if (state.multimedia.isNotEmpty()) {
                    val entities = state.multimedia.map {
                        MultimediaEntity(notaId = state.id, tipo = it.tipo, uri = it.uri, descripcion = it.descripcion)
                    }
                    repository.insertarMultimedia(entities)
                }

                resetForm()
            }
        }
    }

    // Marcar como completada
    fun marcarCompletada(id: Int, completada: Boolean) {
        viewModelScope.launch {
            val nota = repository.obtenerPorId(id) ?: return@launch
            if (nota.tipo == TipoNota.TAREA) {
                repository.actualizar(nota.copy(esCompletada = completada))
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch { 
            repository.eliminarMultimediaPorNotaId(id) // Limpiar multimedia primero por si acaso (aunque CASCADE debería hacerlo)
            repository.eliminarPorId(id) 
        }
    }

    fun resetForm() {
        _formState.value = FormState()
    }

    suspend fun obtenerPorId(id: Int): NotaEntity? = repository.obtenerPorId(id)

    suspend fun obtenerMultimedia(id: Int): List<MultimediaEntity> = repository.obtenerMultimediaPorNotaId(id)
}
