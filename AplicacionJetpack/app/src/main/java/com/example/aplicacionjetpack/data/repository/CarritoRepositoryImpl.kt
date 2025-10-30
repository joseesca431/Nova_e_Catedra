// Ruta: app/src/main/java/com/example/aplicacionjetpack/data/repository/CarritoRepositoryImpl.kt
package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.api.CarritoApi
import com.example.aplicacionjetpack.data.dto.CarritoItemRequest
import com.example.aplicacionjetpack.data.dto.CarritoItemResponse
import com.example.aplicacionjetpack.data.dto.CarritoResponse
import javax.inject.Inject
import kotlin.Result

// ¡SOLO LA CLASE!
class CarritoRepositoryImpl @Inject constructor(
    private val api: CarritoApi
) : CarritoRepository {
    private val TAG = "CarritoRepository"

    override suspend fun getOrCreateCarrito(userId: Long): Result<CarritoResponse> {
        return try {
            Result.success(api.getOrCreateCarrito(userId))
        } catch (e: Exception) {
            Log.e(TAG, "getOrCreateCarrito falló", e)
            Result.failure(e)
        }
    }

    override suspend fun getItems(idCarrito: Long): Result<List<CarritoItemResponse>> {
        return try {
            Result.success(api.getItemsByCarrito(idCarrito))
        } catch (e: Exception) {
            Log.e(TAG, "getItems falló", e)
            Result.failure(e)
        }
    }

    override suspend fun addItem(request: CarritoItemRequest): Result<CarritoItemResponse> {
        return try {
            Result.success(api.addItem(request))
        } catch (e: Exception) {
            Log.e(TAG, "addItem falló", e)
            Result.failure(e)
        }
    }

    override suspend fun updateItem(idItem: Long, request: CarritoItemRequest): Result<CarritoItemResponse> {
        return try {
            Result.success(api.updateItem(idItem, request))
        } catch (e: Exception) {
            Log.e(TAG, "updateItem falló", e)
            Result.failure(e)
        }
    }

    override suspend fun removeItem(idItem: Long): Result<Unit> {
        return try {
            api.removeItem(idItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "removeItem falló", e)
            Result.failure(e)
        }
    }
}
    