package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import kotlin.Result

interface DireccionRepository {
    // --- 👇👇👇 CORREGIDO para aceptar el idUser 👇👇👇 ---
    suspend fun createDireccion(idUser: Long, request: DireccionRequest): Result<DireccionResponse>

    // --- 👇👇👇 ¡¡¡EL MÉTODO QUE FALTABA!!! 👇👇👇 ---
    suspend fun getDireccionesByUser(userId: Long): Result<List<DireccionResponse>>
}
