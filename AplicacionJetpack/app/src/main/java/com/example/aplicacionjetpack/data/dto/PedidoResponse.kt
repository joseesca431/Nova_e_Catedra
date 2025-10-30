package com.example.aplicacionjetpack.data.dto

import java.math.BigDecimal

data class PedidoResponse(
    val idPedido: Long,
    val fechaInicio: String?,
    val fechaFinal: String?,
    val total: BigDecimal?,
    val puntosTotales: Int?,
    val idCarrito: Long,
    val tipoPago: String?,
    val estado: String?,
    val idDireccion: Long?,
    val aliasDireccion: String?,
    val calleDireccion: String?,
    val ciudadDireccion: String?,
    val departamentoDireccion: String?
)