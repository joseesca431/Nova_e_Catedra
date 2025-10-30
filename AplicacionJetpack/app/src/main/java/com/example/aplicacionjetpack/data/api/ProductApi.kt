// Ruta: app/src/main/java/com/example/aplicacionjetpack/data/api/ProductApi.kt
package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    // --- 👇👇👇 ¡¡¡LA CORRECCIÓN DEFINITIVA ESTÁ AQUÍ!!! 👇👇👇 ---
    // La ruta correcta es "/all", no "/all/paginated"
    @GET("auth/producto/all")
    // --- ----------------------------------------------------- ---
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<ProductResponse>

    @GET("auth/producto/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductResponse
}
