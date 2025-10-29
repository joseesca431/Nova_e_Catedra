package com.example.adminappnova.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R
import com.example.adminappnova.ui.viewmodel.LoginUiState // 👈 Importa tu data class de UiState

@Composable
fun LoginScreen(
    navController: NavController,
    uiState: LoginUiState, // 👈 Recibe el estado
    onUsuarioChange: (String) -> Unit, // 👈 Recibe los eventos
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
){
    // Ya no necesitas: var usuario by remember ...

    // Efecto para navegar CUANDO el login es exitoso
    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate("start") {
                launchSingleTop = true
                popUpTo("login") { inclusive = true } // Limpia el historial de login
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
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
        // Texto descriptivo
        Text(
            text = "ADMINISTRACIÓN",
            fontSize = 22.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // Campo de correo electrónico
        Text(
            text = "Usuario",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.usuario, // 👈 Usa el estado del VM
            onValueChange = onUsuarioChange, // 👈 Llama al evento del VM
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        //Campo de contraseña
        Text(
            text = "Contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.password, // 👈 Usa el estado del VM
            onValueChange = onPasswordChange, // 👈 Llama al evento del VM
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Botón INICIAR SESIÓN
        Button(
            onClick = onLoginClick, // 👈 Llama al evento del VM
            enabled = !uiState.isLoading, // 👈 Se deshabilita mientras carga
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D1B4E)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                // Muestra un indicador de carga
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                // Muestra el texto
                Text(
                    text = "INICIAR SESIÓN",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Muestra un error si existe
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}