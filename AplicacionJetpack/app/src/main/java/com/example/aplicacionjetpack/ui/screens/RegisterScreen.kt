package com.example.aplicacionjetpack.ui.screens

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
import com.example.aplicacionjetpack.R

@Composable
fun RegisterScreen(navController: NavController){
    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}
    var confirmPassword by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(80.dp))

        // Imagen NOVA+e
        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(90.dp)
                .padding(bottom = 8.dp)
        )

        //Texto descriptivo
        Text(
            text = "Únete y empieza tu aventura hoy.",
            fontSize = 22.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        //Campo de correo electronico
        Text(
            text = "Correo electrónico",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
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

        // Campo de contraseña
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
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Campo de confirmar contraseña
        Text(
            text = "Confirmar contraseña",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
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
        // Botón REGISTRARSE
        Button(
            onClick = {
                // Aquí iría la lógica de registro
                // Por ahora navegamos al login
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D1B4E)
            ),
            shape = RoundedCornerShape(8.dp)
        ){
            Text(
                text = "REGISTRARSE",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón CONTINUA CON GOOGLE
        OutlinedButton(
            onClick = { /* Acción Google */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(8.dp)
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_google2),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF240052)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "REGISTRATE CON GOOGLE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón CONTINUA CON FACEBOOK
        OutlinedButton(
            onClick = { /* Acción Facebook */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(8.dp)
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_facebook2),
                contentDescription = "Facebook",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF240052)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "REGISTRATE CON FACEBOOK",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Texto de login (en lugar de registro)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            TextButton(
                onClick = {
                    navController.navigate("login") {
                        launchSingleTop = true
                        popUpTo("register") { saveState = true }
                        restoreState = true
                    }
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Inicia sesión",
                    color = Color(0xFF2D1B4E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}