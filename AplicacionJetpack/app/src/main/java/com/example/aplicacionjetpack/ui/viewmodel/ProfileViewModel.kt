package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.TokenManager
import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import com.example.aplicacionjetpack.data.repository.NotificacionRepository
import com.example.aplicacionjetpack.data.repository.UserRepository // Importante
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import javax.inject.Inject

data class ProfileUiState(
    // Tu DTO UserResponse no tiene primerNombre/Apellido, usamos los datos que sí tiene
    val username: String = "",
    val email: String = "",
    val notificaciones: List<NotificacionResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository, // Usamos la Interfaz
    private val notificacionRepository: NotificacionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val userId = AuthManager.userId ?: run {
            uiState = uiState.copy(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            // --- 👇👇👇 ¡¡¡LA LÓGICA CORREGIDA QUE USA LA FUNCIÓN REAL!!! 👇👇👇 ---
            // Ahora llamamos a la función que SÍ existe en el repositorio
            val userResult = userRepository.getUserProfile(userId)
            val notificacionesResult = notificacionRepository.getNotificaciones()

            userResult.onSuccess { user ->
                // Usamos los campos que SÍ existen en tu UserResponse.kt (username, email)
                uiState = uiState.copy(username = user.username, email = user.email)
            }.onFailure {
                uiState = uiState.copy(error = "No se pudo cargar el perfil.")
            }

            notificacionesResult.onSuccess { notificaciones ->
                uiState = uiState.copy(notificaciones = notificaciones)
            }.onFailure {
                if (uiState.error == null) {
                    uiState = uiState.copy(error = "No se pudieron cargar las notificaciones.")
                }
            }

            uiState = uiState.copy(isLoading = false)
            // --- ---------------------------------------------------------------- ---
        }
    }

    fun logout() {
        AuthManager.authToken = null
        AuthManager.userId = null
        tokenManager.clearToken()
    }
}
