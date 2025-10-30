// Ruta: app/src/main/java/com/example/aplicacionjetpack/data/repository/CarritoRepository.kt
package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.CarritoItemRequest
import com.example.aplicacionjetpack.data.dto.CarritoItemResponse
import com.example.aplicacionjetpack.data.dto.CarritoResponse
import kotlin.Result

// ¡SOLO LA INTERFAZ!
interface CarritoRepository {
    suspend fun getOrCreateCarrito(userId: Long): Result<CarritoResponse>
    suspend fun getItems(idCarrito: Long): Result<List<CarritoItemResponse>>
    suspend fun addItem(request: CarritoItemRequest): Result<CarritoItemResponse>
    suspend fun updateItem(idItem: Long, request: CarritoItemRequest): Result<CarritoItemResponse>
    suspend fun removeItem(idItem: Long): Result<Unit>
}
    