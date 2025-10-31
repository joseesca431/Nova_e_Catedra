package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.ParametroService
import com.example.aplicacionjetpack.data.dto.ParametroResponse
import javax.inject.Inject

class ParametroRepositoryImpl @Inject constructor(
    private val service: ParametroService
) : ParametroRepository {

    override suspend fun getByClave(clave: String): Result<ParametroResponse> {
        return try {
            val response = service.getByClave(clave)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
