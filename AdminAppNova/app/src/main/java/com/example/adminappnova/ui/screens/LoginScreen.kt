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

@Composable
fun LoginScreen(navController: NavController){
    var usuario by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}

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
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
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
            value = password,
            onValueChange = { password = it },
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
            shape = RoundedCornerShape(8.dp)
        )
        // Botón INICIAR SESIÓN
        Button(
            onClick = {
                // NAVEGAR A LA PANTALLA WELCOME
                navController.navigate("start") {
                    launchSingleTop = true
                    popUpTo("login") { saveState = true }
                    restoreState = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D1B4E)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "INICIAR SESIÓN",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}