package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PEDIDO",
                        fontSize = 18.sp,
                        color = Color(0xFF2D1B4E),
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nombre
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nombre",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "XXXXXXX-XXXX-XXXX-XXXXXX",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            // Campo Correo
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "xxxxxx@gmail.com",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            // Campo Teléfono
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Teléfono",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "+503 0000-0000",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            // Campo Dirección
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Dirección",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Departamento XXXXXX",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sección Productos
            Text(
                text = "Productos:",
                fontSize = 16.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.SemiBold
            )




            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("start") {
                        popUpTo("login") { inclusive = true } // Elimina Login del historial
                        launchSingleTop = true // Evita múltiples copias de la misma pantalla
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}