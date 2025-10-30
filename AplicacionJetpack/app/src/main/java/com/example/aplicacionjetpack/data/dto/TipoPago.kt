package com.example.aplicacionjetpack.data.dto

// Este enum será nuestro "único punto de la verdad" para los métodos de pago
enum class TipoPago(val displayName: String) {
    TARJETA_CREDITO("Tarjeta de Crédito/Débito"),
    PAYPAL("PayPal"),
    EFECTIVO("Efectivo (Pago contra entrega)")
    // Puedes añadir más aquí, como TRANSFERENCIA_BANCARIA
}
