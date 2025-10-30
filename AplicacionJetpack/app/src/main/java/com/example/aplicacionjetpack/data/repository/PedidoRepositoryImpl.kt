// Ruta: data/repository/PedidoRepositoryImpl.kt
package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.PedidoApi
import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import javax.inject.Inject
import kotlin.Result

// ¡SOLO LA CLASE!
class PedidoRepositoryImpl @Inject constructor(
    private val api: PedidoApi
) : PedidoRepository {
    override suspend fun checkout(request: PedidoRequest): Result<PedidoResponse> {
        return try {
            Result.success(api.checkout(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pagar(idPedido: Long, request: PagoRequest): Result<PedidoResponse> {
        return try {
            Result.success(api.pagar(idPedido, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
    