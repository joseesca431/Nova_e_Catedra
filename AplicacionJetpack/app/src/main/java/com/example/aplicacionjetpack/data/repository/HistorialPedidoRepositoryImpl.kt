package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.HistorialPedidoApi
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.data.dto.PagedResponse
import javax.inject.Inject
import kotlin.Result

// --- 👇👇👇 ¡¡¡LA CORRECCIÓN DEFINITIVA!!! 👇👇👇 ---
// La clase DEBE declarar que implementa la interfaz que Hilt está intentando "bindear".
class HistorialPedidoRepositoryImpl @Inject constructor(
    private val api: HistorialPedidoApi
) : HistorialPedidoRepository { // <-- ¡¡ESTA PARTE ": HistorialPedidoRepository" ES LA CLAVE!!
// --- ------------------------------------------------------------------ ---

    override suspend fun getHistorialPaginado(page: Int, size: Int): Result<PagedResponse<HistorialPedidoResponse>> {
        return try {
            val response = api.getHistorial(page, size)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
