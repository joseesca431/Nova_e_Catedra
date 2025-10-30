package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.RegisterRequest

import kotlin.Result // Asegúrate de importar kotlin.Result

// Interfaz que usa el ViewModel
interface AuthRepository {
    // Devuelve un String (token) si tiene éxito
    suspend fun login(username: String, password: String): Result<String>
    suspend fun register(request: RegisterRequest): Result<String>
}