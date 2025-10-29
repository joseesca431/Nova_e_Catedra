package com.example.adminappnova.data.api

import com.example.adminappnova.data.dto.CategoryRequest
import com.example.adminappnova.data.dto.CategoryResponse
import retrofit2.http.*

// "Traducción" de TipoProductoController
interface CategoryApiService {

    @GET("auth/tipoproducto")
    suspend fun getAllCategories(): List<CategoryResponse>

    @GET("auth/tipoproducto/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): CategoryResponse

    @POST("auth/tipoproducto")
    suspend fun createCategory(@Body request: CategoryRequest): CategoryResponse

    @PUT("auth/tipoproducto/{id}")
    suspend fun updateCategory(
        @Path("id") id: Long,
        @Body request: CategoryRequest
    ): CategoryResponse

    @DELETE("auth/tipoproducto/{id}")
    suspend fun deleteCategory(@Path("id") id: Long) // No devuelve contenido
}