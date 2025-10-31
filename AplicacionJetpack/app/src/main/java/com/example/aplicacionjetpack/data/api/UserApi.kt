package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.UserResponse
import com.example.aplicacionjetpack.data.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("auth/users/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): UserResponse

    // --- 👇👇👇 ¡¡¡ENDPOINT #1: ACTUALIZAR PERFIL!!! 👇👇👇 ---
    // Corresponde a tu @PutMapping("/{id}/profile")
    @PUT("auth/users/{id}/profile")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body request: UserUpdateRequest
    ): UserResponse

    // --- 👇👇👇 ¡¡¡ENDPOINT #2: CAMBIAR CONTRASEÑA!!! 👇👇👇 ---
    // Corresponde a tu @PutMapping("/{id}/password")
    @PUT("auth/users/{id}/password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Query("currentPassword") currentPassword: String,
        @Query("newPassword") newPassword: String
    ): Response<Unit> // Devuelve un 204 No Content, por eso Response<Unit>
}
