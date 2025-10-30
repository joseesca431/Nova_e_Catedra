package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.PagoRequest
import com.example.aplicacionjetpack.data.dto.PedidoRequest
import com.example.aplicacionjetpack.data.dto.PedidoResponse
import kotlin.Result

interface PedidoRepository {
    // --- 👇👇👇 ¡LA NUEVA FUNCIÓN QUE LO HACE TODO! 👇👇👇 ---
    // Esta función representa la idea de crear y pagar en un solo paso.
    // Aunque el backend aún no la tenga, la definimos aquí para el ViewModel.
    suspend fun createAndPayOrder(pedidoRequest: PedidoRequest, pagoRequest: PagoRequest): Result<PedidoResponse>

    // Mantenemos las viejas por si las necesitas en otro lado, pero el nuevo flujo no las usa.
    suspend fun checkout(request: PedidoRequest): Result<PedidoResponse>
    suspend fun pagar(idPedido: Long, request: PagoRequest): Result<PedidoResponse>
}
