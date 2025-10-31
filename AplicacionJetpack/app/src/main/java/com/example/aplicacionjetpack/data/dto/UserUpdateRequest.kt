package com.example.aplicacionjetpack.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para enviar actualizaciones parciales de perfil.
 * Coincide EXACTAMENTE con la lógica de tu backend.
 *
 * - `username` y `email` usan @SerializedName para mapear a "newUsername" y "newEmail".
 * - `telefono` y `newPassword` se envían con sus nombres literales.
 */
data class UserUpdateRequest(
    val currentPassword: String,

    @SerializedName("newUsername") // Mapea 'username' de Kotlin a 'newUsername' de JSON
    val username: String? = null,

    @SerializedName("newEmail") // Mapea 'email' de Kotlin a 'newEmail' de JSON
    val email: String? = null,

    // El teléfono no necesita @SerializedName porque el campo se llama igual en ambos lados
    val telefono: String? = null,

    val newPassword: String? = null
)
