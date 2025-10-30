package com.example.aplicacionjetpack.data.remote

import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    // Para HomeScreen y BusquedaScreen
    @GET("auth/producto/all")
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
        // NOTA: Tu backend no tiene un @Query("search").
        // La búsqueda se hará en el cliente (ViewModel).
    ): PagedResponse<ProductResponse>

    // Para ProductDetailScreen
    @GET("auth/producto/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductResponse

    // TODO: Añadir /recomendados si lo necesitas
}