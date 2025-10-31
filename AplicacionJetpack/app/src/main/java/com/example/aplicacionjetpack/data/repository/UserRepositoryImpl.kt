package com.example.aplicacionjetpack.data.repository

import android.util.Log
import com.example.aplicacionjetpack.data.api.UserApi
import com.example.aplicacionjetpack.data.dto.UserResponse
import com.example.aplicacionjetpack.data.dto.UserUpdateRequest
import javax.inject.Inject
import kotlin.Result // <-- ¡AÑADIMOS EL IMPORT QUE FALTABA!

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    private val TAG = "UserRepository"

    override suspend fun getUserProfile(userId: Long): Result<UserResponse> {
        return try {
            Result.success(userApi.getUserProfile(userId))
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en getUserProfile", e)
            Result.failure(e)
        }
    }

    // Ahora este override es VÁLIDO
    override suspend fun updateProfile(id: Long, request: UserUpdateRequest): Result<UserResponse> {
        return try {
            val response = userApi.updateProfile(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en updateProfile", e)
            Result.failure(e)
        }
    }

    // Ahora este override es VÁLIDO
    override suspend fun changePassword(id: Long, current: String, new: String): Result<Unit> {
        return try {
            userApi.changePassword(id, current, new)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo en changePassword", e)
            Result.failure(e)
        }
    }
}
