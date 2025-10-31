package com.example.adminappnova.ui.util

import androidx.compose.ui.graphics.Color
import com.example.adminappnova.data.dto.EstadoPedido
import java.util.Locale

/**
 * Función única para obtener el color según el estado del pedido.
 * Usar esta función desde cualquier screen para evitar duplicados y ambigüedades.
 */
fun estadoColor(estado: EstadoPedido?): Color {
    val name = estado?.name ?: ""
    return when (name.uppercase(Locale.getDefault())) {
        "CARRITO" -> Color(0xFF78909C)
        "PENDIENTE" -> Color(0xFFFFA726)
        "PAGADO" -> Color(0xFF66BB6A)
        "EN_PROCESO", "ENPROCESO", "EN PROCESO" -> Color(0xFFAB47BC)
        "ENVIADO" -> Color(0xFF29B6F6)
        "ENTREGADO" -> Color(0xFF2E7D32)
        "CANCELADO" -> Color(0xFFEF5350)
        else -> Color.Gray
    }
}
