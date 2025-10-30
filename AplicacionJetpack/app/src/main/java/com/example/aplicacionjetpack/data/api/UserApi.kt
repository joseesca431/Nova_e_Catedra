package com.example.aplicacionjetpack.data.api

import com.example.aplicacionjetpack.data.dto.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("auth/users/{id}")
    suspend fun getUserProfile(@Path("id") id: Long): UserResponse
}
