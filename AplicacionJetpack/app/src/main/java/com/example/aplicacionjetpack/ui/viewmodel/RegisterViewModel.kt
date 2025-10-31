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
import com.example.aplicacionjetpack.data.dto.RegisterRequest
import com.example.aplicacionjetpack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class RegisterUiState(
    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val email: String = "",
    val username: String = "",
    val fechaNacimiento: String = "", // "dd/MM/yyyy"
    val password: String = "",
    val confirmPassword: String = "",
    val telefono: String = "",
    val dui: String = "",
    val direccion: String = "",
    // UI state
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val error: String? = null,                // mensaje técnico breve (opcional)
    val validationError: String? = null,      // mensaje amigable resumido para modal
    val validationErrorsList: List<String> = emptyList(), // lista detallada (para debug o campos)
    val showErrorDialog: Boolean = false,     // controla la visibilidad del modal de errores
    val showCalendarDialog: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val TAG = "RegisterVM"

    // Regexes
    private val phoneRegex = Regex("^\\d{4}-\\d{4}$")      // 1234-5678
    private val duiRegex = Regex("^\\d{8}-\\d$")          // 12345678-9
    private val gmailOnlyRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$") // sólo @gmail.com

    // --- Handlers para campos ---
    fun onPrimerNombreChange(value: String) { uiState = uiState.copy(primerNombre = value) }
    fun onSegundoNombreChange(value: String) { uiState = uiState.copy(segundoNombre = value) }
    fun onPrimerApellidoChange(value: String) { uiState = uiState.copy(primerApellido = value) }
    fun onSegundoApellidoChange(value: String) { uiState = uiState.copy(segundoApellido = value) }
    fun onEmailChange(value: String) { uiState = uiState.copy(email = value) }
    fun onUsernameChange(value: String) { uiState = uiState.copy(username = value) }
    fun onPasswordChange(value: String) { uiState = uiState.copy(password = value) }
    fun onConfirmPasswordChange(value: String) { uiState = uiState.copy(confirmPassword = value) }
    fun onDireccionChange(value: String) { uiState = uiState.copy(direccion = value) }

    fun dismissErrorDialog() {
        uiState = uiState.copy(showErrorDialog = false, validationError = null, validationErrorsList = emptyList())
    }

    /**
     * Formatea teléfono como 1234-5678 mientras el usuario escribe.
     */
    fun onTelefonoChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(8)
        val formatted = when {
            digits.length > 4 -> "${digits.substring(0,4)}-${digits.substring(4)}"
            else -> digits
        }
        uiState = uiState.copy(telefono = formatted)
    }

    /**
     * Formatea DUI como 12345678-9 mientras el usuario escribe.
     */
    fun onDuiChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(9)
        val formatted = when {
            digits.length > 8 -> "${digits.substring(0,8)}-${digits.substring(8)}"
            else -> digits
        }
        uiState = uiState.copy(dui = formatted)
    }

    // Calendar dialog controls
    fun onFechaNacimientoClicked() { uiState = uiState.copy(showCalendarDialog = true) }
    fun onCalendarDismiss() { uiState = uiState.copy(showCalendarDialog = false) }

    /**
     * Recibe millis desde el selector de fecha y lo convierte a "dd/MM/yyyy"
     */
    fun onDateSelected(millis: Long?) {
        if (millis == null) return
        try {
            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            uiState = uiState.copy(fechaNacimiento = formattedDate, showCalendarDialog = false)
        } catch (e: Exception) {
            Log.e(TAG, "Error al formatear fecha seleccionada", e)
            showFriendlyValidation(
                listOf("Ocurrió un error al seleccionar la fecha. Intente otra vez."),
                "Error al seleccionar la fecha."
            )
        }
    }

    // centraliza la presentación amigable de errores de validación
    private fun showFriendlyValidation(details: List<String>, shortMessage: String) {
        val politeHeader = "No se pudo continuar con el registro. Por favor revisa lo siguiente:"
        val joined = buildString {
            appendLine(politeHeader)
            details.forEachIndexed { idx, s ->
                appendLine("${idx + 1}. $s")
            }
            appendLine()
            appendLine("Si necesitas ayuda, contacta al soporte.")
        }
        uiState = uiState.copy(
            validationError = shortMessage,
            validationErrorsList = details,
            showErrorDialog = true,
            isLoading = false
        )
        // También dejamos el texto largo (joined) en 'error' para logs/UI opcional
        Log.w(TAG, joined)
    }

    /**
     * Valida todos los campos y muestra un modal con TODOS los fallos para que el usuario sepa exactamente qué corregir.
     */
    fun onRegisterClicked() {
        if (uiState.isLoading) return
        // limpia errores previos (pero no toca los fields)
        uiState = uiState.copy(error = null, validationError = null, validationErrorsList = emptyList(), showErrorDialog = false)

        val errors = mutableListOf<String>()

        // Reglas de validación — recopilamos todos los fallos
        if (uiState.primerNombre.isBlank()) errors.add("Primer nombre: requerido.")
        if (uiState.segundoNombre.isBlank()) errors.add("Segundo nombre: requerido.")
        if (uiState.primerApellido.isBlank()) errors.add("Primer apellido: requerido.")
        if (uiState.segundoApellido.isBlank()) errors.add("Segundo apellido: requerido.")
        if (uiState.email.isBlank()) {
            errors.add("Correo electrónico: requerido.")
        } else if (!gmailOnlyRegex.matches(uiState.email.trim())) {
            errors.add("Correo: debe ser una cuenta Gmail (ej.: usuario@gmail.com).")
        }
        if (uiState.username.isBlank()) errors.add("Nombre de usuario: requerido (5-20 caracteres).")
        if (uiState.fechaNacimiento.isBlank()) errors.add("Fecha de nacimiento: requerida (toca el selector).")
        if (uiState.password.length < 8) errors.add("Contraseña: mínimo 8 caracteres.")
        if (uiState.password != uiState.confirmPassword) errors.add("Confirmación de contraseña: no coincide con la contraseña.")
        if (!phoneRegex.matches(uiState.telefono)) errors.add("Teléfono: formato inválido. Debe ser 1234-5678.")
        if (!duiRegex.matches(uiState.dui)) errors.add("DUI: formato inválido. Debe ser 12345678-9.")
        if (uiState.direccion.isBlank()) errors.add("Dirección: requerida.")

        // si hubo errores, mostramos modal con TODOS ellos
        if (errors.isNotEmpty()) {
            // Mensaje corto para titulo del modal
            val short = "Por favor corrige los campos indicados."
            showFriendlyValidation(errors, short)
            return
        }

        // Si pasa todas las validaciones, ejecuta registro
        executeRegistration()
    }

    private fun executeRegistration() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, validationError = null, error = null, showErrorDialog = false)

            try {
                val request = RegisterRequest(
                    username = uiState.username.trim(),
                    password = uiState.password.trim(),
                    email = uiState.email.trim(),
                    primerNombre = uiState.primerNombre.trim(),
                    segundoNombre = uiState.segundoNombre.trim().ifEmpty { null },
                    primerApellido = uiState.primerApellido.trim(),
                    segundoApellido = uiState.segundoApellido.trim().ifEmpty { null },
                    fechaNacimiento = uiState.fechaNacimiento.trim(), // dd/MM/yyyy
                    telefono = uiState.telefono.trim().ifEmpty { null },
                    dui = uiState.dui.trim().ifEmpty { null },
                    direccion = uiState.direccion.trim().ifEmpty { null }
                )

                Log.d(TAG, "Intentando registrar con datos: $request")

                val result: Result<String> = authRepository.register(request)

                result.onSuccess { tokenOrMessage ->
                    Log.d(TAG, "Registro exitoso, token recibido (o mensaje): $tokenOrMessage")

                    // Guardar token en preferencias y en AuthManager
                    try {
                        tokenManager.saveToken(tokenOrMessage)
                        AuthManager.authToken = tokenOrMessage
                        extractAndStoreUserIdFromJwt(tokenOrMessage)
                    } catch (e: Exception) {
                        Log.w(TAG, "No se pudo guardar el token: ${e.message}")
                    }

                    uiState = uiState.copy(isLoading = false, registerSuccess = true, showErrorDialog = false)
                }.onFailure { ex ->
                    Log.e(TAG, "Registro fallido", ex)
                    val message = friendlyErrorFromException(ex)
                    // Mostrar mensaje amable en modal con detalle técnico opcional
                    showFriendlyValidation(listOf(message), "No se pudo completar el registro.")
                    uiState = uiState.copy(error = message, isLoading = false)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Excepción en executeRegistration", e)
                showFriendlyValidation(listOf("Error inesperado al intentar registrar. Intente de nuevo más tarde."), "Error inesperado.")
            }
        }
    }

    private fun friendlyErrorFromException(ex: Throwable): String {
        if (ex is HttpException) {
            try {
                val errorBody = ex.response()?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    try {
                        val json = JSONObject(errorBody)
                        val msg = json.optString("message")
                        if (msg.isNotBlank()) return msg
                    } catch (_: Exception) { /* no JSON */ }
                    return errorBody
                }
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo parsear body de HttpException", e)
            }
            val code = try { ex.code() } catch (_: Exception) { -1 }
            return when (code) {
                400 -> "Solicitud inválida (verifica los datos)."
                401 -> "No autorizado."
                409 -> "El nombre de usuario o el correo ya están en uso."
                else -> "Error HTTP $code"
            }
        }
        val msg = ex.message ?: "Error desconocido"
        return when {
            msg.contains("timeout", ignoreCase = true) -> "Tiempo de conexión agotado."
            msg.contains("Failed to connect", ignoreCase = true) -> "No se pudo conectar al servidor."
            else -> msg
        }
    }

    private fun extractAndStoreUserIdFromJwt(jwt: String) {
        try {
            val parts = jwt.split(".")
            if (parts.size < 2) return
            val payloadB64 = parts[1]
            val decoded = Base64.decode(payloadB64, Base64.URL_SAFE or Base64.NO_WRAP)
            val payloadJson = String(decoded, Charsets.UTF_8)
            val json = JSONObject(payloadJson)
            val idLong = when {
                json.has("userId") -> json.optLong("userId", -1L)
                json.has("user_id") -> json.optLong("user_id", -1L)
                json.has("id") -> json.optLong("id", -1L)
                else -> -1L
            }
            if (idLong > 0) {
                AuthManager.userId = idLong
                Log.d(TAG, "userId extraído del JWT: $idLong")
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo extraer userId del JWT: ${e.message}")
        }
    }
}
