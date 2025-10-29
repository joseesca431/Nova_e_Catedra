package com.example.adminappnova.data.dto

import java.math.BigDecimal
// Si usas java.time (API 26+):
import java.time.LocalDateTime
// Si usas ThreeTenABP (API < 26), importa esto en su lugar:
// import org.threeten.bp.LocalDateTime

// --- 👇 ENUM CORREGIDO (COINCIDE CON TU JAVA) 👇 ---
enum class EstadoPedido {
    CARRITO,        // Pedido en carrito
    PENDIENTE,      // Confirmado, esperando pago
    PAGADO,         // Pago exitoso
    EN_PROCESO,     // Preparando envío
    ENVIADO,        // En camino
    ENTREGADO,      // Recibido por el cliente
    CANCELADO       // Pedido cancelado
}
// ---------------------------------------------

// --- ENUM TIPO PAGO (ACTUALIZADO SEGÚN TUS LOGS/DTOS) ---
enum class TipoPago {
    EFECTIVO,
    PAYPAL,
    TARJETA_CREDITO,
    TARJETA // Añade otros si existen
}
// ---------------------------------------------------

// --- DTOs (SIN CAMBIOS, SOLO PARA CONTEXTO) ---

// Coincide con PedidoResponse.java
data class PedidoResponse(
    val idPedido: Long,
    val fechaInicio: String, // O LocalDateTime/Instant si usas un TypeAdapter
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
    val departamentoDireccion: String?
    // Los campos _links de HATEOAS se ignoran por defecto
)

// Coincide con PedidoRequest.java
data class PedidoRequest(
    val idCarrito: Long,
    val tipoPago: TipoPago,
    val cuponCodigo: String?,
    val idDireccion: Long?
)

// Coincide con PagoRequest.java (Asumiendo campos)
data class PagoRequest(
    val metodo: String,
    val referencia: String
)