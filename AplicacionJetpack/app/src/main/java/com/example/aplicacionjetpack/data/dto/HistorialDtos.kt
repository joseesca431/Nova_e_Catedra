package com.example.aplicacionjetpack.data.dto

// Este archivo debe ser un espejo de tu HistorialPedidoResponse.java
data class HistorialPedidoResponse(
    val idHistorialPedido: Long,
    val idPedido: Long,
    val idUser: Long,
    val estado: String, // En Kotlin, el enum se recibe como String
    val fecha: String,    // LocalDateTime se recibe como String
    val descripcion: String?
)
