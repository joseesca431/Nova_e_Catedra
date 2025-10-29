package com.example.adminappnova.data.repository

import com.example.adminappnova.data.api.ProductApiService
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.ProductRequest
import com.example.adminappnova.data.dto.ProductResponse
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Interfaz ---
interface ProductRepository {
    suspend fun getAllProducts(page: Int, size: Int): Result<PagedResponse<ProductResponse>>
    suspend fun getProductById(id: Long): Result<ProductResponse>
    suspend fun createProduct(request: ProductRequest): Result<ProductResponse>
    suspend fun updateProduct(id: Long, request: ProductRequest): Result<ProductResponse>
    suspend fun deleteProduct(id: Long): Result<Unit> // Delete no devuelve cuerpo
}

// --- Implementación ---
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService // Asume que tienes ProductApiService.kt
) : ProductRepository {

    override suspend fun getAllProducts(page: Int, size: Int): Result<PagedResponse<ProductResponse>> {
        return try {
            val response = api.getAllProducts(page, size)
            Result.success(response)
        } catch (e: Exception) {
            // Considera loggear el error: Log.e("ProductRepo", "Error fetching all products", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: Long): Result<ProductResponse> {
        return try {
            val response = api.getProductById(id)
            Result.success(response)
        } catch (e: Exception) {
            // Considera loggear el error
            Result.failure(e)
        }
    }

    override suspend fun createProduct(request: ProductRequest): Result<ProductResponse> {
        return try {
            val response = api.createProduct(request)
            Result.success(response)
        } catch (e: Exception) {
            // Considera loggear el error
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(id: Long, request: ProductRequest): Result<ProductResponse> {
        return try {
            val response = api.updateProduct(id, request)
            Result.success(response)
        } catch (e: Exception) {
            // Considera loggear el error
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(id: Long): Result<Unit> {
        return try {
            api.deleteProduct(id) // Llama al endpoint DELETE
            Result.success(Unit) // Si no hay excepción, fue exitoso
        } catch (e: Exception) {
            // Considera loggear el error
            Result.failure(e)
        }
    }
}