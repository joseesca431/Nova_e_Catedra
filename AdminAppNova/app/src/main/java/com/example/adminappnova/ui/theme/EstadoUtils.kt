package com.example.adminappnova.ui.utils

import androidx.compose.ui.graphics.Color
import com.example.adminappnova.data.dto.EstadoPedido
import java.util.Locale

fun estadoColor(estado: EstadoPedido?): Color {
    val name = estado?.name?.uppercase(Locale.getDefault()) ?: ""
    return when (name) {
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
