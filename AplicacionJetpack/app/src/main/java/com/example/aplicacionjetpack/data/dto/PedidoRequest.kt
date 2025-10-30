package com.example.aplicacionjetpack.data.dto

data class PedidoRequest(
    val idCarrito: Long,
    val tipoPago: String, // "TARJETA_CREDITO"
    val cuponCodigo: String? = null,
    val idDireccion: Long

)