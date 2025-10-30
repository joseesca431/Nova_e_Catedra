package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.PagedResponse
import com.example.aplicacionjetpack.data.dto.ProductResponse
import kotlin.Result

interface ProductRepository {
    suspend fun getAllProducts(page: Int, size: Int): Result<PagedResponse<ProductResponse>>
    suspend fun getProductById(id: Long): Result<ProductResponse>
}