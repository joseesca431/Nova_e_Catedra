package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.data.remote.ProductApi
import javax.inject.Inject
import kotlin.Result

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi
) : ProductRepository {

    private val TAG = "ProductRepo"

    override suspend fun getAllProducts(page: Int, size: Int): Result<PagedResponse<ProductResponse>> {
        return try {
            val response = productApi.getAllProducts(page, size)
            Log.d(TAG, "getAllProducts exitoso, ${response.content.size} productos recibidos.")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en getAllProducts", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: Long): Result<ProductResponse> {
        return try {
            val response = productApi.getProductById(id)
            Log.d(TAG, "getProductById($id) exitoso.")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en getProductById($id)", e)
            Result.failure(e)
        }
    }
}