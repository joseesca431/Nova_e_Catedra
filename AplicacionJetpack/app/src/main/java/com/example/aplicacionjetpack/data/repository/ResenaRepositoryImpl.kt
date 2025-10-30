// Ruta: app/src/main/java/com/example/aplicacionjetpack/data/repository/ResenaRepositoryImpl.kt
package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.api.ResenaApi
import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ResenaRequest
import com.example.aplicacionjetpack.data.dto.ResenaResponse
import javax.inject.Inject
import kotlin.Result

class ResenaRepositoryImpl @Inject constructor(
    private val resenaApi: ResenaApi // Hilt inyectará la ResenaApi corregida
) : ResenaRepository {

    private val TAG = "ResenaRepo"

    override suspend fun getReviewsByProduct(productId: Long): Result<List<ResenaResponse>> {
        return try {
            val resp = resenaApi.getReviewsByProduct(productId)
            Log.d(TAG, "getReviewsByProduct($productId) OK: ${resp.size}")
            Result.success(resp)
        } catch (e: Exception) {
            Log.e(TAG, "Error getReviewsByProduct($productId)", e)
            Result.failure(e)
        }
    }

    override suspend fun getReviewsByProductPaginated(productId: Long, page: Int, size: Int): Result<PagedResponse<ResenaResponse>> {
        return try {
            val resp = resenaApi.getReviewsByProductPaginated(productId, page, size)
            Log.d(TAG, "getReviewsByProductPaginated($productId,page=$page,size=$size) OK: ${resp.content.size}")
            Result.success(resp)
        } catch (e: Exception) {
            Log.e(TAG, "Error getReviewsByProductPaginated($productId)", e)
            Result.failure(e)
        }
    }

    // AHORA ESTA LLAMADA ES VÁLIDA
    override suspend fun postReview(request: ResenaRequest): Result<ResenaResponse> {
        return try {
            val resp = resenaApi.postReview(request) // <-- ¡Esto ahora funciona!
            Log.d(TAG, "postReview OK: id=${resp.idResena}")
            Result.success(resp)
        } catch (e: Exception) {
            Log.e(TAG, "Error en postReview", e)
            Result.failure(e)
        }
    }
}
