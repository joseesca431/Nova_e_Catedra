package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.ParametroResponse

interface ParametroRepository {
    suspend fun getByClave(clave: String): Result<ParametroResponse>
}
