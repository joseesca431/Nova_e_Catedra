package com.example.adminappnova.data.api

import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.ProductRequest
import com.example.adminappnova.data.dto.ProductResponse
import retrofit2.http.*

interface ProductApiService {

    @POST("auth/producto")
    suspend fun createProduct(@Body request: ProductRequest): ProductResponse

    @PUT("auth/producto/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body request: ProductRequest
    ): ProductResponse

    @DELETE("auth/producto/{id}")
    suspend fun deleteProduct(@Path("id") id: Long) // No devuelve contenido

    @GET("auth/producto/all")
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<ProductResponse> // Usa tu DTO de paginación

    @GET("auth/producto/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductResponse

    // No incluí /recomendados ya que parece ser para la app de cliente, no admin
}