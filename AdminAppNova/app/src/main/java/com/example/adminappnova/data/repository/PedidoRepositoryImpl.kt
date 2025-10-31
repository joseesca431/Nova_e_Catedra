package com.example.adminappnova.data.repository

import android.util.Log
import com.example.adminappnova.data.api.PedidoApiService
import com.example.adminappnova.data.api.UserApiService
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.data.dto.UserResponse
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Result

/**
 * Implementación concreta de PedidoRepository.
 * Esta clase es la que realmente habla con los ApiServices (Pedido y Usuario)
 * y maneja la lógica de try-catch para las llamadas de red.
 */
class PedidoRepositoryImpl @Inject constructor(
    private val pedidoApi: PedidoApiService,
    private val userApi: UserApiService
) : PedidoRepository {

    private val TAG = "PedidoRepositoryImpl"

    override suspend fun getGananciasTotales(): Result<BigDecimal> {
        return try {
            val response = pedidoApi.getGananciasTotales()
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en getGananciasTotales", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductosMasVendidos(limit: Int): Result<Map<String, Long>> {
        return try {
            val response = pedidoApi.getProductosMasVendidos(limit)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en getProductosMasVendidos", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllPedidos(page: Int, size: Int, estado: EstadoPedido?): Result<PagedResponse<PedidoResponse>> {
        return try {
            val estadoString = estado?.name // Convierte Enum a String para la API
            val response = pedidoApi.getAllPedidos(page, size, estadoString)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en getAllPedidos", e)
            Result.failure(e)
        }
    }

    override suspend fun getPedidoById(id: Long): Result<PedidoResponse> {
        return try {
            val response = pedidoApi.getById(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en getPedidoById($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: Long): Result<UserResponse> {
        return try {
            val response = userApi.getUserById(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en getUserById($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun confirmarPedido(id: Long): Result<PedidoResponse> {
        return try {
            val response = pedidoApi.confirmar(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en confirmarPedido($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun iniciarEnvio(id: Long): Result<PedidoResponse> {
        return try {
            val response = pedidoApi.inicioEnvio(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en iniciarEnvio($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun marcarEntregado(id: Long): Result<PedidoResponse> {
        return try {
            val response = pedidoApi.entregar(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en marcarEntregado($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun cancelarPedido(id: Long, motivo: String): Result<PedidoResponse> {
        return try {
            val response = pedidoApi.cancelar(id, motivo)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error en cancelarPedido($id)", e)
            Result.failure(e)
        }
    }

    override suspend fun getPedidoItems(id: Long): Result<List<PedidoItemDto>> {
        return try {
            val response = pedidoApi.getPedidoItems(id)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("PedidoRepo", "Error en getPedidoItems($id)", e)
            Result.failure(e)
        }
    }
}
