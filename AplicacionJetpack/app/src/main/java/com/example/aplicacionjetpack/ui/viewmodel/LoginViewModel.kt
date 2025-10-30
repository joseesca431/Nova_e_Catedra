package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Base64 // Importado
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.TokenManager
import com.example.aplicacionjetpack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject // Importado
import java.nio.charset.Charset // Importado
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
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
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
                if (token.isNullOrBlank()) {
                    Log.e(TAG, "Login exitoso pero token vacío")
                    uiState = uiState.copy(isLoading = false, error = "Token inválido recibido.")
                    return@onSuccess
                }

                // Normalizar: almacena el JWT sin prefijo "Bearer "
                val jwt = if (token.startsWith("Bearer ", ignoreCase = true)) {
                    token.removePrefix("Bearer ").trim()
                } else token.trim()

                // 1. Guarda en TokenManager (SharedPreferences)
                tokenManager.saveToken(jwt)

                // 2. Decodifica el token para obtener el userId
                val userId = getUserIdFromJwt(jwt)

                // 3. Guarda AMBOS en AuthManager (en memoria)
                try {
                    AuthManager.authToken = jwt
                    AuthManager.userId = userId // <-- ¡ARREGLO APLICADO AQUÍ!
                } catch (ex: Throwable) {
                    // Si AuthManager no existe o falla, ignoramos
                    Log.d(TAG, "AuthManager no disponible o fallo al asignar: ${ex.message}")
                }

                if(userId == null) {
                    Log.e(TAG, "¡Login exitoso pero no se pudo extraer userId del token JWT!")
                    // Opcional: podrías querer mostrar un error al usuario si esto falla
                }

                Log.d(TAG, "Login Exitoso en VM. Token guardado. UserId=$userId.")
                uiState = uiState.copy(isLoading = false, loginSuccess = true)

            }.onFailure { exception ->
                Log.e(TAG, "Login Fallido en VM", exception)
                uiState = uiState.copy(isLoading = false, error = "Usuario o contraseña incorrectos.")
            }
        }
    }

    /**
     * Decodifica el payload de un token JWT para extraer el ID de usuario.
     * Busca los 'claims' (llaves) "userId" o "sub".
     */
    private fun getUserIdFromJwt(token: String): Long? {
        try {
            val parts = token.split(".")
            if (parts.size < 2) {
                Log.e(TAG, "Token JWT inválido, no tiene 3 partes.")
                return null // Token inválido
            }

            // Obtenemos el payload (la parte del medio)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedJson = String(decodedBytes, Charset.defaultCharset())

            val json = JSONObject(decodedJson)

            // El backend puede usar "userId", "sub" (estándar), "id", etc.
            // Priorizamos "userId", luego "sub".
            return when {
                json.has("userId") -> json.getLong("userId")
                json.has("sub") -> json.getString("sub").toLongOrNull() // 'sub' a veces es String
                else -> {
                    Log.e(TAG, "No se encontró 'userId' o 'sub' en el payload del JWT.")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al decodificar JWT para userId", e)
            return null
        }
    }

    // fun onGoogleLoginClicked() { /* TODO */ }
    // fun onFacebookLoginClicked() { /* TODO */ }
}