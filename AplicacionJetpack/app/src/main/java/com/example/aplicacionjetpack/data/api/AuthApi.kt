package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.LoginRequest
import com.example.aplicacionjetpack.data.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /**
     * Corresponde a tu @PostMapping("/login") en AuthController
     */
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): String // Devuelve un token JWT

    /**
     * Corresponde a tu @PostMapping("/register") en AuthController
     */
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): String // Devuelve un token JWT
}