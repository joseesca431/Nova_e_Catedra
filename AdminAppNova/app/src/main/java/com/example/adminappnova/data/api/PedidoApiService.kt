package com.example.adminappnova.data.api

import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PagoRequest // <-- Necesitarás este DTO si usas /pagar
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.data.dto.PedidoResponse
import retrofit2.http.*
import java.math.BigDecimal

interface PedidoApiService {

    // --- Para HomeViewModel ---
    @GET("auth/pedido/dashboard/ganancias/totales")
    suspend fun getGananciasTotales(): BigDecimal

    @GET("auth/pedido/dashboard/productos-mas-vendidos")
    suspend fun getProductosMasVendidos(@Query("limit") limit: Int): Map<String, Long>

    // --- Para PedidosViewModel ---
    @GET("auth/pedido/all")
    suspend fun getAllPedidos(
        @Query("page") page: Int,
        @Query("size") size: Int,
        // --- 👇 AÑADE ESTE PARÁMETRO AQUÍ 👇 ---
        @Query("estado") estado: String? // Nullable para que sea opcional
        // ------------------------------------
    ): PagedResponse<PedidoResponse> // Usa el DTO HATEOAS

    // --- Para OrderDetailViewModel ---
    @GET("auth/pedido/{id}")
    suspend fun getById(@Path("id") id: Long): PedidoResponse

    @POST("auth/pedido/{id}/confirmar")
    suspend fun confirmar(@Path("id") id: Long): PedidoResponse

    @POST("auth/pedido/{id}/envio")
    suspend fun inicioEnvio(@Path("id") id: Long): PedidoResponse

    @POST("auth/pedido/{id}/entregar")
    suspend fun entregar(@Path("id") id: Long): PedidoResponse

    @POST("auth/pedido/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long, @Query("motivo") motivo: String): PedidoResponse

    // --- 👇👇👇 ¡EL ENDPOINT DE LA VICTORIA! 👇👇👇 ---
    @GET("auth/pedido/{id}/items")
    suspend fun getPedidoItems(@Path("id") id: Long): List<PedidoItemDto>

    // --- Otros Endpoints (si los necesitas en la app admin) ---
    // @POST("auth/pedido/checkout")
    // suspend fun checkout(@Body req: PedidoRequest): PedidoResponse

    // @POST("auth/pedido/{id}/pagar")
    // suspend fun pagar(@Path("id") id: Long, @Body req: PagoRequest): PedidoResponse

    // @GET("auth/pedido/user/{idUser}")
    // suspend fun getByUser(...) : PagedResponse<PedidoResponse>
}