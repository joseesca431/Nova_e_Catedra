package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.DireccionApi
import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import javax.inject.Inject
import kotlin.Result

class DireccionRepositoryImpl @Inject constructor(
    private val api: DireccionApi
) : DireccionRepository {

    // --- 👇👇👇 MÉTODO CREATE CORREGIDO 👇👇👇 ---
    override suspend fun createDireccion(idUser: Long, request: DireccionRequest): Result<DireccionResponse> {
        return try {
            // Llama a la nueva versión del método de la API
            Result.success(api.createDireccion(idUser, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 👇👇👇 ¡¡¡LA IMPLEMENTACIÓN QUE FALTABA!!! 👇👇👇 ---
    override suspend fun getDireccionesByUser(userId: Long): Result<List<DireccionResponse>> {
        return try {
            Result.success(api.getDireccionesByUser(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
