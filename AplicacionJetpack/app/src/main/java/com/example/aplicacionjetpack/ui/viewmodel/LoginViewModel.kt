package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

// Estado de la UI para LoginScreen
data class LoginUiState(
    val username: String = "", // El backend usa 'username'
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set
    private val TAG = "LoginVM"

    fun onUsernameChange(username: String) { // Renombrado de onEmailChange
        uiState = uiState.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun onLoginClicked() {
        if (uiState.isLoading) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val usernameLimpio = uiState.username.trim()
            val passwordLimpio = uiState.password.trim()

            Log.d(TAG, "Intentando login con usuario: $usernameLimpio")
            val result: Result<String> = authRepository.login(usernameLimpio, passwordLimpio)

            result.onSuccess { token ->
                Log.d(TAG, "Login Exitoso en VM. Token guardado.")
                AuthManager.authToken = token // Guarda el token
                // TODO: Decodificar token para obtener ID de usuario y rol si es necesario
                // AuthManager.userId = ...
                uiState = uiState.copy(isLoading = false, loginSuccess = true)
            }.onFailure { exception ->
                Log.e(TAG, "Login Fallido en VM", exception)
                uiState = uiState.copy(isLoading = false, error = "Usuario o contraseña incorrectos.")
            }
        }
    }
    // fun onGoogleLoginClicked() { /* TODO */ }
    // fun onFacebookLoginClicked() { /* TODO */ }
}