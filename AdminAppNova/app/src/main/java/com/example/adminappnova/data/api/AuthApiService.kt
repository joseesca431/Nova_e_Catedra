package com.example.adminappnova.data.api

import com.example.adminappnova.data.dto.LoginRequest
import com.example.adminappnova.data.dto.RegisterRequest // 👈 AÑADIR IMPORTACIÓN
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): String

    // --- AÑADIDO ---
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): String
}