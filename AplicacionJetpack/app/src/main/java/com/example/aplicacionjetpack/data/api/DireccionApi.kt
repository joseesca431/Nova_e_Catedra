package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import retrofit2.Response // <-- ¡IMPORTANTE!
import retrofit2.http.Body
import retrofit2.http.DELETE // <-- ¡IMPORTANTE!
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DireccionApi {

    @POST("auth/direcciones")
    suspend fun createDireccion(
        @Query("idUser") idUser: Long,
        @Body request: DireccionRequest
    ): DireccionResponse

    @GET("auth/direcciones/user/{idUser}")
    suspend fun getDireccionesByUser(@Path("idUser") idUser: Long): List<DireccionResponse>

    // --- 👇👇👇 ¡¡¡EL MÉTODO QUE FALTABA!!! 👇👇👇 ---
    // Corresponde a tu @DeleteMapping("/{id}") del backend
    @DELETE("auth/direcciones/{id}")
    suspend fun deleteDireccion(@Path("id") idDireccion: Long): Response<Unit>
}
