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

data class OrderDetailUiState(
    val pedido: PedidoResponse? = null,
    val pedidoItems: List<PedidoItemDto> = emptyList(),
    val usuario: UserResponse? = null,
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

    var uiState by mutableStateOf(OrderDetailUiState())
        private set

    // --- CAMBIO 1 ---
    // Añadido PAGADO y quitado ENVIADO para que coincida con la API
    val estadosSeleccionables = listOf(
        EstadoPedido.PAGADO,
        EstadoPedido.EN_PROCESO,
        EstadoPedido.ENTREGADO,
        EstadoPedido.CANCELADO
    )

    private val pedidoId: Long = savedStateHandle.get<Long>("pedidoId") ?: 0L
    private val userId: Long = savedStateHandle.get<Long>("userId") ?: 0L

    private val TAG = "OrderDetailVM"

    init {
        if (pedidoId != 0L && userId != 0L) {
            loadOrderDetails()
        } else {
            uiState = uiState.copy(isLoading = false, error = "ID de pedido o usuario inválido.")
        }
    }

    private fun loadOrderDetails() {
        viewModelScope.launch {
            uiState = OrderDetailUiState(isLoading = true)
            Log.d(TAG, "Iniciando carga de detalles REALES para pedido ID: $pedidoId")

            try {
                val pedidoDeferred = async { pedidoRepository.getPedidoById(pedidoId) }
                val userDeferred = async { pedidoRepository.getUserById(userId) }
                val itemsDeferred = async { pedidoRepository.getPedidoItems(pedidoId) }

                // Esperamos los resultados
                val pedido = pedidoDeferred.await().getOrThrow()
                val usuario = userDeferred.await().getOrThrow()
                val items = itemsDeferred.await().getOrThrow()

                Log.d(TAG, "Carga exitosa. Pedido: ${pedido.idPedido}, Usuario: ${usuario.username}, Items REALES: ${items.size}")

                uiState = uiState.copy(
                    isLoading = false,
                    pedido = pedido,
                    usuario = usuario,
                    pedidoItems = items
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando detalles completos del pedido: ", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los detalles completos del pedido.")
            }
        }
    }

    // --- CAMBIO 2 ---
    // Función 'cambiarEstado' con la lógica del 'when' corregida
    fun cambiarEstado(nuevoEstado: EstadoPedido, motivo: String? = null) {
        // 1. Evita clics duplicados si ya se está actualizando
        if (uiState.isUpdatingStatus) return

        // 2. Obtiene el ID del pedido. Si es nulo, muestra error y sale.
        val currentPedidoId = uiState.pedido?.idPedido ?: run {
            uiState = uiState.copy(actionError = "Error: No se encontró el ID del pedido.")
            return
        }

        viewModelScope.launch {
            // 3. Pone la UI en estado de carga y limpia errores viejos
            uiState = uiState.copy(isUpdatingStatus = true, actionError = null)
            Log.d(TAG, "Intentando cambiar estado a $nuevoEstado para pedido $currentPedidoId")

            // 4. Determina qué llamada al repositorio hacer
            //    !! LÓGICA CORREGIDA !!
            val result: Result<PedidoResponse> = when (nuevoEstado) {

                // Esta es la llamada que devuelve "PAGADO"
                EstadoPedido.PAGADO -> pedidoRepository.confirmarPedido(currentPedidoId)

                // Esta es la llamada que devuelve "EN_PROCESO"
                EstadoPedido.EN_PROCESO -> pedidoRepository.iniciarEnvio(currentPedidoId)

                // Esta ya funcionaba bien
                EstadoPedido.ENTREGADO -> pedidoRepository.marcarEntregado(currentPedidoId)

                // Esta es la llamada correcta
                EstadoPedido.CANCELADO -> {
                    if (motivo.isNullOrBlank()) {
                        Result.failure(IllegalArgumentException("El motivo es obligatorio para cancelar."))
                    } else {
                        pedidoRepository.cancelarPedido(currentPedidoId, motivo)
                    }
                }

                // Cualquier otro estado (como ENVIADO) no está soportado
                else -> Result.failure(IllegalArgumentException("Estado no soportado: $nuevoEstado"))
            }


            // 6. Maneja el resultado de la llamada
            result.onSuccess { pedidoActualizado ->
                // ¡ÉXITO! Actualiza la UI con el pedido que retornó la API
                Log.d(TAG, "Estado cambiado exitosamente a: ${pedidoActualizado.estado}")
                uiState = uiState.copy(
                    isUpdatingStatus = false,
                    pedido = pedidoActualizado // <-- Aquí se actualiza la pantalla
                )
            }.onFailure { exception ->
                // FALLO: Muestra el error en el Snackbar
                Log.e(TAG, "Error al cambiar estado a $nuevoEstado", exception)
                uiState = uiState.copy(
                    isUpdatingStatus = false,
                    actionError = "Error: ${exception.message}"
                )
            }
        }
    }
}