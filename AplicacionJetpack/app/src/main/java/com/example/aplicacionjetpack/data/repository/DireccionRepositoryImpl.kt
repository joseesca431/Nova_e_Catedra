package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.api.DireccionApi
import com.example.aplicacionjetpack.data.dto.DireccionRequest
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import javax.inject.Inject
import kotlin.Result

class DireccionRepositoryImpl @Inject constructor(
    private val api: DireccionApi
) : DireccionRepository {
    private val TAG = "DireccionRepository"

    override suspend fun createDireccion(idUser: Long, request: DireccionRequest): Result<DireccionResponse> {
        return try {
            val response = api.createDireccion(idUser, request)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "createDireccion falló", e)
            Result.failure(e)
        }
    }

    override suspend fun getDireccionesByUser(userId: Long): Result<List<DireccionResponse>> {
        return try {
            val response = api.getDireccionesByUser(userId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "getDireccionesByUser falló", e)
            Result.failure(e)
        }
    }

    // --- 👇👇👇 ¡¡¡LA IMPLEMENTACIÓN QUE FALTABA O ESTABA ROTA!!! 👇👇👇 ---
    // Esta es la implementación que SÍ llama al @DELETE de la API.
    override suspend fun deleteDireccion(idDireccion: Long): Result<Unit> {
        return try {
            // Llama al método de la API que devuelve un Response<Unit>
            val response = api.deleteDireccion(idDireccion)

            // Retrofit considera éxito cualquier código 2xx.
            // Para DELETE, un 200 OK o un 204 No Content son éxitos.
            if (response.isSuccessful) {
                Log.d(TAG, "deleteDireccion($idDireccion) exitoso con código: ${response.code()}")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "Error al borrar dirección: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al borrar dirección: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteDireccion($idDireccion) falló con una excepción", e)
            Result.failure(e)
        }
    }
    // --- ------------------------------------------------------------- ---
}
