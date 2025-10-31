package com.example.adminappnova.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// --- 👇 ¡IMPORTACIONES CORREGIDAS! Se fue HateoasItem 👇 ---
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoResponse
// --- --------------------------------------------------- ---
import com.example.adminappnova.data.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- DATA CLASS PARA LA UI (AHORA USA PedidoResponse limpio) ---
data class PedidosUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val currentPage: Int = 0,
    // La lista completa ahora es de PedidoResponse, no de HateoasItem
    private val _pedidosCompletos: List<PedidoResponse> = emptyList(),
    val filtroEstado: EstadoPedido? = null,
    val error: String? = null
) {
    /**
     * La propiedad que la UI observa. Filtra la lista de PedidoResponse.
     */
    val pedidosFiltrados: List<PedidoResponse> by mutableStateOf(
        if (filtroEstado == null) {
            _pedidosCompletos
        } else {
            // Filtra directamente por el campo 'estado' del PedidoResponse
            _pedidosCompletos.filter { it.estado == filtroEstado }
        }
    )

    // Un getter para acceder a la lista completa si es necesario
    val allPedidos: List<PedidoResponse>
        get() = _pedidosCompletos
}


@HiltViewModel
class PedidosViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    var uiState by mutableStateOf(PedidosUiState())
        private set

    private val pageSize = 15
    private val TAG = "PedidosVM"

    init {
        loadPedidos(page = 0)
    }

    private fun loadPedidos(page: Int) {
        if (uiState.isLoadingMore || (uiState.isLoading && page != 0)) {
            return
        }

        viewModelScope.launch {
            if (page == 0) {
                uiState = uiState.copy(isLoading = true, error = null)
            } else {
                uiState = uiState.copy(isLoadingMore = true, error = null)
            }

            // El repositorio devuelve PagedResponse<PedidoResponse> y el deserializador hace la magia
            val result = pedidoRepository.getAllPedidos(page = page, size = pageSize, estado = uiState.filtroEstado)

            result.onSuccess { pagedResponse ->
                val currentList = if (page == 0) emptyList() else uiState.allPedidos
                // pagedResponse.content AHORA ES List<PedidoResponse> gracias al deserializador
                val newList = currentList + pagedResponse.content

                Log.d(TAG, "loadPedidos($page): Éxito API. Recibidos ${pagedResponse.content.size}. Total ahora: ${newList.size}")

                // Actualizamos el estado con la nueva lista completa
                uiState = PedidosUiState(
                    isLoading = false,
                    isLoadingMore = false,
                    _pedidosCompletos = newList,
                    currentPage = page,
                    canLoadMore = !pagedResponse.last,
                    filtroEstado = uiState.filtroEstado, // Mantenemos el filtro
                    error = null
                )
            }.onFailure { e ->
                Log.e(TAG, "loadPedidos($page): Fallo API.", e)
                uiState = uiState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "Error al cargar pedidos: ${e.message}"
                )
            }
        }
    }

    fun changeFilter(nuevoEstado: EstadoPedido?) {
        if (uiState.filtroEstado != nuevoEstado) {
            Log.d(TAG, "Cambiando filtro a: ${nuevoEstado?.name ?: "TODOS"}")
            // Al cambiar el filtro, recargamos la lista desde la API con el nuevo estado
            uiState = uiState.copy(filtroEstado = nuevoEstado) // Actualiza el estado del filtro
            loadPedidos(page = 0) // Recarga desde la primera página
        }
    }

    fun loadNextPage() {
        if (uiState.canLoadMore && !uiState.isLoading && !uiState.isLoadingMore) {
            loadPedidos(uiState.currentPage + 1)
        }
    }

    fun refreshPedidos() {
        if (!uiState.isLoading && !uiState.isLoadingMore) {
            loadPedidos(page = 0)
        }
    }
}
