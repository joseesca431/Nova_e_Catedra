package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import android.util.Patterns // Import para la validación de email
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.UserUpdateRequest
import com.example.aplicacionjetpack.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val username: String = "",
    val email: String = "",
    val telefono: String = "",
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

    private var originalUsername: String = ""
    private var originalEmail: String = ""
    private var originalTelefono: String = ""

    init {
        loadUserProfile()
    }

    // --- MANEJADORES DE EVENTOS ---
    fun onUsernameChanged(value: String) { uiState = uiState.copy(username = value, error = null) }
    fun onEmailChanged(value: String) { uiState = uiState.copy(email = value, error = null) }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
    fun onTelefonoChanged(value: String) {
        val digits = value.filter { it.isDigit() }
        val formatted = when {
            digits.length > 4 -> "${digits.substring(0, 4)}-${digits.substring(4, digits.length.coerceAtMost(8))}"
            else -> digits
        }
        uiState = uiState.copy(telefono = formatted, error = null)
    }
    fun onNewPasswordChanged(value: String) { uiState = uiState.copy(newPassword = value, error = null) }
    fun onConfirmNewPasswordChanged(value: String) { uiState = uiState.copy(confirmNewPassword = value, error = null) }
    fun onCurrentPasswordChanged(value: String) { uiState = uiState.copy(currentPassword = value, error = null) }

    private fun loadUserProfile() {
        val userId = AuthManager.userId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            userRepository.getUserProfile(userId).onSuccess { user ->
                originalUsername = user.username
                originalEmail = user.email
                originalTelefono = user.telefono ?: ""
                uiState = uiState.copy(
                    isLoading = false,
                    username = user.username,
                    email = user.email,
                    telefono = user.telefono ?: ""
                )
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el perfil.")
            }
        }
    }

    fun updateProfile() {
        val userId = AuthManager.userId ?: run {
            uiState = uiState.copy(error = "Sesión expirada.")
            return
        }
        if (uiState.currentPassword.isBlank()) {
            uiState = uiState.copy(error = "Debes introducir tu contraseña actual para guardar.")
            return
        }

        // VALIDACIONES
        if (uiState.email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState = uiState.copy(error = "El formato del correo electrónico es inválido.")
            return
        }
        if (uiState.telefono.isNotBlank() && !uiState.telefono.matches("^\\d{4}-\\d{4}$".toRegex())) {
            uiState = uiState.copy(error = "El formato del teléfono debe ser 1234-5678.")
            return
        }
        if (uiState.newPassword.isNotEmpty() && uiState.newPassword.length < 6) {
            uiState = uiState.copy(error = "La nueva contraseña debe tener al menos 6 caracteres.")
            return
        }
        if (uiState.newPassword != uiState.confirmNewPassword) {
            uiState = uiState.copy(error = "Las contraseñas nuevas no coinciden.")
            return
        }

        val usernameChanged = uiState.username != originalUsername
        val emailChanged = uiState.email != originalEmail
        val telefonoChanged = uiState.telefono != originalTelefono
        val passwordChanged = uiState.newPassword.isNotBlank()

        if (!usernameChanged && !emailChanged && !telefonoChanged && !passwordChanged) {
            uiState = uiState.copy(error = "No has realizado ningún cambio.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isUpdating = true, error = null)

            val profileRequest = UserUpdateRequest(
                currentPassword = uiState.currentPassword,
                username = uiState.username.takeIf { usernameChanged },
                email = uiState.email.takeIf { emailChanged },
                telefono = uiState.telefono.takeIf { telefonoChanged },
                newPassword = uiState.newPassword.takeIf { passwordChanged }
            )

            userRepository.updateProfile(userId, profileRequest).onSuccess {
                originalUsername = uiState.username
                originalEmail = uiState.email
                originalTelefono = uiState.telefono
                uiState = uiState.copy(isUpdating = false, updateSuccess = true)

            }.onFailure { exception ->
                val errorMessage = if (exception.message?.contains("401") == true) {
                    "La contraseña actual es incorrecta."
                } else {
                    "Error al actualizar perfil: ${exception.message}"
                }
                uiState = uiState.copy(
                    isUpdating = false,
                    error = errorMessage
                )
            }
        }
    }
}
