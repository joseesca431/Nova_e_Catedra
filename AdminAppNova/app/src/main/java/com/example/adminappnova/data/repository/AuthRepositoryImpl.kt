package com.example.adminappnova.data.repository

import com.example.adminappnova.data.api.AuthApiService
import com.example.adminappnova.data.dto.LoginRequest
import com.example.adminappnova.data.dto.RegisterRequest // 👈 AÑADIR IMPORTACIÓN
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val request = LoginRequest(username = username, password = password)
            val token = api.login(request)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- AÑADIDO ---
    override suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            // El 'request' ya viene listo desde el ViewModel
            val token = api.register(request)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}