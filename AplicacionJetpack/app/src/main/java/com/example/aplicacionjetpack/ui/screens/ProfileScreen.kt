package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R

@Composable
fun ProfileScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Perfil") }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                navController = navController,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título "MI CUENTA"
            Text(
                text = "MI CUENTA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeAccent,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Tarjeta de perfil
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PurpleDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícono de perfil circular
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(40.dp),
                            tint = PurpleDark
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Información del usuario
                    Column {
                        Text(
                            text = "Nombre del usuario",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Apellido",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "correo@gmail.com",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Botón "Editar perfil"
            Button(
                onClick = { /* Acción editar perfil */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Editar perfil",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón "Historial de compras"
            Button(
                onClick = { /* Acción historial */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Historial de compras",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón "Cerrar sesión"
            TextButton(
                onClick = {
                    // Navegar de regreso al login
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = "Cerrar sesión",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}