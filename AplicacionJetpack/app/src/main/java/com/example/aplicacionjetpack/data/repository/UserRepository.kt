package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.UserResponse
import kotlin.Result

interface UserRepository {
    // --- 👇👇👇 ¡¡¡LA FUNCIÓN CON EL NOMBRE CORRECTO!!! 👇👇👇 ---
    // Debe coincidir con el nombre en la API y en la Implementación.
    suspend fun getUserProfile(id: Long): Result<UserResponse>
    // --- ---------------------------------------------------- ---
}
