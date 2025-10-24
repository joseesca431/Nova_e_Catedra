package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun CategoriesScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Categorías") }
    val categories = remember { mutableStateListOf("Categoría 1") }

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
                    onClick = { selectedTab = "Categorías" },
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
                    onClick = {
                        selectedTab = "Pedidos"
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Agregar nueva categoría */ },
                containerColor = Color(0xFFFF801F),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar categoría",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
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

            // Lista de categorías
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        categoryName = category,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    categoryName: String,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                navController.navigate("categories_detail/$categoryName")
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = Color(0xFF2D1B4E),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = categoryName,
                fontSize = 16.sp,
                color = Color(0xFF2D1B4E),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}