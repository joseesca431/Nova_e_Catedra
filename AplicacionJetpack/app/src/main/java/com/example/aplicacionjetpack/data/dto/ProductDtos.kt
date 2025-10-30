package com.example.aplicacionjetpack.data.dto

import java.math.BigDecimal

/**
 * DTOs relacionados con Producto.
 * NOTA: Este archivo NO contiene ResenaResponse/ResenaRequest para evitar duplicados.
 */

// --- DTO para la respuesta de un solo producto ---
data class ProductResponse(
    val idProducto: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: BigDecimal,
    val costo: BigDecimal?,
    val cantidad: Int?, // Tu DTO de Java lo llama 'cantidad'
    val imagen: String?,
    val cantidadPuntos: Int?,
    val idTipoProducto: Long?,
    val nombreTipo: String?,
    val fechaCreacion: String?,
    val fechaActualizacion: String?
)
