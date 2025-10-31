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
        // --- 👇👇👇 ¡¡¡LA LÓGICA DE FILTRADO EN CLIENTE DE LA VICTORIA!!! 👇👇👇 ---

        // 1. Obtener el ID del usuario que ha iniciado sesión. ¡Es la clave de todo!
        val currentUserId = AuthManager.userId
        if (currentUserId == null) {
            Log.e(TAG, "No se puede cargar el historial: Usuario no autenticado.")
            uiState = uiState.copy(isLoading = false, error = "Error de sesión. Por favor, inicie sesión de nuevo.")
            return
        }

        Log.d(TAG, "Cargando historial para el usuario ID: $currentUserId")

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            // 2. Pedir TODOS los pedidos al backend (página 0, hasta 100 items).
            val result = repository.getHistorialPaginado(page = 0, size = 100)

            result.onSuccess { pagedResponse ->
                val todosLosPedidos = pagedResponse.content
                Log.d(TAG, "Se recibieron ${todosLosPedidos.size} pedidos en total del backend.")

                // 3. ¡LA MAGIA! Filtramos la lista completa que llegó del backend.
                val misPedidos = todosLosPedidos.filter { pedido ->
                    // Comparamos el 'idUser' de cada pedido con el del usuario actual.
                    pedido.idUser == currentUserId
                }

                Log.d(TAG, "Filtrado a ${misPedidos.size} pedidos que pertenecen al usuario $currentUserId.")

                // 4. Actualizamos la UI solo con la lista filtrada y ordenada.
                uiState = uiState.copy(
                    isLoading = false,
                    pedidos = misPedidos.sortedByDescending { it.fecha } // Ordenamos lo más reciente primero
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
