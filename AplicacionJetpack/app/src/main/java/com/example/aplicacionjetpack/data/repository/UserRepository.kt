package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.dto.UserResponse
import kotlin.Result

interface UserRepository {
    suspend fun getUserProfile(userId: Long): Result<UserResponse>
}
