package com.example.adminappnova.ui.viewmodel

import android.util.Log // Importa Log para depuración
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.AuthManager // Importa tu gestor de token
import com.example.adminappnova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI ---
data class LoginUiState(
    val usuario: String = "", // Campo "Usuario" de la UI
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false, // Flag para indicar éxito y permitir navegación
    val error: String? = null // Mensaje de error a mostrar
)

// --- ViewModel ---
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
    // TODO: Considerar inyectar DataStore/SessionManager aquí en lugar de usar AuthManager global
) : ViewModel() {

    // Estado observable por la UI de Compose
    var uiState by mutableStateOf(LoginUiState())
        private set // Solo el ViewModel puede modificar el estado directamente

    // --- Funciones de Evento (llamadas desde la UI) ---

    // Actualiza el estado cuando el campo 'Usuario' cambia
    fun onUsuarioChange(usuario: String) {
        // Actualiza el valor y limpia cualquier error previo
        uiState = uiState.copy(usuario = usuario, error = null)
    }

    // Actualiza el estado cuando el campo 'Password' cambia
    fun onPasswordChange(password: String) {
        // Actualiza el valor y limpia cualquier error previo
        uiState = uiState.copy(password = password, error = null)
    }

    // Se ejecuta cuando se presiona el botón de Iniciar Sesión
    fun onLoginClicked() {
        // Evita múltiples clics si ya está cargando
        if (uiState.isLoading) return

        // Inicia una Coroutine para la llamada de red (no bloquear UI)
        viewModelScope.launch {
            // 1. Mostrar estado de carga en la UI
            uiState = uiState.copy(isLoading = true, error = null) // Limpia error anterior

            // 2. Limpiar espacios en blanco de la entrada del usuario
            val usernameLimpio = uiState.usuario.trim()
            val passwordLimpio = uiState.password.trim()

            // 3. Llamar al repositorio para intentar iniciar sesión
            val result: Result<String> = authRepository.login(
                username = usernameLimpio, // Usa los valores limpios
                password = passwordLimpio
            )

            // 4. Manejar el resultado de la llamada
            result.onSuccess { token ->
                // --- ÉXITO ---
                Log.d("LoginVM", "Login Exitoso en VM. Token recibido.") // Log para depuración
                // Guarda el token JWT (usando el AuthManager temporal)
                AuthManager.authToken = token
                // Actualiza el estado para indicar éxito (esto activará la navegación en la UI)
                uiState = uiState.copy(isLoading = false, loginSuccess = true)

            }.onFailure { exception ->
                // --- ERROR ---
                Log.e("LoginVM", "Login Fallido en VM", exception) // Log del error real
                // Actualiza el estado para mostrar un mensaje de error genérico
                // (Podrías intentar parsear el 'exception' si es HttpException para mostrar el mensaje del backend)
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Usuario o contraseña incorrectos."
                )
            }
        }
    }
}