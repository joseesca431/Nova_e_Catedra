package com.example.aplicacionjetpack.data.dto

// Coincide con tu LoginRequest.java
data class LoginRequest(
    val username: String,
    val password: String
)

/**
* RegisterRequest usado por el cliente.
* Nota: fechaNacimiento debe enviarse en formato "dd/MM/yyyy"
* (coincide con el @JsonFormat(pattern = "dd/MM/yyyy") del DTO Java).
*/
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val primerNombre: String,
    val segundoNombre: String? = null,
    val primerApellido: String,
    val segundoApellido: String? = null,
    val fechaNacimiento: String, // "dd/MM/yyyy"
    val telefono: String? = null,
    val dui: String? = null,
    val direccion: String? = null
)
// NOTA: Tu API devuelve un String (token) para login/register,
// así que NO necesitamos un AuthResponse DTO.