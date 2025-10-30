package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PedidoApi {
    @POST("auth/pedido/checkout")
    suspend fun checkout(@Body request: PedidoRequest): PedidoResponse

    @POST("auth/pedido/{id}/pagar")
    suspend fun pagar(
        @Path("id") idPedido: Long,
        @Body request: PagoRequest
    ): PedidoResponse
}