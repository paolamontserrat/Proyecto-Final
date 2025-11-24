package com.example.notasymedia.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.TipoNota
import com.example.notasymedia.data.repository.NotaRepositoryFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import android.app.Application
import androidx.lifecycle.AndroidViewModel

//Objeto de estado
data class FormState(
    val id: Int = -1,
    val titulo: String = "",
    val descripcion: String = "",
    val isTask: Boolean = false,
    val fechaVencimiento: Date? = null,
    val horaVencimiento: Int? = null,
    val minutoVencimiento: Int? = null,
    val rutaAdjuntos: String? = null
)

class NotaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotaRepositoryFactory.crear(application)
    // Estados observables para listas
    val todasNotas = repository.obtenerTodas()
    val notas = repository.obtenerNotas()
    val tareas = repository.obtenerTareas()
    val completadas = repository.obtenerCompletadas()

    //Estado del formulario
    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    //Cargar nota para edicion
    fun loadNota(id: Int) {
        viewModelScope.launch {
            val nota = if (id != -1) repository.obtenerPorId(id) else null
            _formState.value = if (nota != null) {
                FormState(
                    id = id,
                    titulo = nota.titulo,
                    descripcion = nota.descripcion,
                    isTask = nota.tipo == TipoNota.TAREA,
                    fechaVencimiento = nota.fechaVencimiento,
                    horaVencimiento = nota.fechaVencimiento?.let { date ->
                        java.util.Calendar.getInstance().apply { time = date }.get(java.util.Calendar.HOUR_OF_DAY)
                    },
                    minutoVencimiento = nota.fechaVencimiento?.let { date ->
                        java.util.Calendar.getInstance().apply { time = date }.get(java.util.Calendar.MINUTE)
                    },
                    rutaAdjuntos = nota.rutaAdjuntos
                )
            } else {
                FormState() //Estado vacio para nueva nota
            }
        }
    }

    //Funciones para actualizar el estado del formulario
    fun updateTitulo(titulo: String) {
        _formState.value = _formState.value.copy(titulo = titulo)
    }
    fun updateDescripcion(descripcion: String) {
        _formState.value = _formState.value.copy(descripcion = descripcion)
    }
    fun updateIsTask(isTask: Boolean) {
        _formState.value = _formState.value.copy(isTask = isTask)
        if (!isTask) {
            //Si no es tarea, limpiar fecha y hora
            _formState.value = _formState.value.copy(
                fechaVencimiento = null,
                horaVencimiento = null,
                minutoVencimiento = null
            )
        }
    }
    fun updateFechaVencimiento(fecha: Date?) {
        _formState.value = _formState.value.copy(fechaVencimiento = fecha, horaVencimiento = null, minutoVencimiento = null)
    }
    fun updateHoraVencimiento(hora: Int, minuto: Int) {
        _formState.value = _formState.value.copy(horaVencimiento = hora, minutoVencimiento = minuto)
    }

    // Insertar nueva nota/tarea
    fun insertarNueva() {
        viewModelScope.launch {
            val state = _formState.value
            val tipo = if (state.isTask) TipoNota.TAREA else TipoNota.NOTA
            val vencimientoFinal: Date? = if (state.isTask && state.fechaVencimiento != null) {
                val cal = java.util.Calendar.getInstance().apply {
                    time = state.fechaVencimiento
                    set(java.util.Calendar.HOUR_OF_DAY, state.horaVencimiento ?: 0)
                    set(java.util.Calendar.MINUTE, state.minutoVencimiento ?: 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                cal.time
            } else null

            val nuevaNota = NotaEntity(
                titulo = state.titulo,
                descripcion = state.descripcion,
                tipo = tipo,
                fechaCreacion = Date(),
                fechaVencimiento = vencimientoFinal,
                rutaAdjuntos = state.rutaAdjuntos
            )
            repository.insertar(nuevaNota)
            resetForm() //Resetear el formulario tras guardar
        }
    }

    //Actualizar nota existente
    fun actualizar() {
        viewModelScope.launch {
            val state = _formState.value
            if (state.id != -1) {
                val tipo = if (state.isTask) TipoNota.TAREA else TipoNota.NOTA
                val vencimientoFinal: Date? = if (state.isTask && state.fechaVencimiento != null) {
                    val cal = java.util.Calendar.getInstance().apply {
                        time = state.fechaVencimiento
                        set(java.util.Calendar.HOUR_OF_DAY, state.horaVencimiento ?: 0)
                        set(java.util.Calendar.MINUTE, state.minutoVencimiento ?: 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    cal.time
                } else null

                val updatedNota = NotaEntity(
                    id = state.id,
                    titulo = state.titulo,
                    descripcion = state.descripcion,
                    tipo = tipo,
                    fechaCreacion = Date(),
                    fechaVencimiento = vencimientoFinal
                )
                repository.actualizar(updatedNota)
                resetForm() //Resetear el formulario tras actualizar
            }
        }
    }

    //Marcar completada (solo para tareas)
    fun marcarCompletada(id: Int, esCompletada: Boolean) {
        viewModelScope.launch {
            val nota = repository.obtenerPorId(id)
            if (nota?.tipo == TipoNota.TAREA) {
                val actualizada = nota.copy(esCompletada = esCompletada)
                repository.actualizar(actualizada)
            }
        }
    }

    //Eliminar
    fun eliminar(id: Int) {
        viewModelScope.launch {
            repository.eliminarPorId(id)
        }
    }

    //Agregar adjunti
    fun agregarAdjunto(ruta: String) {
        val current = _formState.value.rutaAdjuntos ?: ""
        val updated = if (current.isNotEmpty()) "$current,$ruta" else ruta
        _formState.value = _formState.value.copy(rutaAdjuntos = updated)
    }

    //Resetear el formulario
    fun resetForm() {
        _formState.value = FormState()
    }

    //Obtener nota
    suspend fun obtenerPorId(id: Int): NotaEntity? {
        return repository.obtenerPorId(id)
    }
}