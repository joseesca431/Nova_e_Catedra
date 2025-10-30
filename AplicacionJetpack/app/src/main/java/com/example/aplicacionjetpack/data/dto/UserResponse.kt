package com.example.aplicacionjetpack.data.dto

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val fechaNacimiento: String?, // La API lo envía como String "yyyy-MM-dd"
    val telefono: String?,
    val roleName: String?
    // Tu backend no envía los nombres y apellidos, así que no los incluimos aquí.
    // Usaremos el 'username' como el nombre.
)
