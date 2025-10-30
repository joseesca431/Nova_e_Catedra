package com.example.aplicacionjetpack.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.UserResponse
import com.example.aplicacionjetpack.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- ESTADO PARA LA PANTALLA DE EDICIÓN (ahora con campos de contraseña) ---
data class UserUiState(
    val user: UserResponse? = null,
    val username: String = "",
    val email: String = "",
    val telefono: String = "",

    // Campos para manejo de contraseñas en la UI
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val currentPassword: String = "",

    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(UserUiState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = AuthManager.userId ?: run {
            uiState = uiState.copy(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            userRepository.getUserProfile(userId).onSuccess { user ->
                uiState = uiState.copy(
                    isLoading = false,
                    user = user,
                    username = user.username,
                    email = user.email,
                    telefono = user.telefono ?: ""
                )
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el perfil.")
            }
        }
    }

    // --- EVENTOS DE CAMBIO (incluye contraseñas) ---
    fun onUsernameChanged(newUsername: String) {
        uiState = uiState.copy(username = newUsername, error = null)
    }

    fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail, error = null)
    }

    fun onTelefonoChanged(newTelefono: String) {
        uiState = uiState.copy(telefono = newTelefono, error = null)
    }

    fun onNewPasswordChanged(value: String) {
        uiState = uiState.copy(newPassword = value, error = null)
    }

    fun onConfirmNewPasswordChanged(value: String) {
        uiState = uiState.copy(confirmNewPassword = value, error = null)
    }

    fun onCurrentPasswordChanged(value: String) {
        uiState = uiState.copy(currentPassword = value, error = null)
    }

    // --- LÓGICA DE ACTUALIZACIÓN ---
    fun updateProfile() {
        // Validaciones UI básicas
        if (uiState.currentPassword.isBlank()) {
            uiState = uiState.copy(error = "Introduce tu contraseña actual para confirmar los cambios.")
            return
        }

        if (uiState.newPassword.isNotBlank()) {
            if (uiState.newPassword != uiState.confirmNewPassword) {
                uiState = uiState.copy(error = "La nueva contraseña y su confirmación no coinciden.")
                return
            }
            if (uiState.newPassword.length < 6) {
                uiState = uiState.copy(error = "La contraseña debe tener al menos 6 caracteres.")
                return
            }
        }

        val userId = AuthManager.userId
        if (userId == null) {
            uiState = uiState.copy(error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isUpdating = true, error = null)

            try {
                // ----- AQUÍ puedes llamar a tu repositorio para actualizar realmente -----
                // Ejemplo (si tienes un DTO UpdateUserRequest):
                // val request = UpdateUserRequest(
                //     username = uiState.username,
                //     email = uiState.email,
                //     telefono = uiState.telefono,
                //     currentPassword = uiState.currentPassword,
                //     newPassword = if (uiState.newPassword.isBlank()) null else uiState.newPassword
                // )
                // val result = userRepository.updateProfile(userId, request)
                // result.onSuccess { ... }.onFailure { ... }

                // Como no conocemos tu endpoint exacto, simulamos la llamada:
                delay(900)

                // Si usas la API real, reemplaza lo anterior por la llamada a userRepository
                // y maneja onSuccess/onFailure según tu API.

                // Simulamos éxito:
                uiState = uiState.copy(isUpdating = false, updateSuccess = true, error = null)

            } catch (e: Exception) {
                uiState = uiState.copy(isUpdating = false, error = "Error al actualizar el perfil.")
            }
        }
    }
}
