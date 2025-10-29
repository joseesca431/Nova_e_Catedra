package com.example.adminappnova.ui.viewmodel

import android.util.Log // Importar Log para depuración
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async // Importar async para llamadas concurrentes
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI ---
data class HomeUiState(
    val isLoading: Boolean = true,
    val totalGanancias: BigDecimal? = null, // Ganancias totales
    val topProductos: Map<String, Long>? = null, // Mapa Nombre -> Cantidad Vendida
    val pendingOrders: Int = 0,     // Contador pedidos pendientes (AÚN SIMULADO)
    val completedOrders: Int = 0, // Contador pedidos completados (AÚN SIMULADO)
    val returns: Int = 0,         // Contador devoluciones (AÚN SIMULADO)
    val error: String? = null
)

// --- ViewModel ---
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository // Inyecta el repositorio real
) : ViewModel() {

    // Estado observable
    var uiState by mutableStateOf(HomeUiState())
        private set

    // Carga inicial
    init {
        loadDashboardData()
    }

    // Carga todos los datos necesarios para el dashboard
    private fun loadDashboardData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null) // Indicar carga
            Log.d("HomeVM", "Iniciando carga de datos del dashboard")

            try {
                // --- LLAMADAS ASÍNCRONAS CONCURRENTES ---
                // Lanza las llamadas que no dependen entre sí al mismo tiempo
                val gananciasDeferred = async { pedidoRepository.getGananciasTotales() }
                val topProductosDeferred = async { pedidoRepository.getProductosMasVendidos(limit = 3) }
                // TODO: Lanza aquí las llamadas para los contadores cuando existan
                // val pendingOrdersDeferred = async { pedidoRepository.getPendingOrdersCount() }
                // val completedOrdersDeferred = async { pedidoRepository.getCompletedOrdersCount() }
                // val returnsDeferred = async { pedidoRepository.getReturnsCount() }

                // Espera los resultados de las llamadas
                val gananciasResult: Result<BigDecimal> = gananciasDeferred.await()
                val topProductosResult: Result<Map<String, Long>> = topProductosDeferred.await()
                // val pendingOrdersResult = pendingOrdersDeferred.await()
                // val completedOrdersResult = completedOrdersDeferred.await()
                // val returnsResult = returnsDeferred.await()

                // --- MANEJO DE RESULTADOS ---
                // Lanza excepción si alguna llamada falló (puedes manejar errores individualmente si prefieres)
                val ganancias = gananciasResult.getOrThrow()
                val topProductos = topProductosResult.getOrThrow()
                // val pendingCount = pendingOrdersResult.getOrThrow()
                // val completedCount = completedOrdersResult.getOrThrow()
                // val returnsCount = returnsResult.getOrThrow()

                // Simulación TEMPORAL para contadores (Borra esto cuando tengas las llamadas reales)
                val pendingCount = 5
                val completedCount = 120
                val returnsCount = 2
                // Fin simulación

                Log.d("HomeVM", "Datos cargados: Ganancias=$ganancias, Top=$topProductos")

                // Actualiza el estado con todos los datos cargados
                uiState = uiState.copy(
                    isLoading = false,
                    totalGanancias = ganancias,
                    topProductos = topProductos,
                    pendingOrders = pendingCount,
                    completedOrders = completedCount,
                    returns = returnsCount,
                    error = null // Limpia error si todo fue exitoso
                )

            } catch (e: Exception) {
                // Captura cualquier error de las llamadas .getOrThrow()
                Log.e("HomeVM", "Error al cargar datos del dashboard", e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}" // Muestra el mensaje de error
                )
            }
        }
    }

    // Función para refrescar los datos
    fun refreshData() {
        // Solo refresca si no está cargando actualmente
        if (!uiState.isLoading) {
            loadDashboardData()
        }
    }
}