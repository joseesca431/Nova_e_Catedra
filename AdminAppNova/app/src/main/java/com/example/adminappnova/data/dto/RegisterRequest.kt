package com.example.adminappnova.data.dto

// Esta data class debe coincidir con tu RegisterRequest.java
// (Ajusta los campos si es necesario)
data class RegisterRequest(
    val primerNombre: String,
    val primerApellido: String,
    val email: String,
    val username: String,
    val password: String
    // NOTA: Tu UserCreateRequest también tiene roleName, telefono, DUI, etc.
    // Añádelos aquí si tu RegisterRequest los necesita.
)