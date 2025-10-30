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
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- ESTADO PARA LA PANTALLA DE EDICIÓN ---
data class UserUiState(
    val user: UserResponse? = null,
    val username: String = "",
    val email: String = "",
    val telefono: String = "",
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
            // Llamamos a la función correcta que ya definimos
            userRepository.getUserProfile(userId).onSuccess { user ->
                uiState = uiState.copy(
                    isLoading = false,
                    user = user,
                    // Poblamos los campos de edición con los datos actuales
                    username = user.username,
                    email = user.email,
                    telefono = user.telefono ?: ""
                )
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el perfil.")
            }
        }
    }

    // --- EVENTOS DE CAMBIO ---
    fun onUsernameChanged(newUsername: String) {
        uiState = uiState.copy(username = newUsername, error = null)
    }

    fun onEmailChanged(newEmail: String) {
        uiState = uiState.copy(email = newEmail, error = null)
    }

    fun onTelefonoChanged(newTelefono: String) {
        uiState = uiState.copy(telefono = newTelefono, error = null)
    }

    // --- LÓGICA DE ACTUALIZACIÓN ---
    fun updateProfile() {
        // TODO: Implementar la llamada real al backend para actualizar.
        // Por ahora, simulamos un éxito para probar la navegación de vuelta.
        viewModelScope.launch {
            uiState = uiState.copy(isUpdating = true)
            // Aquí iría la llamada real al repositorio:
            // val result = userRepository.updateProfile(userId, request)
            // result.onSuccess { ... }
            // Como no tenemos el endpoint, simulamos que funciona.
            kotlinx.coroutines.delay(1000) // Simula una llamada de red
            uiState = uiState.copy(isUpdating = false, updateSuccess = true)
        }
    }
}
