package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.UserResponse
import com.example.aplicacionjetpack.data.dto.UserUpdateRequest
import kotlin.Result

interface UserRepository {
    // --- 👇👇👇 ¡¡¡LA FUNCIÓN CON EL NOMBRE CORRECTO!!! 👇👇👇 ---
    // Debe coincidir con el nombre en la API y en la Implementación.
    suspend fun getUserProfile(id: Long): Result<UserResponse>
    suspend fun updateProfile(id: Long, request: UserUpdateRequest): Result<UserResponse>
    suspend fun changePassword(id: Long, current: String, new: String): Result<Unit>
    // --- ---------------------------------------------------- ---
}
