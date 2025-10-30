package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Importar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
// Importaciones para el Calendario
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.ui.viewmodel.RegisterUiState
import java.time.Instant // Importar para el DatePicker
import java.time.ZoneId // Importar para el DatePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    uiState: RegisterUiState,
    // --- Eventos ---
    onPrimerNombreChange: (String) -> Unit,
    onPrimerApellidoChange: (String) -> Unit,
    // onFechaNacimientoChange: (String) -> Unit, // Ya no se usa
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onDuiChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onSegundoNombreChange: (String) -> Unit,
    onSegundoApellidoChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    // --- EVENTOS NUEVOS DEL CALENDARIO ---
    onFechaNacimientoClicked: () -> Unit,
    onCalendarDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit
){
    // --- Efecto para navegar ---
    LaunchedEffect(key1 = uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            // Navega al Home (o 'start') y limpia el historial
            navController.navigate("home") { // Asume "home" como ruta
                popUpTo("register") { inclusive = true }
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // --- Estado para el DatePicker (CORREGIDO) ---
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().minusMillis(568025136000L).toEpochMilli() // Aprox. 18 años
    )
    val showCalendar = uiState.showCalendarDialog
    // --------------------------------

    // --- DIÁLOGO DE CALENDARIO (DatePickerDialog) ---
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
    // --- --------------------------------------- ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(60.dp))

        // --- CORREGIDO: Imagen ---
        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(90.dp)
                .padding(bottom = 8.dp)
        )
        // -----------------------

        Text(
            text = "Únete y empieza tu aventura hoy.",
            fontSize = 22.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // --- Campos del Formulario ---
        FormTextField(label = "Primer Nombre (*)", value = uiState.primerNombre, onValueChange = onPrimerNombreChange, isError = uiState.error?.contains("Nombre") == true)
        FormTextField(label = "Primer Apellido (*)", value = uiState.primerApellido, onValueChange = onPrimerApellidoChange, isError = uiState.error?.contains("Apellido") == true)
        FormTextField(label = "Email (*)", value = uiState.email, onValueChange = onEmailChange, keyboardType = KeyboardType.Email, isError = uiState.error?.contains("Email") == true)
        FormTextField(label = "Nombre de Usuario (*)", value = uiState.username, onValueChange = onUsernameChange, isError = uiState.error?.contains("Usuario") == true)

        // --- CAMPO DE FECHA DE NACIMIENTO (MODIFICADO) ---
        Box(modifier = Modifier.clickable { onFechaNacimientoClicked() }) {
            FormTextField(
                label = "Fecha Nacimiento (*)",
                value = uiState.fechaNacimiento,
                onValueChange = {}, // No editable
                readOnly = true,
                enabled = false, // Deshabilitado
                trailingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendario") },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = if (uiState.error?.contains("Fecha") == true) Color.Red else Color.LightGray,
                    disabledTextColor = if (uiState.error?.contains("Fecha") == true) Color.Red else Color.Black,
                    disabledLabelColor = if (uiState.error?.contains("Fecha") == true) Color.Red else Color.Black,
                    disabledTrailingIconColor = if (uiState.error?.contains("Fecha") == true) Color.Red else Color.Black,
                    disabledContainerColor = Color.White
                )
            )
        }
        // --------------------------------------------

        FormTextField(label = "Contraseña (min 6 char) (*)", value = uiState.password, onValueChange = onPasswordChange, keyboardType = KeyboardType.Password, isError = uiState.error?.contains("Contraseña") == true)
        FormTextField(label = "Confirmar Contraseña (*)", value = uiState.confirmPassword, onValueChange = onConfirmPasswordChange, keyboardType = KeyboardType.Password, isError = uiState.error?.contains("Contraseña") == true)

        // --- Campos Opcionales ---
        FormTextField(label = "Segundo Nombre (Opcional)", value = uiState.segundoNombre, onValueChange = onSegundoNombreChange)
        FormTextField(label = "Segundo Apellido (Opcional)", value = uiState.segundoApellido, onValueChange = onSegundoApellidoChange)
        FormTextField(label = "Teléfono (Opcional)", value = uiState.telefono, onValueChange = onTelefonoChange, keyboardType = KeyboardType.Phone)
        FormTextField(label = "DUI (Opcional)", value = uiState.dui, onValueChange = onDuiChange, keyboardType = KeyboardType.Number)
        FormTextField(label = "Dirección (Opcional)", value = uiState.direccion, onValueChange = onDireccionChange, singleLine = false, modifier = Modifier.height(100.dp))

        // --- Mensaje de Error (CORREGIDO) ---
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(if (uiState.error == null) 16.dp else 8.dp))
        // ---------------------------------

        // --- Botón REGISTRARSE (CORREGIDO) ---
        Button(
            onClick = onRegisterClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
            shape = RoundedCornerShape(8.dp)
        ){
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
            } else {
                Text("REGISTRARSE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        // ---------------------------------

        Spacer(modifier = Modifier.height(24.dp))

        // --- Texto de login (CORREGIDO) ---
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
        // ------------------------------
    } // Fin Column
} // Fin RegisterScreen

// --- FormTextField (CORREGIDO) ---
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
    // Define colores por defecto
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (isError) Color.Red else Color(0xFF2D1B4E),
        unfocusedBorderColor = if (isError) Color.Red else Color.LightGray,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        errorBorderColor = Color.Red,
        // Añade colores para deshabilitado (para el campo de fecha)
        disabledBorderColor = if (isError) Color.Red else Color.LightGray,
        disabledTextColor = if (isError) Color.Red else Color.Black,
        disabledLabelColor = if (isError) Color.Red else Color.Black,
        disabledTrailingIconColor = if (isError) Color.Red else Color.Black,
        disabledContainerColor = Color.White
    )
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = colors, // Usa el parámetro de colores
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            readOnly = readOnly,
            enabled = enabled,
            trailingIcon = trailingIcon
        )
    }
}
// --- -------------------- ---