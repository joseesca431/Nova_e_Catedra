package com.example.adminappnova.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoResponse
// import com.example.adminappnova.data.dto.TipoPago // No se usa aquí directamente
import com.example.adminappnova.data.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
// import java.math.BigDecimal // No se usa aquí
// import java.time.LocalDateTime // No se usa aquí
import javax.inject.Inject
import kotlin.Result


// --- Data Class para el Estado de la UI ---
data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val pedido: PedidoResponse? = null,
    val error: String? = null,
    // --- ESTADO ÚNICO PARA ACCIONES ---
    val isUpdatingStatus: Boolean = false, // Un solo indicador de carga
    // ---------------------------------
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
    private val TAG = "OrderDetailVM"

    // Estados finales (no se pueden cambiar desde aquí)
    private val estadosFinales = listOf(EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO)

    // Estados que el admin puede seleccionar
    // Filtramos los estados que no se pueden seleccionar manualmente
    val estadosSeleccionables: List<EstadoPedido> = EstadoPedido.values().filter {
        it != EstadoPedido.CARRITO &&
                it !in estadosFinales &&
                it != EstadoPedido.PENDIENTE // No se puede "revertir" a pendiente manualmente
    }

    // Carga inicial
    init {
        if (pedidoId != -1L) {
            loadOrderDetails()
        } else {
            uiState = uiState.copy(isLoading = false, error = "ID de pedido inválido recibido.")
        }
    }

    // Carga los detalles
    fun loadOrderDetails() {
        if (pedidoId == -1L) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, actionError = null)
            val result: Result<PedidoResponse> = pedidoRepository.getPedidoById(pedidoId)

            result.onSuccess { loadedPedido ->
                uiState = uiState.copy(isLoading = false, pedido = loadedPedido)
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Error al cargar detalles: ${e.message}")
            }
        }
    }

    // --- 👇 FUNCIÓN CORREGIDA (SE ELIMINÓ 'CONFIRMADO') 👇 ---
    // Mapea el ESTADO OBJETIVO seleccionado a la LLAMADA DE API correcta
    fun cambiarEstado(nuevoEstado: EstadoPedido, motivo: String? = null) {
        // No hacer nada si ya está actualizando o si el estado es final
        if (uiState.isUpdatingStatus || uiState.pedido?.estado in estadosFinales) {
            Log.w(TAG, "Actualización ignorada: ya está actualizando o el estado es final.")
            return
        }

        // Determina qué llamada al repositorio hacer
        val repositoryCall: suspend () -> Result<PedidoResponse> = when (nuevoEstado) {
            // El usuario selecciona "Pagado"
            EstadoPedido.PAGADO -> {
                // Tu API (PedidoController) tiene /pagar pero requiere un PagoRequest.
                // No podemos llamarlo sin más datos desde un simple dropdown.
                { Result.failure(Exception("La acción 'Pagar' requiere datos de pago (no implementada)")) }
            }
            // El usuario selecciona "En Proceso"
            EstadoPedido.EN_PROCESO -> {
                // Tu API tiene /confirmar. Asumimos que esta llamada pone el estado en EN_PROCESO.
                { pedidoRepository.confirmarPedido(pedidoId) }
            }
            // El usuario selecciona "Enviado"
            EstadoPedido.ENVIADO -> {
                { pedidoRepository.iniciarEnvio(pedidoId) } // Llama al endpoint /envio
            }
            // El usuario selecciona "Entregado" (Aunque está en estadosSeleccionables, lo manejamos por si acaso)
            EstadoPedido.ENTREGADO -> {
                { pedidoRepository.marcarEntregado(pedidoId) } // Llama al endpoint /entregar
            }
            // El usuario selecciona "Cancelado"
            EstadoPedido.CANCELADO -> {
                if (motivo.isNullOrBlank()) {
                    { Result.failure(Exception("Se requiere un motivo para cancelar")) }
                } else {
                    { pedidoRepository.cancelarPedido(pedidoId, motivo) }
                }
            }
            // No se puede cambiar a estos estados manualmente desde el dropdown
            EstadoPedido.CARRITO, EstadoPedido.PENDIENTE -> {
                { Result.failure(Exception("Acción no permitida")) }
            }
            // El 'when' es exhaustivo porque cubre todos los valores del Enum
        }

        // Llama a la función genérica
        executePedidoAction(
            repositoryCall = repositoryCall,
            actionStateUpdate = { copy(isUpdatingStatus = true, actionError = null) },
            successStateUpdate = { result -> copy(isUpdatingStatus = false, pedido = result) },
            errorStateUpdate = { e -> copy(isUpdatingStatus = false, actionError = "Error al actualizar: ${e.message}") }
        )
    }
    // --- -------------------- ---

    // --- Función genérica (sin cambios) ---
    private fun executePedidoAction(
        actionStateUpdate: OrderDetailUiState.() -> OrderDetailUiState,
        repositoryCall: suspend () -> Result<PedidoResponse>,
        successStateUpdate: OrderDetailUiState.(PedidoResponse) -> OrderDetailUiState,
        errorStateUpdate: OrderDetailUiState.(Exception) -> OrderDetailUiState
    ) {
        if (uiState.pedido == null || pedidoId == -1L) return
        viewModelScope.launch {
            uiState = uiState.actionStateUpdate()
            val result = repositoryCall()
            result.onSuccess { updatedPedido ->
                uiState = uiState.successStateUpdate(updatedPedido)
            }.onFailure { e ->
                uiState = uiState.errorStateUpdate(e as Exception)
            }
        }
    }
}