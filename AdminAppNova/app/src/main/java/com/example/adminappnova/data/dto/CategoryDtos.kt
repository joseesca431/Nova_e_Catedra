package com.example.adminappnova.data.dto

// Coincide con TipoProductoRequest.java
data class CategoryRequest(
    // Renombrado de 'nombre' a 'tipo' para coincidir
    val tipo: String,
    val descripcion: String? // Nullable
)

// Coincide con TipoProductoResponse.java
data class CategoryResponse(
    val idTipoProducto: Long,
    // Renombrado de 'nombre' a 'tipo' para coincidir
    val tipo: String,
    val descripcion: String? // Nullable
)