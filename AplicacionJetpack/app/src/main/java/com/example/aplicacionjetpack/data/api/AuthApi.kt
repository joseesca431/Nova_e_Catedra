package com.example.aplicacionjetpack.data.remote

import com.example.aplicacionjetpack.data.dto.LoginRequest
import com.example.aplicacionjetpack.data.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Llama al endpoint de login y espera un String (token)
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): String // <-- Espera String

    // Llama al endpoint de registro y espera un String (token)
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): String // <-- Espera String
}