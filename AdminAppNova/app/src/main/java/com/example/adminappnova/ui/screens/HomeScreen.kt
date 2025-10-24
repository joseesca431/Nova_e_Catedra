package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = true,
                    onClick = {
                        navController.navigate("start") {
                            popUpTo("start") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D1B4E),
                        selectedTextColor = Color(0xFF2D1B4E),
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_categorias),
                            contentDescription = "Categorías",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Categorías", fontSize = 10.sp) },
                    selected = false,
                    onClick = {
                        navController.navigate("categories") {
                            popUpTo("start") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D1B4E),
                        selectedTextColor = Color(0xFF2D1B4E),
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pedido),
                            contentDescription = "Pedidos",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Pedidos", fontSize = 10.sp) },
                    selected = false,
                    onClick = {
                        navController.navigate("pedidos") {
                            popUpTo("start") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D1B4E),
                        selectedTextColor = Color(0xFF2D1B4E),
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Título superior
            Text(
                text = "Administración",
                fontSize = 20.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logo NOVA
            Text(
                text = "NOVA-e",
                fontSize = 48.sp,
                color = Color(0xFF2D1B4E),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cards de estadísticas - Primera fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pedidos pendientes",
                    value = "0",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Pedidos completados",
                    value = "0",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Devoluciones
            StatCard(
                title = "Devoluciones",
                value = "0",
                modifier = Modifier.fillMaxWidth(0.48f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acciones - Primera fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Historial de ventas",
                    backgroundColor = Color(0xFF2D1B4E),
                    modifier = Modifier.weight(1f),
                    onClick = { /* Sin funcionalidad */ }
                )

                ActionButton(
                    text = "Clientes registrados",
                    backgroundColor = Color(0xFFB695D4),
                    modifier = Modifier.weight(1f),
                    onClick = { /* Sin funcionalidad */ }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9D6B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
