package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.dto.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HistorialPedidoApi {
    @GET("auth/historial-pedidos")
    suspend fun getHistorial(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<HistorialPedidoResponse>
}
