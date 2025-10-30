package com.example.aplicacionjetpack.data.dto

import java.math.BigDecimal

// Basado en tu CarritoItemController.java (asumiendo campos de CarritoItemRequest)
data class CarritoItemRequest(
    val idProducto: Long,
    val cantidad: Int
    // Asumimos que el backend obtiene idCarrito/idUser del token
)

// Basado en tu CarritoItemController.java (asumiendo campos de CarritoItemResponse)
data class CarritoItemResponse(
    val idCarritoItem: Long,
    val idProducto: Long,
    val nombreProducto: String,
    val precioProducto: BigDecimal,
    val cantidad: Int,
    val imagenProducto: String?
)