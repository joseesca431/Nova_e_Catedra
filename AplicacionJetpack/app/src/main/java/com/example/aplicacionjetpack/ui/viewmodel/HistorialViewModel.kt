package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// --- 👇👇👇 ¡IMPORTS CRUCIALES! 👇👇👇 ---
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.repository.HistorialPedidoRepository
// --- ------------------------------------ ---
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Cambiamos el nombre para que coincida con el ViewModel
data class HistorialPedidoUiState(
    val pedidos: List<HistorialPedidoResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
// Cambiamos el nombre del ViewModel para que sea más descriptivo
class HistorialPedidoViewModel @Inject constructor(
    private val repository: HistorialPedidoRepository
) : ViewModel() {

    // El estado de la UI usará el nuevo nombre del data class
    var uiState by mutableStateOf(HistorialPedidoUiState())
        private set

    private val TAG = "HistorialPedidoVM"

    init {
        loadHistorial()
    }

    fun loadHistorial() {
        val currentUserId = AuthManager.userId
        if (currentUserId == null) {
            Log.e(TAG, "No se puede cargar el historial: Usuario no autenticado.")
            uiState = uiState.copy(isLoading = false, error = "Error de sesión. Por favor, inicie sesión de nuevo.")
            return
        }

        Log.d(TAG, "Cargando historial para el usuario ID: $currentUserId")

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = repository.getHistorialPaginado(page = 0, size = 100)

            result.onSuccess { pagedResponse ->
                val todosLosPedidos = pagedResponse.content
                Log.d(TAG, "Se recibieron ${todosLosPedidos.size} pedidos en total del backend.")

                // --- 👇👇👇 ¡¡¡EL DOBLE FILTRO DE LA VICTORIA!!! 👇👇👇 ---
                val misPedidosFiltrados = todosLosPedidos.filter { pedido ->
                    // Condición 1: El pedido DEBE ser del usuario actual.
                    val esMio = pedido.idUser == currentUserId
                    // Condición 2: El estado del pedido NO DEBE ser "PENDIENTE".
                    val noEsPendiente = pedido.estado.equals("PENDIENTE", ignoreCase = true).not()

                    esMio && noEsPendiente // Se deben cumplir AMBAS condiciones.
                }
                // --- ---------------------------------------------------- ---

                Log.d(TAG, "Filtrado a ${misPedidosFiltrados.size} pedidos que pertenecen al usuario $currentUserId y no están pendientes.")

                uiState = uiState.copy(
                    isLoading = false,
                    pedidos = misPedidosFiltrados.sortedByDescending { it.fecha }
                )

            }.onFailure { exception ->
                Log.e(TAG, "Fallo al cargar el historial del backend.", exception)
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo cargar el historial."
                )
            }
        }
        // --- ------------------------------------------------------------------- ---
    }
}
