package com.example.aplicacionjetpack.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue // <-- CORREGIDO
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import com.example.aplicacionjetpack.data.repository.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificacionesUiState(
    val notificaciones: List<NotificacionResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    var uiState by mutableStateOf(NotificacionesUiState())
        private set

    init {
        loadNotificaciones()
    }

    fun loadNotificaciones() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = notificacionRepository.getNotificaciones()

            result.onSuccess { notificaciones ->
                uiState = uiState.copy(
                    isLoading = false,
                    notificaciones = notificaciones.sortedByDescending { it.fechaEnvio }
                )
            }.onFailure { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudieron cargar las notificaciones."
                )
            }
        }
    }

    fun marcarComoLeida(idNotificacion: Long) {
        viewModelScope.launch {
            // El campo correcto es 'id' según el último log de la API
            val notificacion = uiState.notificaciones.find { it.id == idNotificacion }

            if (notificacion?.estado == "ENVIADA") {
                val result = notificacionRepository.marcarLeida(idNotificacion)
                result.onSuccess {
                    val updatedList = uiState.notificaciones.map {
                        if (it.id == idNotificacion) { // El campo correcto es 'id'
                            it.copy(estado = "LEIDA")
                        } else {
                            it
                        }
                    }
                    uiState = uiState.copy(notificaciones = updatedList)
                }.onFailure {
                    // Manejar error si es necesario
                }
            }
        }
    }
}
