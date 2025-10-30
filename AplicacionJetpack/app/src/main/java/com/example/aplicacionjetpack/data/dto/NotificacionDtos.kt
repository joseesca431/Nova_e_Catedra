package com.example.aplicacionjetpack.data.dto

// Basado en el log de okhttp del 2025-10-30 17:16:48.866
data class NotificacionResponse(
    // --- 👇👇👇 ¡¡¡LA CORRECCIÓN DEFINITIVA BASADA EN EL LOG!!! 👇👇👇 ---
    val id: Long, // El campo en el JSON es "id", no "idNotificacion"
    // --- ----------------------------------------------------------- ---
    val mensaje: String,
    val fechaEnvio: String,
    val estado: String,
    val pedidoId: Long
)
