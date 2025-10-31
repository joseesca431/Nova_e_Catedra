package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.ParametroResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ParametroService {
    @GET("auth/parametros/clave/{clave}")
    suspend fun getByClave(@Path("clave") clave: String): Response<ParametroResponse>
}
