package com.example.adminappnova.data.repository

import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.data.dto.UserResponse
import java.math.BigDecimal // <-- ¡¡¡LA LÍNEA CORREGIDA!!!
import kotlin.Result

/**
 * Interfaz que define el contrato para las operaciones relacionadas con los Pedidos.
 * Es la abstracción que los ViewModels usarán, sin conocer los detalles de la implementación.
 */
interface PedidoRepository {

    // --- Funciones para el Dashboard ---
    suspend fun getGananciasTotales(): Result<BigDecimal>
    suspend fun getProductosMasVendidos(limit: Int): Result<Map<String, Long>>

    // --- Funciones para la Lista de Pedidos ---
    suspend fun getAllPedidos(page: Int, size: Int, estado: EstadoPedido?): Result<PagedResponse<PedidoResponse>>

    // --- Funciones para el Detalle de Pedido ---
    suspend fun getPedidoById(id: Long): Result<PedidoResponse>
    suspend fun getUserById(id: Long): Result<UserResponse> // Para obtener datos del cliente

    // --- Funciones para cambiar el estado de un Pedido ---
    suspend fun confirmarPedido(id: Long): Result<PedidoResponse>
    suspend fun iniciarEnvio(id: Long): Result<PedidoResponse>
    suspend fun marcarEntregado(id: Long): Result<PedidoResponse>
    suspend fun cancelarPedido(id: Long, motivo: String): Result<PedidoResponse>


    suspend fun getPedidoItems(id: Long): Result<List<PedidoItemDto>>
}
