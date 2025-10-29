package com.example.adminappnova.data.repository

import com.example.adminappnova.data.api.PedidoApiService // <-- Asegúrate de tener e importar PedidoApiService.kt
import com.example.adminappnova.data.dto.EstadoPedido // <-- Importa el Enum
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PedidoResponse
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Interfaz ---
interface PedidoRepository {
    // Funciones para HomeScreen
    suspend fun getGananciasTotales(): Result<BigDecimal>
    suspend fun getProductosMasVendidos(limit: Int): Result<Map<String, Long>>

    // Funciones para PedidosScreen (Lista con filtro opcional)
    suspend fun getAllPedidos(page: Int, size: Int, estado: EstadoPedido? = null): Result<PagedResponse<PedidoResponse>>

    // Funciones para OrderDetailViewModel
    suspend fun getPedidoById(id: Long): Result<PedidoResponse>
    suspend fun confirmarPedido(id: Long): Result<PedidoResponse>
    suspend fun iniciarEnvio(id: Long): Result<PedidoResponse>
    suspend fun marcarEntregado(id: Long): Result<PedidoResponse>
    suspend fun cancelarPedido(id: Long, motivo: String): Result<PedidoResponse>
    // suspend fun pagarPedido(id: Long, request: PagoRequest): Result<PedidoResponse> // Si necesitas pagar
}

// --- Implementación ---
class PedidoRepositoryImpl @Inject constructor(
    private val api: PedidoApiService // <-- Inyecta la dependencia correctamente
) : PedidoRepository {

    override suspend fun getGananciasTotales(): Result<BigDecimal> {
        return try {
            val response = api.getGananciasTotales()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductosMasVendidos(limit: Int): Result<Map<String, Long>> {
        return try {
            val response = api.getProductosMasVendidos(limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Implementación ÚNICA para getAllPedidos (con filtro opcional)
    override suspend fun getAllPedidos(page: Int, size: Int, estado: EstadoPedido?): Result<PagedResponse<PedidoResponse>> {
        return try {
            // Pasa el nombre del enum (o null si no hay filtro) a la API
            val response = api.getAllPedidos(page, size, estado = estado?.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Implementaciones para OrderDetailViewModel (sin cambios respecto a la versión anterior) ---
    override suspend fun getPedidoById(id: Long): Result<PedidoResponse> {
        return try {
            val response = api.getById(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmarPedido(id: Long): Result<PedidoResponse> {
        return try {
            val response = api.confirmar(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun iniciarEnvio(id: Long): Result<PedidoResponse> {
        return try {
            val response = api.inicioEnvio(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarEntregado(id: Long): Result<PedidoResponse> {
        return try {
            val response = api.entregar(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarPedido(id: Long, motivo: String): Result<PedidoResponse> {
        return try {
            val response = api.cancelar(id, motivo)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}