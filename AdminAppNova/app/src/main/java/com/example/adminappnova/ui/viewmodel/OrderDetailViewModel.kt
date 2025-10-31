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
// YA NO NECESITAMOS EL REPOSITORIO DE PRODUCTOS AQUÍ
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailUiState(
    val pedido: PedidoResponse? = null,
    val pedidoItems: List<PedidoItemDto> = emptyList(), // ¡Ahora contendrá datos REALES!
    val usuario: UserResponse? = null,
    val isLoading: Boolean = true,
    val isUpdatingStatus: Boolean = false,
    val error: String? = null,
    val actionError: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    // YA NO SE INYECTA ProductRepository, hemos matado la simulación
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(OrderDetailUiState())
        private set

    val estadosSeleccionables = listOf(EstadoPedido.EN_PROCESO, EstadoPedido.ENVIADO, EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO)

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
                // --- 👇👇👇 ¡LA LÓGICA DE CARGA REAL DE LA VICTORIA! 👇👇👇 ---
                // Lanzamos las 3 llamadas en paralelo para máxima eficiencia
                val pedidoDeferred = async { pedidoRepository.getPedidoById(pedidoId) }
                val userDeferred = async { pedidoRepository.getUserById(userId) }
                // ¡LA LLAMADA AL NUEVO ENDPOINT!
                val itemsDeferred = async { pedidoRepository.getPedidoItems(pedidoId) }

                // Esperamos los resultados
                val pedido = pedidoDeferred.await().getOrThrow()
                val usuario = userDeferred.await().getOrThrow()
                val items = itemsDeferred.await().getOrThrow() // ¡Lista real de PedidoItemDto!
                // --- -------------------------------------------------------- ---

                Log.d(TAG, "Carga exitosa. Pedido: ${pedido.idPedido}, Usuario: ${usuario.username}, Items REALES: ${items.size}")

                uiState = uiState.copy(
                    isLoading = false,
                    pedido = pedido,
                    usuario = usuario,
                    pedidoItems = items // ¡ASIGNAMOS LOS ITEMS REALES!
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando detalles completos del pedido: ", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los detalles completos del pedido.")
            }
        }
    }

    // La función cambiarEstado no necesita cambios
    fun cambiarEstado(nuevoEstado: EstadoPedido, motivo: String? = null) {
        if (uiState.isUpdatingStatus) return
        viewModelScope.launch {
            // ... (sin cambios)
        }
    }
}
