package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.CarritoItemRequest
import com.example.aplicacionjetpack.data.dto.CarritoItemResponse
import com.example.aplicacionjetpack.data.dto.CarritoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CarritoApi {

    @GET("auth/carrito/{idUser}")
    suspend fun getOrCreateCarrito(@Path("idUser") idUser: Long): CarritoResponse

    @GET("auth/carrito-item/carrito/{idCarrito}")
    suspend fun getItemsByCarrito(@Path("idCarrito") idCarrito: Long): List<CarritoItemResponse>

    @POST("auth/carrito-item")
    suspend fun addItem(@Body request: CarritoItemRequest): CarritoItemResponse

    @PUT("auth/carrito-item/{id}")
    suspend fun updateItem(
        @Path("id") idCarritoItem: Long,
        @Body request: CarritoItemRequest
    ): CarritoItemResponse

    @DELETE("auth/carrito-item/{id}")
    suspend fun removeItem(@Path("id") idCarritoItem: Long): Response<Unit>
}