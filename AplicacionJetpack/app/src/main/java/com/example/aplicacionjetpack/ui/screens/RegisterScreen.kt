package com.example.aplicacionjetpack.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.ui.viewmodel.RegisterUiState
import java.time.Instant
import java.time.ZoneId
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    uiState: RegisterUiState,
    // --- Eventos ---
    onPrimerNombreChange: (String) -> Unit,
    onSegundoNombreChange: (String) -> Unit,
    onPrimerApellidoChange: (String) -> Unit,
    onSegundoApellidoChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onDuiChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    // --- EVENTOS DEL CALENDARIO ---
    onFechaNacimientoClicked: () -> Unit,
    onCalendarDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    // --- Nuevo: callback para cerrar modal de errores ---
    onDismissErrorDialog: () -> Unit
) {
    // Navegar al home cuando el registro es exitoso
    LaunchedEffect(key1 = uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // DatePicker state con valor por defecto (aprox. 18 años atrás)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().minusMillis(568025136000L).toEpochMilli()
    )
    val showCalendar = uiState.showCalendarDialog

    if (showCalendar) {
        DatePickerDialog(
            onDismissRequest = onCalendarDismiss,
            confirmButton = {
                Button(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onCalendarDismiss()
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = onCalendarDismiss) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Helpers para errores por campo (busca coincidencias en la lista de validaciones)
    fun fieldHasError(keyword: String): Boolean =
        uiState.validationErrorsList.any { it.contains(keyword, ignoreCase = true) }

    fun fieldErrorText(keyword: String): String? =
        uiState.validationErrorsList.firstOrNull { it.contains(keyword, ignoreCase = true) }

    // Toggles para mostrar/ocultar contraseñas
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp)
        )

        Text(
            text = "Únete y empieza tu aventura hoy.",
            fontSize = 20.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 14.dp)
        )

        // ======= REORDENADO SEGÚN TU PETICIÓN =======
        // Grupo: Primer Nombre + Segundo Nombre
        FormTextField(
            label = "Primer Nombre (*)",
            value = uiState.primerNombre,
            onValueChange = onPrimerNombreChange,
            isError = fieldHasError("primer nombre") || fieldHasError("nombre"),
            errorText = fieldErrorText("primer nombre") ?: fieldErrorText("nombre")
        )

        FormTextField(
            label = "Segundo Nombre (*)",
            value = uiState.segundoNombre,
            onValueChange = onSegundoNombreChange,
            isError = fieldHasError("segundo nombre"),
            errorText = fieldErrorText("segundo nombre")
        )

        // Grupo: Primer Apellido + Segundo Apellido
        FormTextField(
            label = "Primer Apellido (*)",
            value = uiState.primerApellido,
            onValueChange = onPrimerApellidoChange,
            isError = fieldHasError("apellido"),
            errorText = fieldErrorText("apellido")
        )

        FormTextField(
            label = "Segundo Apellido (*)",
            value = uiState.segundoApellido,
            onValueChange = onSegundoApellidoChange,
            isError = fieldHasError("segundo apellido"),
            errorText = fieldErrorText("segundo apellido")
        )
        // =================================================

        FormTextField(
            label = "Email (*)",
            value = uiState.email,
            onValueChange = onEmailChange,
            keyboardType = KeyboardType.Email,
            isError = fieldHasError("correo") || fieldHasError("email"),
            errorText = fieldErrorText("correo") ?: fieldErrorText("email")
        )

        FormTextField(
            label = "Nombre de Usuario (*)",
            value = uiState.username,
            onValueChange = onUsernameChange,
            isError = fieldHasError("usuario") || fieldHasError("nombre de usuario"),
            errorText = fieldErrorText("usuario") ?: fieldErrorText("nombre de usuario")
        )

        // Fecha de nacimiento: campo no editable que abre el DatePicker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onFechaNacimientoClicked() }
        ) {
            FormTextField(
                label = "Fecha Nacimiento (*)",
                value = uiState.fechaNacimiento,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                trailingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendario") },
                isError = fieldHasError("fecha")
            )
        }
        fieldErrorText("fecha")?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        // Contraseña
        FormTextField(
            label = "Contraseña (mín 8 caracteres) (*)",
            value = uiState.password,
            onValueChange = onPasswordChange,
            keyboardType = KeyboardType.Password,
            isError = fieldHasError("contraseña"),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
        )
        fieldErrorText("contraseña")?.let { msg ->
            Text(text = msg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }

        // Confirmar contraseña
        FormTextField(
            label = "Confirmar Contraseña (*)",
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            keyboardType = KeyboardType.Password,
            isError = fieldHasError("confirmación") || fieldHasError("contraseña"),
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showConfirmPassword) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        // Opcionales validados
        FormTextField(
            label = "Teléfono (Opcional) - formato 1234-5678",
            value = uiState.telefono,
            onValueChange = onTelefonoChange,
            keyboardType = KeyboardType.Phone,
            isError = fieldHasError("teléfono") || fieldHasError("telefono"),
            errorText = fieldErrorText("teléfono") ?: fieldErrorText("telefono")
        )

        FormTextField(
            label = "DUI (Opcional) - formato 12345678-9",
            value = uiState.dui,
            onValueChange = onDuiChange,
            keyboardType = KeyboardType.Number,
            isError = fieldHasError("dui"),
            errorText = fieldErrorText("dui")
        )

        FormTextField(
            label = "Dirección (Opcional)",
            value = uiState.direccion,
            onValueChange = onDireccionChange,
            singleLine = false,
            modifier = Modifier.height(100.dp),
            isError = fieldHasError("dirección") || fieldHasError("direccion"),
            errorText = fieldErrorText("dirección") ?: fieldErrorText("direccion")
        )

        // Mensaje central breve (amable o técnico)
        val centralMessage = uiState.validationError ?: uiState.error
        centralMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Botón Registrar
        Button(
            onClick = onRegisterClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 3.dp)
            } else {
                Text("REGISTRARSE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(
                onClick = { navController.navigate("login") { popUpTo("register") { inclusive = true } } },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Inicia sesión", color = Color(0xFF2D1B4E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // --- Modal amable con TODOS los errores (detallados) ---
    if (uiState.showErrorDialog) {
        val title = uiState.validationError ?: "Hay algunos problemas"
        val details = if (uiState.validationErrorsList.isNotEmpty()) {
            uiState.validationErrorsList.joinToString("\n") { "• $it" }
        } else {
            uiState.error ?: "Ocurrió un problema. Intenta de nuevo."
        }

        AlertDialog(
            onDismissRequest = { onDismissErrorDialog() },
            title = { Text(text = title, fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Text(text = details, fontSize = 14.sp, maxLines = 12, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Si necesitas ayuda, contacta al soporte.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { onDismissErrorDialog() }) {
                    Text("Entendido")
                }
            }
        )
    }

    // --- Modal de éxito ---
    if (uiState.registerSuccess) {
        AlertDialog(
            onDismissRequest = { /* la navegación ya ocurre en LaunchedEffect */ },
            title = { Text("Registro exitoso", fontWeight = FontWeight.SemiBold) },
            text = { Text("¡Bienvenido! Tu cuenta fue creada correctamente. Se te redirigirá ahora.") },
            confirmButton = {
                TextButton(onClick = { /* navegación manejada arriba */ }) {
                    Text("Continuar")
                }
            }
        )
    }
}

/**
 * Composable reutilizable para campos con soporte de error inline y trailing icon opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorText: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // Este Text actúa como un Label externo, siempre visible y negro.
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            readOnly = readOnly,
            enabled = enabled,
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                // Colores para el TEXTO DENTRO del campo
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black, // ¡TEXTO NEGRO INCLUSO DESHABILITADO!

                // Colores para los BORDES
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF2D1B4E),
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.LightGray,
                disabledBorderColor = Color.LightGray, // Borde gris claro cuando está deshabilitado

                // Color para el CURSOR
                cursorColor = Color(0xFF2D1B4E),

                // Colores para los ÍCONOS
                focusedTrailingIconColor = Color(0xFF2D1B4E),
                unfocusedTrailingIconColor = Color.Black.copy(alpha = 0.7f),
                disabledTrailingIconColor = Color.Black.copy(alpha = 0.7f) // Ícono visible cuando está deshabilitado

                // --- 👇👇👇 ¡¡¡LOS PARÁMETROS INVENTADOS HAN SIDO ANIQUILADOS!!! 👇👇👇 ---
                // NO EXISTEN: placeholderColor, disabledPlaceholderColor
                // --- ------------------------------------------------------------------ ---
            ),
            placeholder = {
                Text(
                    text = if (value.isEmpty() && label != "Fecha Nacimiento (*)") "Escribe aquí..." else "",
                    color = Color.Gray, // <-- El color del placeholder se define aquí.
                    fontSize = 14.sp
                )
            }
        )

        // Muestra el texto de error debajo si existe
        if (!errorText.isNullOrBlank()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewRegisterScreen() {
    // Preview: crear NavController con contexto válido para evitar NullPointerException
    val ctx = LocalContext.current
    val previewNav = remember { NavHostController(ctx) }

    val dummyState = RegisterUiState(
        primerNombre = "",
        segundoNombre = "",
        primerApellido = "",
        segundoApellido = "",
        email = "",
        username = "",
        fechaNacimiento = "",
        password = "",
        confirmPassword = "",
        telefono = "",
        dui = "",
        direccion = "",
        isLoading = false,
        registerSuccess = false,
        error = null,
        validationError = null,
        validationErrorsList = emptyList(),
        showErrorDialog = false,
        showCalendarDialog = false
    )

    RegisterScreen(
        navController = previewNav,
        uiState = dummyState,
        onPrimerNombreChange = {},
        onSegundoNombreChange = {},
        onPrimerApellidoChange = {},
        onSegundoApellidoChange = {},
        onEmailChange = {},
        onUsernameChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onTelefonoChange = {},
        onDuiChange = {},
        onDireccionChange = {},
        onRegisterClick = {},
        onFechaNacimientoClicked = {},
        onCalendarDismiss = {},
        onDateSelected = {},
        onDismissErrorDialog = {}
    )
}
