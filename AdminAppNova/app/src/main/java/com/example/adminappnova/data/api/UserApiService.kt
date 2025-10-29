package com.example.adminappnova.data.api

import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.UserCreateRequest
import com.example.adminappnova.data.dto.UserResponse
import retrofit2.http.*

// Nota: Faltan los DTOs para UserUpdateRequest y UserUpdateAdminRequest
interface UserApiService {

    @GET("auth/users")
    suspend fun listUsers(): List<UserResponse>

    // No incluí /profile ya que es para que el usuario edite su perfil
    // No incluí /password ya que es para que el usuario cambie su contraseña

    @GET("auth/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserResponse

    @GET("auth/users/paginated")
    suspend fun listAllUsersPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String? = "idUser"
    ): PagedResponse<UserResponse> // Tu backend devuelve 'Page', que mapea a PagedResponse

    @POST("auth/users")
    suspend fun createUser(@Body request: UserCreateRequest): UserResponse

    // @PUT("auth/users/{id}/admin")
    // suspend fun updateUsersByAdmin(...) // Necesitarías el DTO UserUpdateAdminRequest

    @DELETE("auth/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long) // No devuelve contenido
}