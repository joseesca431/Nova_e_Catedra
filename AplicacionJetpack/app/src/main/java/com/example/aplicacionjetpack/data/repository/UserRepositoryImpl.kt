package com.example.aplicacionjetpack.data.repository

import com.example.aplicacionjetpack.data.api.UserApi
import com.example.aplicacionjetpack.data.dto.UserResponse
import javax.inject.Inject
import kotlin.Result

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {
    override suspend fun getUserProfile(userId: Long): Result<UserResponse> {
        return try {
            Result.success(api.getUserProfile(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
