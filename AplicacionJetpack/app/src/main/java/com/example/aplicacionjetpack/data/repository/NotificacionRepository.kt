package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import kotlin.Result

interface NotificacionRepository {
    suspend fun getNotificaciones(): Result<List<NotificacionResponse>>
    suspend fun marcarLeida(id: Long): Result<Unit>
}
