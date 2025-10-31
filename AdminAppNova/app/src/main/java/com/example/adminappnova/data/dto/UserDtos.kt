package com.example.adminappnova.data.dto

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName(value = "idUser", alternate = ["id"])
    val idUser: Long,
    val primerNombre: String? = null,
    val primerApellido: String? = null,
    val email: String? = null,
    val username: String? = null,
    @SerializedName("roleName")
    val role: String? = null
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