package com.example.adminappnova.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.data.dto.UserResponse
import com.example.adminappnova.data.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OrderDetailVM"

data class OrderDetailUiState(
    val pedido: PedidoResponse? = null,
    val usuario: UserResponse? = null,
    val pedidoItems: List<PedidoItemDto> = emptyList(),
    val isLoading: Boolean = true,
    val isUpdatingStatus: Boolean = false,
    val error: String? = null,
    val actionError: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI state expuesto como property mutable (Compose-friendly)
    var uiState by mutableStateOf(OrderDetailUiState())
        private set

    val estadosSeleccionables = listOf(
        EstadoPedido.PENDIENTE,
        EstadoPedido.PAGADO,
        EstadoPedido.EN_PROCESO,
        EstadoPedido.ENVIADO,
        EstadoPedido.ENTREGADO,
        EstadoPedido.CANCELADO
    )

    // Leemos los argumentos de navegación (definidos como Long en NavGraph)
    private val pedidoId: Long = try {
        savedStateHandle.get<Long>("pedidoId") ?: (savedStateHandle.get<String>("pedidoId")?.toLongOrNull() ?: 0L)
    } catch (e: Exception) {
        0L
    }
    private val userId: Long = try {
        savedStateHandle.get<Long>("userId") ?: (savedStateHandle.get<String>("userId")?.toLongOrNull() ?: 0L)
    } catch (e: Exception) {
        0L
    }

    init {
        if (pedidoId != 0L && userId != 0L) {
            loadOrderDetails()
        } else {
            uiState = uiState.copy(isLoading = false, error = "ID de pedido o usuario inválido.")
            Log.w(TAG, "IDs inválidos: pedidoId=$pedidoId userId=$userId")
        }
    }

    fun loadOrderDetails() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val pedidoDeferred = async { pedidoRepository.getPedidoById(pedidoId) }
                val usuarioDeferred = async { pedidoRepository.getUserById(userId) }
                val itemsDeferred = async { pedidoRepository.getPedidoItems(pedidoId) }

                val pedidoResult = pedidoDeferred.await()
                val usuarioResult = usuarioDeferred.await()
                val itemsResult = itemsDeferred.await()

                // Asumo que tus repos retornan Result<T> o directamente el objeto; adaptalo:
                val pedido = if (pedidoResult is Result<*>) (pedidoResult as Result<PedidoResponse>).getOrNull() else (pedidoResult as? PedidoResponse)
                val usuario = if (usuarioResult is Result<*>) (usuarioResult as Result<UserResponse>).getOrNull() else (usuarioResult as? UserResponse)
                val items = if (itemsResult is Result<*>) (itemsResult as Result<List<PedidoItemDto>>).getOrNull() else (itemsResult as? List<PedidoItemDto>)

                uiState = uiState.copy(
                    isLoading = false,
                    pedido = pedido,
                    usuario = usuario,
                    pedidoItems = items ?: emptyList()
                )

                Log.d(TAG, "Carga exitosa. Pedido: ${pedido?.idPedido ?: "null"}, Usuario: ${usuario?.idUser ?: "null"}, Items: ${uiState.pedidoItems.size}")

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando detalles: ", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los detalles.")
            }
        }
    }

    // Función para cambiar estado (actualiza uiState)
    fun cambiarEstado(nuevoEstado: EstadoPedido, motivo: String? = null) {
        if (uiState.isUpdatingStatus) return

        val currentPedidoId = uiState.pedido?.idPedido ?: run {
            uiState = uiState.copy(actionError = "Error: No se encontró el ID del pedido.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingStatus = true, actionError = null)
            Log.d(TAG, "Intentando cambiar estado a $nuevoEstado para pedido $currentPedidoId")

            val result = try {
                when (nuevoEstado) {
                    EstadoPedido.PAGADO -> pedidoRepository.confirmarPedido(currentPedidoId)
                    EstadoPedido.EN_PROCESO -> pedidoRepository.iniciarEnvio(currentPedidoId)
                    EstadoPedido.ENTREGADO -> pedidoRepository.marcarEntregado(currentPedidoId)
                    EstadoPedido.CANCELADO -> {
                        if (motivo.isNullOrBlank()) throw IllegalArgumentException("El motivo es obligatorio para cancelar.")
                        pedidoRepository.cancelarPedido(currentPedidoId, motivo)
                    }
                    else -> throw IllegalArgumentException("Estado no soportado: $nuevoEstado")
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error en petición cambiarEstado", ex)
                null
            }

            // Manejo del resultado (adapta según tu repo devuelva Result<T> o T)
            if (result is Result<*>) {
                result.onSuccess { newPedido ->
                    val p = newPedido as? PedidoResponse
                    uiState = uiState.copy(isUpdatingStatus = false, pedido = p)
                }.onFailure { ex ->
                    uiState = uiState.copy(isUpdatingStatus = false, actionError = "Error: ${ex.message}")
                }
            } else if (result is PedidoResponse) {
                uiState = uiState.copy(isUpdatingStatus = false, pedido = result)
            } else {
                uiState = uiState.copy(isUpdatingStatus = false, actionError = "No se pudo cambiar el estado.")
            }
        }
    }
}
