package com.example.aplicacionjetpack.data.dto

// Coincide con tu LoginRequest.java
data class LoginRequest(
    val username: String,
    val password: String
)

// Coincide con tu UserCreateRequest.java (que se usa para /auth/register)
data class RegisterRequest(
    val primerNombre: String,
    val primerApellido: String,
    val email: String,
    val username: String,
    val password: String,
    val fechaNacimiento: String, // Enviar como String ISO "YYYY-MM-DD"
    val roleName: String,
    // Campos Opcionales (pueden ser null o String vacío)
    val segundoNombre: String? = null,
    val segundoApellido: String? = null,
    val telefono: String? = null,
    val dui: String? = null, // Renombrado de DUI
    val direccion: String? = null
)

// NOTA: Tu API devuelve un String (token) para login/register,
// así que NO necesitamos un AuthResponse DTO.