package com.example.adminappnova.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.data.dto.TipoPago
import com.example.adminappnova.data.repository.PedidoRepository // <-- Importa el repo real
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result


// --- Data Class para el Estado de la UI ---
data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val pedido: PedidoResponse? = null,
    val error: String? = null,
    // Estados para acciones específicas
    val isConfirming: Boolean = false,
    val isSending: Boolean = false,
    val isDelivering: Boolean = false,
    val isCancelling: Boolean = false,
    val actionError: String? = null // Error específico de una acción
)

// --- ViewModel ---
@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository, // <-- Inyecta el repo real
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Estado observable
    var uiState by mutableStateOf(OrderDetailUiState())
        private set

    // ID del pedido desde argumentos de navegación
    private val pedidoId: Long = savedStateHandle.get<Long>("pedidoId") ?: -1L

    // Carga inicial
    init {
        if (pedidoId != -1L) {
            loadOrderDetails()
        } else {
            // Si el ID es inválido, muestra error inmediatamente
            uiState = uiState.copy(isLoading = false, error = "ID de pedido inválido recibido.")
        }
    }

    // Carga los detalles del pedido desde el repositorio
    fun loadOrderDetails() {
        if (pedidoId == -1L) return // No hacer nada si el ID no es válido

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, actionError = null) // Muestra carga
            // Llama al repositorio (necesitas getPedidoById en PedidoRepository)
            val result: Result<PedidoResponse> = pedidoRepository.getPedidoById(pedidoId)

            result.onSuccess { loadedPedido ->
                // Éxito: Actualiza el estado con los detalles
                uiState = uiState.copy(isLoading = false, pedido = loadedPedido)
            }.onFailure { e ->
                // Error: Actualiza el estado con el mensaje de error
                uiState = uiState.copy(isLoading = false, error = "Error al cargar detalles: ${e.message}")
            }
        }
    }

    // --- Funciones para Acciones del Admin ---

    // Confirma un pedido pendiente
    fun confirmarPedido() {
        executePedidoAction(
            actionStateUpdate = { copy(isConfirming = true, actionError = null) },
            repositoryCall = { pedidoRepository.confirmarPedido(pedidoId) }, // Necesitas esta función en Repo
            successStateUpdate = { result -> copy(isConfirming = false, pedido = result) },
            errorStateUpdate = { e -> copy(isConfirming = false, actionError = "Error al confirmar: ${e.message}") }
        )
    }

    // Marca un pedido como enviado
    fun iniciarEnvio() {
        executePedidoAction(
            actionStateUpdate = { copy(isSending = true, actionError = null) },
            repositoryCall = { pedidoRepository.iniciarEnvio(pedidoId) }, // Necesitas esta función en Repo
            successStateUpdate = { result -> copy(isSending = false, pedido = result) },
            errorStateUpdate = { e -> copy(isSending = false, actionError = "Error al marcar envío: ${e.message}") }
        )
    }

    // Marca un pedido como entregado
    fun marcarEntregado() {
        executePedidoAction(
            actionStateUpdate = { copy(isDelivering = true, actionError = null) },
            repositoryCall = { pedidoRepository.marcarEntregado(pedidoId) }, // Necesitas esta función en Repo
            successStateUpdate = { result -> copy(isDelivering = false, pedido = result) },
            errorStateUpdate = { e -> copy(isDelivering = false, actionError = "Error al marcar entregado: ${e.message}") }
        )
    }

    // Cancela un pedido (requiere un motivo)
    fun cancelarPedido(motivo: String) {
        if (motivo.isBlank()) {
            uiState = uiState.copy(actionError = "Se requiere un motivo para cancelar.")
            return
        }
        executePedidoAction(
            actionStateUpdate = { copy(isCancelling = true, actionError = null) },
            repositoryCall = { pedidoRepository.cancelarPedido(pedidoId, motivo) }, // Necesitas esta función en Repo
            successStateUpdate = { result -> copy(isCancelling = false, pedido = result) },
            errorStateUpdate = { e -> copy(isCancelling = false, actionError = "Error al cancelar: ${e.message}") }
        )
    }


    // --- Función genérica para ejecutar acciones y actualizar estado ---
    private fun executePedidoAction(
        actionStateUpdate: OrderDetailUiState.() -> OrderDetailUiState,
        repositoryCall: suspend () -> Result<PedidoResponse>,
        successStateUpdate: OrderDetailUiState.(PedidoResponse) -> OrderDetailUiState,
        errorStateUpdate: OrderDetailUiState.(Exception) -> OrderDetailUiState
    ) {
        if (uiState.pedido == null || pedidoId == -1L) return // No hacer nada si no hay pedido cargado

        viewModelScope.launch {
            uiState = uiState.actionStateUpdate() // Pone el estado de carga de la acción específica
            val result = repositoryCall() // Llama al repositorio

            result.onSuccess { updatedPedido ->
                uiState = uiState.successStateUpdate(updatedPedido) // Actualiza el estado con el pedido modificado
            }.onFailure { e ->
                uiState = uiState.errorStateUpdate(e as Exception) // Muestra el error de la acción
            }
        }
    }
}