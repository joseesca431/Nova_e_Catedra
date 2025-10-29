package com.example.adminappnova.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf // <-- Importar para state derivado
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Importa los DTOs necesarios
import com.example.adminappnova.data.dto.EstadoPedido // <-- Importa el Enum
import com.example.adminappnova.data.dto.PagedResponse // Importa DTO Paginación HATEOAS
import com.example.adminappnova.data.dto.PedidoResponse // Importa DTO Pedido
// Importa el repositorio real
import com.example.adminappnova.data.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI (CON FILTRADO CLIENTE) ---
data class PedidosUiState(
    val isLoading: Boolean = true, // Carga inicial
    val isLoadingMore: Boolean = false, // Carga de siguientes páginas
    val canLoadMore: Boolean = true, // Indica si hay más páginas
    val currentPage: Int = 0, // Última página cargada con éxito (base 0)
    // Lista interna privada que almacena TODOS los pedidos cargados
    private val _pedidosCompletos: List<PedidoResponse> = emptyList(),
    // Filtro actual seleccionado por el usuario (null = mostrar todos)
    val filtroEstado: EstadoPedido? = null,
    // Mensaje de error a mostrar en la UI
    val error: String? = null
) {
    /**
     * Propiedad calculada que la UI observará.
     * Devuelve SIEMPRE la lista filtrada basada en `_pedidosCompletos` y `filtroEstado`.
     * Usa `derivedStateOf` para que Compose solo la recalcule si cambia la lista completa o el filtro.
     */
    val pedidosFiltrados: List<PedidoResponse> by derivedStateOf {
        if (filtroEstado == null) {
            _pedidosCompletos // Si no hay filtro, devuelve la lista completa
        } else {
            // Si hay filtro, filtra la lista completa por el estado seleccionado
            _pedidosCompletos.filter { it.estado == filtroEstado }
        }
    }

    /**
     * Propiedad pública (opcional) para acceder a la lista completa si fuera necesario
     * (por ejemplo, para depuración o cálculos internos).
     */
    val allPedidos: List<PedidoResponse>
        get() = _pedidosCompletos
}

// --- ViewModel (CON FILTRADO CLIENTE) ---
@HiltViewModel
class PedidosViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository // Inyecta el repositorio
) : ViewModel() {

    // Estado observable por la UI
    var uiState by mutableStateOf(PedidosUiState())
        private set // Solo el ViewModel modifica el estado

    // Configuración de paginación
    private val pageSize = 15 // Cuántos items pedir a la API cada vez
    private val TAG = "PedidosVM" // Tag para logs

    // Carga inicial al crear el ViewModel
    init {
        Log.d(TAG, "ViewModel inicializado. Cargando página 0 (sin filtro API)...")
        loadPedidos(page = 0) // Carga la primera página completa
    }

    /**
     * Carga una página de pedidos desde la API (SIEMPRE sin filtro de estado)
     * y la añade a la lista interna `_pedidosCompletos`.
     * @param page El número de página (base 0) a cargar.
     */
    fun loadPedidos(page: Int) {
        // Evita cargas concurrentes
        if (uiState.isLoadingMore || (uiState.isLoading && page != 0)) {
            Log.d(TAG, "loadPedidos($page) ignorado: Ya está cargando.")
            return
        }
        Log.d(TAG, "loadPedidos($page): Iniciando carga desde API (sin filtro)...")

        viewModelScope.launch {
            // Actualiza estado de carga (inicial o 'cargando más')
            if (page == 0) {
                uiState = uiState.copy(isLoading = true, error = null)
            } else {
                uiState = uiState.copy(isLoadingMore = true, error = null)
            }

            // --- LLAMADA AL REPOSITORIO (SIEMPRE SIN FILTRO DE ESTADO) ---
            val result: Result<PagedResponse<PedidoResponse>> =
                pedidoRepository.getAllPedidos(
                    page = page,
                    size = pageSize,
                    estado = null // <-- NUNCA enviamos filtro a la API en esta estrategia
                )
            // ------------------------------------

            // Maneja el resultado
            result.onSuccess { pagedResponse ->
                // Obtiene la lista actual completa
                val currentList = if (page == 0) emptyList() else uiState.allPedidos
                // Añade los nuevos pedidos a la lista completa
                val newList = currentList + pagedResponse.content
                Log.d(TAG, "loadPedidos($page): Éxito API. Recibidos ${pagedResponse.content.size}. Total completo ahora: ${newList.size}. Última API: ${pagedResponse.last}")

                // Actualiza el estado
                uiState = uiState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    _pedidosCompletos = newList, // Guarda la nueva lista completa
                    currentPage = page,          // Actualiza la última página cargada con éxito
                    canLoadMore = !pagedResponse.last, // Determina si la API tiene más páginas
                    error = null                 // Limpia errores previos
                    // El filtroEstado NO cambia aquí
                )
            }.onFailure { e ->
                // Maneja el error de la API
                Log.e(TAG, "loadPedidos($page): Fallo API.", e)
                uiState = uiState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "Error al cargar pedidos: ${e.message}"
                )
            }
        }
    }

    /**
     * Cambia el filtro de estado aplicado a la lista mostrada en la UI.
     * NO vuelve a llamar a la API, solo actualiza el estado `filtroEstado`.
     * La propiedad calculada `pedidosFiltrados` se actualizará automáticamente.
     * @param nuevoEstado El nuevo estado por el cual filtrar, o `null` para mostrar todos.
     */
    fun changeFilter(nuevoEstado: EstadoPedido?) {
        // Solo actualiza si el filtro realmente cambió
        if (uiState.filtroEstado != nuevoEstado) {
            Log.d(TAG, "Cambiando filtro UI a: ${nuevoEstado?.name ?: "TODOS"}")
            uiState = uiState.copy(filtroEstado = nuevoEstado)
            // La lista 'pedidosFiltrados' se actualiza sola gracias a derivedStateOf
        } else {
            Log.d(TAG, "Filtro ${nuevoEstado?.name ?: "TODOS"} ya aplicado. No se hace nada.")
        }
    }

    /**
     * Intenta cargar la siguiente página de pedidos desde la API
     * si hay más páginas disponibles y no se está cargando actualmente.
     */
    fun loadNextPage() {
        Log.d(TAG, "Intentando cargar página siguiente. ¿Puede?: ${uiState.canLoadMore}, ¿Cargando?: ${uiState.isLoading || uiState.isLoadingMore}")
        if (uiState.canLoadMore && !uiState.isLoading && !uiState.isLoadingMore) {
            loadPedidos(uiState.currentPage + 1) // Carga la siguiente página de la lista completa
        }
    }

    /**
     * Refresca la lista de pedidos volviendo a cargar la primera página
     * desde la API (sin filtro).
     */
    fun refreshPedidos() {
        Log.d(TAG, "Intentando refrescar. ¿Cargando?: ${uiState.isLoading || uiState.isLoadingMore}")
        if (!uiState.isLoading && !uiState.isLoadingMore) {
            // Al llamar a loadPedidos(0), la lista _pedidosCompletos se limpiará
            // y se volverá a llenar con la primera página fresca de la API.
            // El filtro actual (filtroEstado) se mantendrá.
            loadPedidos(page = 0)
        }
    }
}