package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

// --- DTO Auxiliar para combinar las peticiones ---
data class CreateAndPayRequest(
    val pedidoRequest: PedidoRequest,
    val pagoRequest: PagoRequest
)

interface PedidoApi {
    // --- 👇👇👇 ¡EL NUEVO ENDPOINT QUE NECESITAREMOS EN SPRING! 👇👇👇 ---
    @POST("auth/pedido/create-and-pay") // Asumimos esta nueva ruta
    suspend fun createAndPayOrder(@Body request: CreateAndPayRequest): PedidoResponse

    // --- Endpoints antiguos ---
    @POST("auth/pedido/checkout")
    suspend fun checkout(@Body request: PedidoRequest): PedidoResponse

    @POST("auth/pedido/{id}/pagar")
    suspend fun pagar(
        @Path("id") idPedido: Long,
        @Body request: PagoRequest
    ): PedidoResponse
}
