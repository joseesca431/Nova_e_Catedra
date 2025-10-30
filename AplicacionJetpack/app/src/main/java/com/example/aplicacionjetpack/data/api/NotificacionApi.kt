package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificacionApi {
    @GET("auth/notificacion/usuario/{id}")
    suspend fun getNotificacionesByUsuario(@Path("id") idUsuario: Long): List<NotificacionResponse>

    @PUT("auth/notificacion/leer/{id}")
    suspend fun marcarComoLeida(@Path("id") idNotificacion: Long)
}
