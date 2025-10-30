package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import retrofit2.http.Body
import retrofit2.http.GET // <-- Importante
import retrofit2.http.POST
import retrofit2.http.Path // <-- Importante
import retrofit2.http.Query // <-- Importante

interface DireccionApi {

    // --- 👇👇👇 MÉTODO CREATE CORREGIDO 👇👇👇 ---
    // Corresponde a tu @PostMapping que requiere un @RequestParam y un @RequestBody
    @POST("auth/direcciones")
    suspend fun createDireccion(
        @Query("idUser") idUser: Long, // El idUser va como parámetro en la URL
        @Body request: DireccionRequest
    ): DireccionResponse

    // --- 👇👇👇 ¡¡¡EL MÉTODO QUE FALTABA!!! 👇👇👇 ---
    // Corresponde a tu @GetMapping("/user/{idUser}")
    @GET("auth/direcciones/user/{idUser}")
    suspend fun getDireccionesByUser(@Path("idUser") idUser: Long): List<DireccionResponse>
}
