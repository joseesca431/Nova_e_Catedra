package com.example.adminappnova.data.dto

import java.math.BigDecimal // Importa BigDecimal
import java.time.LocalDateTime // O la librería de fecha que uses

// Coincide con ProductoRequest.java
data class ProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: BigDecimal,
    val costo: BigDecimal, // Añadido
    val cantidad: Int,
    val imagen: String?, // Nullable
    val cantidadPuntos: Int,
    val idTipoProducto: Long
)

// Coincide con ProductoResponse.java
data class ProductResponse(
    val idProducto: Long,
    val nombre: String,
    val descripcion: String?, // Nullable si puede ser nulo
    val precio: BigDecimal,
    val costo: BigDecimal?, // Nullable si puede ser nulo
    val cantidad: Int?, // Renombrado de 'stock' a 'cantidad' para coincidir, Nullable si puede ser nulo
    val imagen: String?, // Nullable
    val cantidadPuntos: Int?, // Nullable si puede ser nulo
    val idTipoProducto: Long?, // Nullable si puede ser nulo
    val nombreTipo: String?, // Nullable si puede ser nulo
    val fechaCreacion: String?, // String ISO
    val fechaActualizacion: String? // String ISO
    // Ignoramos RepresentationModel
)