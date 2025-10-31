package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.LoginUiState

@Composable
fun LoginScreen(
    navController: NavController,
    uiState: LoginUiState,
    // --- Recibe eventos ---
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    // Estado para diálogo de validación (modal amable)
    var showValidationDialog by remember { mutableStateOf(false) }
    var validationTitle by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf("") }

    // Estado para mostrar/ocultar contraseña
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Efecto para navegación al iniciar sesión
    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Handler local que valida y llama al onLoginClick sólo si pasa
    fun handleLoginClick() {
        val missing = mutableListOf<String>()
        if (uiState.username.isBlank()) missing.add("Nombre de usuario")
        if (uiState.password.isBlank()) missing.add("Contraseña")

        if (missing.isNotEmpty()) {
            validationTitle = "Faltan datos"
            validationMessage = missing.joinToString("\n") { "• $it" }
            showValidationDialog = true
            return
        }

        // Si pasa validación local, llamamos al evento externo (ViewModel)
        onLoginClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(90.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = "Compras fáciles, confianza total",
            fontSize = 22.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // --- Campo de Usuario (en lugar de Email) ---
        Text(
            text = "Nombre de Usuario",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleDark,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PurpleDark,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black.copy(alpha = 0.6f),
                focusedLabelColor = PurpleDark,
                unfocusedLabelColor = Color.Black.copy(alpha = 0.85f),
                focusedLeadingIconColor = PurpleDark,
                unfocusedLeadingIconColor = Color.Black.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            isError = uiState.error != null,
            placeholder = { Text(text = "usuario", color = Color.Black.copy(alpha = 0.5f)) }
        )

        // --- Campo de Contraseña con eye toggle ---
        Text(
            text = "Contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleDark,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PurpleDark,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black.copy(alpha = 0.6f),
                focusedLabelColor = PurpleDark,
                unfocusedLabelColor = Color.Black.copy(alpha = 0.85f),
                focusedLeadingIconColor = PurpleDark,
                unfocusedLeadingIconColor = Color.Black.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            isError = uiState.error != null,
            placeholder = { Text(text = "••••••••", color = Color.Black.copy(alpha = 0.5f)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = desc, tint = PurpleDark)
                }
            }
        )

        // --- Mensaje de Error (UI state) ---
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(if (uiState.error == null) 32.dp else 8.dp))

        // --- Botón INICIAR SESIÓN ---
        Button(
            onClick = { handleLoginClick() },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleDark),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
            } else {
                Text("INICIAR SESIÓN", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // --- Texto de Registro ---
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(
                onClick = {
                    navController.navigate("register") {
                        launchSingleTop = true
                        popUpTo("login") { saveState = true }
                        restoreState = true
                    }
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Regístrate", color = PurpleDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // --- Modal de validación (amable) ---
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            title = { Text(validationTitle, color = Color.Black, fontWeight = FontWeight.SemiBold) },
            text = { Text(validationMessage, color = Color.Black.copy(alpha = 0.95f)) },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("Entendido", color = PurpleDark, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}
