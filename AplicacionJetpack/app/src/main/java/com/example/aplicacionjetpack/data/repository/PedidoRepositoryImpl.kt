package com.example.aplicacionjetpack.data.repository

import android.util.Log
// --- 👇👇👇 ¡¡¡LA CORRECCIÓN ESTÁ AQUÍ!!! 👇👇👇 ---
import com.example.aplicacionjetpack.data.api.CreateAndPayRequest
import com.example.aplicacionjetpack.data.api.PedidoApi
// --- -------------------------------------------- ---
import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import javax.inject.Inject
import kotlin.Result

class PedidoRepositoryImpl @Inject constructor(
    private val api: PedidoApi
) : PedidoRepository {
    private val TAG = "PedidoRepository"

    // --- 👇👇👇 ¡LA IMPLEMENTACIÓN DE LA NUEVA FUNCIÓN! 👇👇👇 ---
    override suspend fun createAndPayOrder(pedidoRequest: PedidoRequest, pagoRequest: PagoRequest): Result<PedidoResponse> {
        return try {
            val combinedRequest = CreateAndPayRequest(pedidoRequest, pagoRequest)
            val response = api.createAndPayOrder(combinedRequest)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "createAndPayOrder falló", e)
            Result.failure(e)
        }
    }

    override suspend fun checkout(request: PedidoRequest): Result<PedidoResponse> {
        return try {
            Result.success(api.checkout(request))
        } catch (e: Exception) {
            Log.e(TAG, "checkout falló", e)
            Result.failure(e)
        }
    }

    override suspend fun pagar(idPedido: Long, request: PagoRequest): Result<PedidoResponse> {
        return try {
            Result.success(api.pagar(idPedido, request))
        } catch (e: Exception) {
            Log.e(TAG, "pagar falló", e)
            Result.failure(e)
        }
    }
}
