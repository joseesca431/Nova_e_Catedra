package com.example.aplicacionjetpack.data.dto

// Basado en tu CarritoItemRequest.java
// ¡Esta es la única definición que debe existir!
data class CarritoItemRequest(
    val idCarrito: Long,
    val idProducto: Long,
    val cantidad: Int
)

// Basado en tu CarritoItemResponse.java
// ¡Esta es la única definición que debe existir!
data class CarritoItemResponse(
    val idCarritoItem: Long,
    val idCarrito: Long,
    val idProducto: Long,
    val cantidad: Int?,
    val producto: ProductResponse? // Anidado (Usa el ProductResponse.kt que ya tienes)
)

// Basado en tu CarritoResponse.java
data class CarritoResponse(
    val idCarrito: Long,
    val idUser: Long,
    val fechaCreacion: String?, // Usar String para GSON
    val items: List<CarritoItemResponse>?
)