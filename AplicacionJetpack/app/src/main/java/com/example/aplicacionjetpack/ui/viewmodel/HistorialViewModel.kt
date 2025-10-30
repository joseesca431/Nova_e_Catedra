package com.example.aplicacionjetpack.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.repository.HistorialPedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistorialUiState(
    val historial: List<HistorialPedidoResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val repository: HistorialPedidoRepository
) : ViewModel() {

    var uiState by mutableStateOf(HistorialUiState())
        private set

    init {
        loadHistorial()
    }

    fun loadHistorial() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            // Llama al repositorio. Asumimos que devuelve un PagedResponse.
            val result = repository.getHistorialPaginado(page = 0, size = 20)

            result.onSuccess { pagedResponse ->
                uiState = uiState.copy(
                    isLoading = false,
                    // Ordenamos por fecha para mostrar lo más reciente primero
                    historial = pagedResponse.content.sortedByDescending { it.fecha }
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo cargar el historial."
                )
            }
        }
    }
}
