// Ruta: app/src/main/java/com/example/aplicacionjetpack/data/api/ResenaApi.kt
package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ResenaRequest
import com.example.aplicacionjetpack.data.dto.ResenaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ResenaApi {
    @GET("auth/resenas/producto/{id}/paginated")
    suspend fun getReviewsByProductPaginated(
        @Path("id") productId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<ResenaResponse>

    @GET("auth/resenas/producto/{id}")
    suspend fun getReviewsByProduct(@Path("id") productId: Long): List<ResenaResponse>

    // --- ¡¡¡LA CORRECCIÓN DEFINITIVA ESTÁ AQUÍ!!! ---
    @POST("auth/resenas")
    suspend fun postReview(@Body request: ResenaRequest): ResenaResponse // <-- RENOMBRADO A postReview
}
