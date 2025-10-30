package com.example.aplicacionjetpack.data.dto

// DTO anidado que coincide con la estructura de tu User.java
data class UserRequest(
    val idUser: Long
)

// --- 👇👇👇 ¡EL DTO CORREGIDO! 👇👇👇 ---
data class PagoRequest(
    val detallesPago: String,
    val usuario: UserRequest // Coincide con el 'getUsuario()' de tu PagoRequest.java
)
// --- --------------------------------- ---
