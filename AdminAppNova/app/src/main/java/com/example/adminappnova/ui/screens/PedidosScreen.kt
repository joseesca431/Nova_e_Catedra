package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
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

data class Pedido(
    val id: String,
    val fecha: String,
    val cliente: String,
    val monto: String
)

@Composable
fun PedidosScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Pedidos") }
    val pedidos = remember {
        mutableStateListOf(
            Pedido(
                id = "Pedido X",
                fecha = "fecha de pedido",
                cliente = "Nombre del cliente",
                monto = "$0.00"
            )
        )
    }

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
                    selected = selectedTab == "Home",
                    onClick = {
                        selectedTab = "Home"
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
                    selected = selectedTab == "Categorías",
                    onClick = {
                        selectedTab = "Categorías"
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
                    selected = selectedTab == "Pedidos",
                    onClick = { selectedTab = "Pedidos" },
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

            // Lista de pedidos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos) { pedido ->
                    PedidoCard(pedido = pedido, navController = navController)
                }

            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                navController.navigate("detalles_pago")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B4E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título del pedido con fecha
            Text(
                text = pedido.id,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            // Nombre del cliente
            Text(
                text = pedido.cliente,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )

            // Monto
            Text(
                text = pedido.monto,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}