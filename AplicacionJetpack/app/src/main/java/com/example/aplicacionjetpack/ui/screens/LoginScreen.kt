package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Importar
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // Importar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.ui.viewmodel.LoginUiState // <-- Importar UiState

@Composable
fun LoginScreen(
    navController: NavController,
    uiState: LoginUiState, // <-- Recibe estado
    // --- Recibe eventos ---
    onUsernameChange: (String) -> Unit, // Renombrado
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
    // TODO: Añadir onGoogleClick, onFacebookClick
) {
    // --- Efecto para navegar ---
    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            // Asume que "home" es tu ruta principal de cliente
            navController.navigate("home") { // CAMBIA "home" por tu ruta real (ej: "start")
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Añadido scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(90.dp).padding(bottom = 8.dp)
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
            text = "Nombre de Usuario", // Cambiado de "Correo electrónico"
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.username, // <-- Usa UiState
            onValueChange = onUsernameChange, // <-- Llama evento del VM
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E), unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            isError = uiState.error != null
        )

        // --- Campo de Contraseña ---
        Text(
            text = "Contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.password, // <-- Usa UiState
            onValueChange = onPasswordChange, // <-- Llama evento del VM
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E), unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            isError = uiState.error != null
        )

        // --- Mensaje de Error ---
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(if (uiState.error == null) 32.dp else 0.dp))

        // --- Botón INICIAR SESIÓN ---
        Button(
            onClick = onLoginClick, // <-- Llama evento del VM
            enabled = !uiState.isLoading, // <-- Deshabilitado si carga
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
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
                    navController.navigate("register"){
                        launchSingleTop = true
                        popUpTo("login") { saveState = true }
                        restoreState = true
                    }
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Regístrate", color = Color(0xFF2D1B4E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}