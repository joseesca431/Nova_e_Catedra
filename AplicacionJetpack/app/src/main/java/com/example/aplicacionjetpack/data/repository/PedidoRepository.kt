// Ruta: data/repository/PedidoRepository.kt
package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import kotlin.Result

// ¡SOLO LA INTERFAZ!
interface PedidoRepository {
    suspend fun checkout(request: PedidoRequest): Result<PedidoResponse>
    suspend fun pagar(idPedido: Long, request: PagoRequest): Result<PedidoResponse>
}
    