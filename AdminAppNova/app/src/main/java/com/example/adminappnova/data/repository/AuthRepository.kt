package com.example.adminappnova.data.repository

import com.example.adminappnova.data.dto.RegisterRequest // 👈 AÑADIR IMPORTACIÓN

// La interfaz ahora devuelve un String (el token)
interface AuthRepository {

    suspend fun login(username: String, password: String): Result<String>

    // --- AÑADIDO ---
    // Es más fácil pasar el objeto request completo desde el ViewModel
    // ya que son muchos campos.
    suspend fun register(request: RegisterRequest): Result<String>
}