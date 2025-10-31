package com.example.adminappnova.data.dto

import java.math.BigDecimal

enum class EstadoPedido {
    CARRITO, PENDIENTE, PAGADO, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO
}

enum class TipoPago {
    EFECTIVO, PAYPAL, TARJETA_CREDITO, TARJETA
}

/**
 * DTO limpio que representa la respuesta de un pedido individual
 * de la API (ej. /auth/pedido/{id}).
 */
data class PedidoResponse(
    val idPedido: Long,
    val fechaInicio: String,
    val fechaFinal: String?,
    val total: BigDecimal?,
    val puntosTotales: Int?,
    val idCarrito: Long?,
    val tipoPago: TipoPago?,
    val estado: EstadoPedido?,
    val idDireccion: Long?,
    val aliasDireccion: String?,
    val calleDireccion: String?,
    val ciudadDireccion: String?,
    val departamentoDireccion: String?,
    val idUser: Long? = null // <- necesario
)


// --- DTOs de Petición (Request) ---

/**
 * DTO para la petición de checkout (crear un pedido desde un carrito).
 */
data class PedidoRequest(
    val idCarrito: Long,
    val tipoPago: TipoPago,
    val cuponCodigo: String?,
    val idDireccion: Long?
)

/**
 * DTO para la petición de pago.
 */
data class PagoRequest(
    val metodo: String,
    val referencia: String
)

/**
 * DTO para representar un item de producto dentro de un pedido.
 * * !! COMENTARIO CORREGIDO !!
 * La API SÍ provee un endpoint para esto, y se está usando en el ViewModel.
 * (ver: PedidoApiService.kt -> getPedidoItems)
 */
data class PedidoItemDto(
    val idProducto: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: BigDecimal
)