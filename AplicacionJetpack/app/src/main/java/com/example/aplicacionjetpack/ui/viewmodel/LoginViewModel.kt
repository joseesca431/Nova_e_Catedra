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
import com.example.aplicacionjetpack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import javax.inject.Inject
import kotlin.Result

data class LoginUiState(
    val username: String = "",
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

    fun onUsernameChange(username: String) {
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
                if (token.isBlank()) {
                    Log.e(TAG, "Login exitoso pero token vacío")
                    uiState = uiState.copy(isLoading = false, error = "Token inválido recibido.")
                    return@onSuccess
                }

                val jwt = if (token.startsWith("Bearer ", ignoreCase = true)) {
                    token.removePrefix("Bearer ").trim()
                } else token.trim()

                // 1. Guardar en TokenManager (SharedPreferences)
                tokenManager.saveToken(jwt)

                // 2. Decodificar el token para obtener el userId
                val userId = getUserIdFromJwt(jwt)

                // --- 👇👇👇 ¡LA CORRECCIÓN DEFINITIVA ESTÁ AQUÍ! 👇👇👇 ---
                if (userId == null) {
                    Log.e(TAG, "¡Login exitoso pero no se pudo extraer userId del token JWT!")
                    uiState = uiState.copy(isLoading = false, error = "Error al procesar los datos de usuario.")
                    // Limpiamos el token guardado para evitar un estado inconsistente
                    tokenManager.clearToken()
                    return@onSuccess // ¡NO CONTINUAR SI NO TENEMOS userId!
                }

                // 3. Guardar AMBOS en AuthManager (en memoria)
                // Ahora estamos seguros de que userId no es nulo.
                AuthManager.authToken = jwt
                AuthManager.userId = userId

                Log.d(TAG, "Login Exitoso en VM. Token guardado. UserId=$userId.")
                uiState = uiState.copy(isLoading = false, loginSuccess = true)
                // --- -------------------------------------------------------- ---

            }.onFailure { exception ->
                Log.e(TAG, "Login Fallido en VM", exception)
                uiState = uiState.copy(isLoading = false, error = "Usuario o contraseña incorrectos.")
            }
        }
    }

    private fun getUserIdFromJwt(token: String): Long? {
        try {
            val parts = token.split(".")
            if (parts.size < 2) {
                Log.e(TAG, "Token JWT inválido, no tiene payload.")
                return null
            }

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedJson = String(decodedBytes, Charset.defaultCharset())
            val json = JSONObject(decodedJson)

            return when {
                json.has("userId") -> json.getLong("userId")
                json.has("sub") -> json.getString("sub").toLongOrNull()
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
}
