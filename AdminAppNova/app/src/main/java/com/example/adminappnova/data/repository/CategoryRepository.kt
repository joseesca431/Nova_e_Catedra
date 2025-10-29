package com.example.adminappnova.data.repository

import com.example.adminappnova.data.api.CategoryApiService
import com.example.adminappnova.data.dto.CategoryRequest
import com.example.adminappnova.data.dto.CategoryResponse
import javax.inject.Inject

// Interfaz
interface CategoryRepository {
    suspend fun getAllCategories(): Result<List<CategoryResponse>>
    suspend fun createCategory(request: CategoryRequest): Result<CategoryResponse>
    // Agrega aquí las funciones para update, delete, getById si las necesitas en admin
}

// Implementación
class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApiService
) : CategoryRepository {

    override suspend fun getAllCategories(): Result<List<CategoryResponse>> {
        return try {
            val response = api.getAllCategories()
            Result.success(response)
        } catch (e: Exception) {
            // Log.e("CategoryRepo", "Error fetching categories", e) // Consider logging errors
            Result.failure(e)
        }
    }

    override suspend fun createCategory(request: CategoryRequest): Result<CategoryResponse> {
        return try {
            val response = api.createCategory(request)
            Result.success(response)
        } catch (e: Exception) {
            // Log.e("CategoryRepo", "Error creating category", e)
            Result.failure(e)
        }
    }
}