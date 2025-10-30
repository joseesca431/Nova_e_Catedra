package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.dto.PagedResponse
import kotlin.Result

interface HistorialPedidoRepository {
    suspend fun getHistorialPaginado(page: Int, size: Int): Result<PagedResponse<HistorialPedidoResponse>>
}
