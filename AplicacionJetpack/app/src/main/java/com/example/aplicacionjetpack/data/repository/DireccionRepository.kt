package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import kotlin.Result

interface DireccionRepository {
    suspend fun createDireccion(idUser: Long, request: DireccionRequest): Result<DireccionResponse>
    suspend fun getDireccionesByUser(userId: Long): Result<List<DireccionResponse>>
    // --- 👇👇👇 ¡LA FUNCIÓN QUE FALTABA! 👇👇👇 ---
    suspend fun deleteDireccion(idDireccion: Long): Result<Unit>
}
