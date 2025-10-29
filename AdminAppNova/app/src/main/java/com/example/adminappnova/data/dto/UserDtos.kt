package com.example.adminappnova.data.dto

// Para UserResponse
data class UserResponse(
    val idUser: Long,
    val primerNombre: String,
    val primerApellido: String,
    val email: String,
    val username: String,
    val role: String // Asumiendo que conviertes el Rol a un String
    // Agrega los demás campos
)

// Para UserCreateRequest
data class UserCreateRequest(
    val primerNombre: String,
    val primerApellido: String,
    val email: String,
    val username: String,
    val contrasena: String,
    val roleName: String // "ADMIN" o "USER"
    // Agrega los demás campos
)

// ... (Crea también data classes para UserUpdateAdminRequest y UserUpdateRequest)