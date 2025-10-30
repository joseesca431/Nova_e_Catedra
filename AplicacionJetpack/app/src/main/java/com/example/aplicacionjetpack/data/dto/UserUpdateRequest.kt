package com.example.aplicacionjetpack.data.dto

/**
 * DTO para la petición de actualizar el perfil de usuario.
 * Coincide con la lógica del backend que espera estos campos.
 */
data class UserUpdateRequest(
    val username: String,
    val email: String,
    val telefono: String,
    val newPassword: String?, // Nulable: si es nulo, el backend no cambia la contraseña
    val currentPassword: String // Obligatoria para autenticar la petición
)
