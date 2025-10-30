package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ResenaResponse
import com.example.aplicacionjetpack.data.dto.ResenaRequest
import kotlin.Result

interface ResenaRepository {
    suspend fun getReviewsByProduct(productId: Long): Result<List<ResenaResponse>>
    suspend fun getReviewsByProductPaginated(productId: Long, page: Int, size: Int): Result<PagedResponse<ResenaResponse>>
    suspend fun postReview(request: ResenaRequest): Result<ResenaResponse>
}
