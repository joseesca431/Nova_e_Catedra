package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.dto.LoginRequest
import com.example.aplicacionjetpack.data.dto.RegisterRequest
import com.example.aplicacionjetpack.data.remote.AuthApi
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi // Hilt inyectará la implementación de AuthApi
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val request = LoginRequest(username, password)
            val token = authApi.login(request) // Llama a la API
            Log.d("AuthRepo", "Login exitoso, token obtenido")
            Result.success(token)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Fallo en login", e)
            Result.failure(e) // Devuelve la excepción
        }
    }

    override suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            val token = authApi.register(request) // Llama a la API
            Log.d("AuthRepo", "Registro exitoso, token obtenido")
            Result.success(token)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Fallo en registro", e)
            Result.failure(e)
        }
    }

}