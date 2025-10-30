package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.UserApi
import com.example.aplicacionjetpack.data.dto.UserResponse
import javax.inject.Inject
import kotlin.Result

// --- 👇👇👇 ¡LA IMPLEMENTACIÓN QUE SÍ COMPILA Y FUNCIONA! 👇👇👇 ---
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    // --- AHORA SOBREESCRIBE LA FUNCIÓN QUE SÍ EXISTE ---
    override suspend fun getUserProfile(id: Long): Result<UserResponse> {
        return try {
            // --- Y LLAMA A LA FUNCIÓN DE LA API QUE SÍ EXISTE ---
            val response = api.getUserProfile(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
