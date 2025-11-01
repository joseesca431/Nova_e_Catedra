package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.repository.HistorialPedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class HistorialPedidoUiState(
    val pedidos: List<HistorialPedidoResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistorialPedidoViewModel @Inject constructor(
    private val repository: HistorialPedidoRepository
) : ViewModel() {

    var uiState by mutableStateOf(HistorialPedidoUiState())
        private set

    private val TAG = "HistorialPedidoVM"

    init {
        loadHistorial()
    }

    /**
     * Prioridad de estados (lower == mayor prioridad)
     * Según tu CASE:
     * PAGADO -> 1
     * ENVIADO -> 2
     * EN_PROCESO -> 3
     * PENDIENTE -> 4
     * ENTREGADO -> 5
     * CANCELADO -> 6
     * else -> 7
     */
    private fun statePriority(estadoRaw: String?): Int {
        return when (estadoRaw?.trim()?.uppercase()) {
            "PAGADO" -> 1
            "ENVIADO" -> 2
            "EN_PROCESO", "EN PROCESO" -> 3
            "PENDIENTE" -> 4
            "ENTREGADO" -> 5
            "CANCELADO" -> 6
            else -> 7
        }
    }

    /**
     * Intenta parsear diferentes formatos de fecha que pueda devolver el backend.
     * Devuelve epoch millis para comparaciones; si falla, devuelve 0L.
     */
    private fun parseFechaToEpoch(fechaStr: String?): Long {
        if (fechaStr.isNullOrBlank()) return 0L
        val candidates = listOf(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        )

        for (fmt in candidates) {
            try {
                return try {
                    // Primero intentar como OffsetDateTime
                    val odt = OffsetDateTime.parse(fechaStr, fmt)
                    odt.toInstant().toEpochMilli()
                } catch (_: Exception) {
                    // Si no es offset, intentar LocalDateTime o LocalDate
                    try {
                        val ldt = LocalDateTime.parse(fechaStr, fmt)
                        ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } catch (_: Exception) {
                        val ld = LocalDate.parse(fechaStr, fmt)
                        ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    }
                }
            } catch (_: DateTimeParseException) {
                // intentar siguiente formato
            } catch (_: Exception) {
            }
        }

        // Intento final: parsear como Instant directo
        return try {
            Instant.parse(fechaStr).toEpochMilli()
        } catch (_: Exception) {
            0L
        }
    }

    fun loadHistorial() {
        val currentUserId = AuthManager.userId
        if (currentUserId == null) {
            Log.e(TAG, "No se puede cargar el historial: Usuario no autenticado.")
            uiState = uiState.copy(
                isLoading = false,
                error = "Error de sesión. Por favor, inicie sesión de nuevo."
            )
            return
        }

        Log.d(TAG, "Cargando historial para el usuario ID: $currentUserId")

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = repository.getHistorialPaginado(
                page = 0,
                size = 200
            ) // pedir más mientras debuggeamos

            result.onSuccess { pagedResponse ->
                val todosLosPedidos = pagedResponse.content
                Log.d(TAG, "DEBUG: recibidos ${todosLosPedidos.size} pedidos crudos desde backend.")

                // Log detallado para inspección: idPedido, idUser, estado, fecha
                todosLosPedidos.forEachIndexed { idx, p ->
                    Log.d(
                        TAG,
                        "RAW[$idx] idPedido=${p.idPedido} idUser=${p.idUser} estado='${p.estado}' fecha='${p.fecha}'"
                    )
                }

                // Intento 1: filtrar normalmente (pero con comparación tolerante)
                val pedidosDelUsuario = todosLosPedidos.filter { pedido ->
                    matchesUser(pedido.idUser, currentUserId)
                }

                Log.d(
                    TAG,
                    "DEBUG: pedidosDelUsuario (coincidencia tolerante) = ${pedidosDelUsuario.size}"
                )

                // Si no hay pedidos para el usuario, para ayudar al debug dejaremos temporalmente
                // la lista completa en UI (para que puedas ver en pantalla lo que llega).
                if (pedidosDelUsuario.isEmpty()) {
                    Log.w(
                        TAG,
                        "No se encontraron pedidos pertenecientes al usuario usando la comparación tolerante. " +
                                "Mostrando todos los pedidos crudos para inspección."
                    )
                }

                // --- Dedupe y prioridad (si hay registros) ---
                val toProcess =
                    if (pedidosDelUsuario.isNotEmpty()) pedidosDelUsuario else todosLosPedidos

                val dedupeById = toProcess
                    .groupBy { it.idPedido }
                    .mapValues { (_, list) ->
                        list.minWithOrNull(
                            compareBy<HistorialPedidoResponse> { statePriority(it.estado) }
                                .thenByDescending { parseFechaToEpoch(it.fecha) }
                        )!!
                    }
                    .values
                    .toList()

                Log.d(
                    TAG,
                    "DEBUG: después dedupe quedan ${dedupeById.size} pedidos (usando ${if (pedidosDelUsuario.isNotEmpty()) "filtrado por usuario" else "todos los crudos"})"
                )

                val ordenados = dedupeById.sortedWith(
                    compareBy<HistorialPedidoResponse> { statePriority(it.estado) }
                        .thenByDescending { parseFechaToEpoch(it.fecha) }
                )

                uiState = uiState.copy(isLoading = false, pedidos = ordenados)
            }.onFailure { exception ->
                Log.e(TAG, "Fallo al cargar el historial del backend.", exception)
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el historial.")
            }
        }
    }

    /**
     * Comparación tolerante entre el idUser del pedido (puede venir como Long, Int o String)
     * y el currentUserId que tengas en AuthManager.
     */
    private fun matchesUser(pedidoIdUser: Any?, currentUserId: Long): Boolean {
        if (pedidoIdUser == null) return false
        return try {
            when (pedidoIdUser) {
                is Number -> pedidoIdUser.toLong() == currentUserId
                is String -> {
                    // comparar como número si puede, sino comparar como strings
                    val asLong = pedidoIdUser.toLongOrNull()
                    if (asLong != null) asLong == currentUserId else pedidoIdUser == currentUserId.toString()
                }

                else -> pedidoIdUser.toString() == currentUserId.toString()
            }
        } catch (e: Exception) {
            // En caso cualquier excepción devolvemos false (evita crash)
            Log.w(TAG, "matchesUser fallo al comparar: ${e.message}")
            false
        }
    }
}

