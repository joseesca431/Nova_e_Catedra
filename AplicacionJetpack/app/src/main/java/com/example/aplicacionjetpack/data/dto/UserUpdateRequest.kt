package com.example.aplicacionjetpack.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para enviar actualizaciones parciales de perfil.
 * Los campos puede ser null para indicar "no cambiar".
 *
 * - currentPassword: obligatorio (autorización)
 * - username/email/telefono/newPassword: opcionales (nullable)
 *
 * Nota: usamos @SerializedName para mapear al backend Java que espera
 * "newUsername" / "newEmail".
 */
data class UserUpdateRequest(
    val currentPassword: String,
    @SerializedName("newUsername") val username: String? = null,
    @SerializedName("newEmail") val email: String? = null,
    val telefono: String? = null,
    val newPassword: String? = null
)
