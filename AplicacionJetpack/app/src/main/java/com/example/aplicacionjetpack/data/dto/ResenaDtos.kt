package com.example.aplicacionjetpack.data.dto

import com.google.gson.annotations.SerializedName

// ResenaResponse según tu backend (ajustar nombres)
data class ResenaResponse(
    val idResena: Long,
    // backend usa 'username' en Java -> lo mapeamos a nombreUsuario para consistencia
    @SerializedName("username")
    val nombreUsuario: String?,
    @SerializedName("productoNombre")
    val productoNombre: String?,
    val comentario: String?,
    val fecha: String?, // ISO string (LocalDateTime) -> mostrar tal cual o formatear
    val rating: String? // El backend envía RatingEnum como nombre (ej "FIVE", "THREE_HALF")
)

// Request para crear reseña en backend
data class ResenaRequest(
    val idUser: Long,
    val idProducto: Long,
    val comentario: String?,
    val rating: String? // Debe coincidir con el nombre del enum (ej "FIVE", "THREE_HALF")
)
