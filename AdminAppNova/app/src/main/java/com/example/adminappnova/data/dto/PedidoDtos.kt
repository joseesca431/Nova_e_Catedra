package com.example.adminappnova.data.dto

import java.math.BigDecimal // Importa BigDecimal
// Importa cualquier librería de fecha/hora que uses (ej: kotlinx-datetime o java.time si tu minSdk lo permite)
// import kotlinx.datetime.LocalDateTime // Ejemplo con kotlinx-datetime
import java.time.LocalDateTime // Ejemplo con java.time (minSdk 26+)

// --- Enums (deben coincidir con los de tu backend) ---
enum class EstadoPedido {
    PENDIENTE, CONFIRMADO, PAGADO, EN_ENVIO, ENTREGADO, CANCELADO
    // Asegúrate de que los nombres coincidan EXACTAMENTE con tu enum Java
}

enum class TipoPago {
    TARJETA, EFECTIVO, TRANSFERENCIA
    // Asegúrate de que los nombres coincidan EXACTAMENTE con tu enum Java
}

// --- DTOs ---

// Coincide con PedidoResponse.java
data class PedidoResponse(
    val idPedido: Long,
    val fechaInicio: String, // Retrofit/Gson usualmente maneja Strings ISO para fechas/horas
    val fechaFinal: String?, // Nullable si puede ser nulo
    val total: BigDecimal,
    val puntosTotales: Int?, // Nullable si puede ser nulo
    val idCarrito: Long?,    // Nullable si puede ser nulo
    val tipoPago: TipoPago?,  // Nullable si puede ser nulo
    val estado: EstadoPedido?, // Nullable si puede ser nulo
    val idDireccion: Long?,   // Nullable si puede ser nulo
    val aliasDireccion: String?, // Nullable si puede ser nulo
    val calleDireccion: String?, // Nullable si puede ser nulo
    val ciudadDireccion: String?, // Nullable si puede ser nulo
    val departamentoDireccion: String? // Nullable si puede ser nulo
    // Ignoramos RepresentationModel de HATEOAS por ahora
)

// Coincide con PedidoRequest.java
data class PedidoRequest(
    val idCarrito: Long,
    val tipoPago: TipoPago,
    val cuponCodigo: String?, // Nullable
    val idDireccion: Long?   // Nullable
)

// Coincide con PagoRequest.java (Necesitarás crearlo si no lo tienes)
// Asumiendo que PagoRequest tiene algo como esto:
data class PagoRequest(
    val metodo: String, // Ejemplo: "Tarjeta XYZ"
    val referencia: String // Ejemplo: "Transacción #12345"
    // Añade los campos que realmente tenga tu PagoRequest.java
)