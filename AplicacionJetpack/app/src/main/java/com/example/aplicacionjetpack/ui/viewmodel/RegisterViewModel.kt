package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.RegisterRequest
import com.example.aplicacionjetpack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
// --- 👇 IMPORTACIONES DE FECHA (java.time) 👇 ---
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
// ------------------------------------------
import javax.inject.Inject
import kotlin.Result

// --- Data Class para el Estado de la UI (ACTUALIZADA) ---
data class RegisterUiState(
    val primerNombre: String = "",
    val primerApellido: String = "",
    val fechaNacimiento: String = "", // Se guarda como String "dd/MM/yyyy"
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Campos opcionales
    val telefono: String = "",
    val dui: String = "",
    val direccion: String = "",
    val segundoNombre: String = "",
    val segundoApellido: String = "",
    // ---
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val error: String? = null,
    // --- 👇 AÑADIDO PARA EL CALENDARIO 👇 ---
    val showCalendarDialog: Boolean = false
    // ------------------------------------
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val TAG = "RegisterVM"

    // --- Funciones de Evento (sin cambios) ---
    fun onPrimerNombreChange(value: String) { uiState = uiState.copy(primerNombre = value, error = null) }
    fun onPrimerApellidoChange(value: String) { uiState = uiState.copy(primerApellido = value, error = null) }
    fun onEmailChange(value: String) { uiState = uiState.copy(email = value, error = null) }
    fun onUsernameChange(value: String) { uiState = uiState.copy(username = value, error = null) }
    fun onPasswordChange(value: String) { uiState = uiState.copy(password = value, error = null) }
    fun onConfirmPasswordChange(value: String) { uiState = uiState.copy(confirmPassword = value, error = null) }
    fun onDireccionChange(value: String) { uiState = uiState.copy(direccion = value, error = null) }
    fun onSegundoNombreChange(value: String) { uiState = uiState.copy(segundoNombre = value) }
    fun onSegundoApellidoChange(value: String) { uiState = uiState.copy(segundoApellido = value) }

    fun onTelefonoChange(value: String) {
        val digits = value.filter { it.isDigit() }
        val formatted = when {
            digits.length > 4 -> "${digits.substring(0, 4)}-${digits.substring(4, digits.length.coerceAtMost(8))}"
            else -> digits
        }
        uiState = uiState.copy(telefono = formatted, error = null)
    }

    fun onDuiChange(value: String) {
        val digits = value.filter { it.isDigit() }
        val formatted = when {
            digits.length == 9 -> "${digits.substring(0, 8)}-${digits.substring(8)}"
            else -> digits.take(9)
        }
        uiState = uiState.copy(dui = formatted, error = null)
    }

    // --- 👇 FUNCIONES NUEVAS PARA EL CALENDARIO 👇 ---
    fun onFechaNacimientoClicked() {
        uiState = uiState.copy(showCalendarDialog = true)
    }

    fun onCalendarDismiss() {
        uiState = uiState.copy(showCalendarDialog = false)
    }

    fun onDateSelected(millis: Long?) {
        if (millis == null) return
        try {
            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            uiState = uiState.copy(fechaNacimiento = formattedDate, error = null)
        } catch (e: Exception) {
            Log.e(TAG, "Error al formatear fecha seleccionada", e)
            uiState = uiState.copy(error = "Error al seleccionar la fecha.")
        }
    }
    // --- --------------------------------------- ---


    fun onRegisterClicked() {
        if (uiState.isLoading) return

        // --- 1. Validación de UI más estricta ---
        if (uiState.primerNombre.isBlank()) {
            uiState = uiState.copy(error = "El primer nombre es obligatorio.")
            return
        }
        if (uiState.primerApellido.isBlank()) {
            uiState = uiState.copy(error = "El primer apellido es obligatorio.")
            return
        }
        if (uiState.email.isBlank()) {
            uiState = uiState.copy(error = "El correo electrónico es obligatorio.")
            return
        }
        if (uiState.username.isBlank()) {
            uiState = uiState.copy(error = "El nombre de usuario es obligatorio.")
            return
        }
        if (uiState.fechaNacimiento.isBlank()) {
            uiState = uiState.copy(error = "La fecha de nacimiento es obligatoria.")
            return
        }
        if (uiState.password.length < 8) {
            uiState = uiState.copy(error = "La contraseña debe tener al menos 8 caracteres.")
            return
        }
        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(error = "Las contraseñas no coinciden.")
            return
        }
        if (uiState.telefono.isBlank() || !uiState.telefono.matches("^\\d{4}-\\d{4}$".toRegex())) {
            uiState = uiState.copy(error = "El teléfono es obligatorio y debe tener el formato 1234-5678.")
            return
        }
        if (uiState.dui.isBlank() || !uiState.dui.matches("^\\d{8}-\\d$".toRegex())) {
            uiState = uiState.copy(error = "El DUI es obligatorio y debe tener el formato 12345678-9.")
            return
        }
        if (uiState.direccion.isBlank()) {
            uiState = uiState.copy(error = "La dirección es obligatoria.")
            return
        }
        // Validación de campos opcionales no es necesaria si son realmente opcionales

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val request = RegisterRequest(
                primerNombre = uiState.primerNombre.trim(),
                segundoNombre = uiState.segundoNombre.trim().takeIf { it.isNotEmpty() },
                primerApellido = uiState.primerApellido.trim(),
                segundoApellido = uiState.segundoApellido.trim().takeIf { it.isNotEmpty() },
                fechaNacimiento = uiState.fechaNacimiento.trim(),
                email = uiState.email.trim(),
                username = uiState.username.trim(),
                password = uiState.password.trim(),
                telefono = uiState.telefono.trim(),
                dui = uiState.dui.trim(),
                direccion = uiState.direccion.trim(),
                roleName = "ROLE_USER"
            )
            Log.d(TAG, "Intentando registrar: $request")

            val result: Result<String> = authRepository.register(request)

            result.onSuccess { token ->
                Log.d(TAG, "Registro Exitoso. Token guardado.")
                AuthManager.authToken = token
                uiState = uiState.copy(isLoading = false, registerSuccess = true)
            }.onFailure { exception ->
                Log.e(TAG, "Registro Fallido", exception)
                // Aquí podrías parsear la respuesta de error del servidor si quisieras
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error: El usuario o correo ya existen."
                )
            }
        }
    }
}