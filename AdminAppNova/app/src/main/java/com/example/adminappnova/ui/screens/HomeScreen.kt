package com.example.adminappnova.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R
import com.example.adminappnova.ui.viewmodel.HomeUiState // 👈 Importar UiState

@Composable
fun HomeScreen(
    navController: NavController,
    uiState: HomeUiState // 👈 Recibe el estado del ViewModel
) {
    // selectedTab controla la apariencia de la BottomBar
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp // Sombra para la barra
            ) {
                // Item Home
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = selectedTab == "Home", // Marcado como seleccionado si es la tab actual
                    onClick = {
                        selectedTab = "Home" // Actualiza el estado local
                        // No necesitas navegar si ya estás en Home
                        // navController.navigate("start") { ... }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D1B4E), // Color morado oscuro cuando seleccionado
                        selectedTextColor = Color(0xFF2D1B4E),
                        indicatorColor = Color.Transparent, // Sin fondo indicador
                        unselectedIconColor = Color.Gray,   // Color gris cuando no seleccionado
                        unselectedTextColor = Color.Gray
                    )
                )

                // Item Categorías
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
                        selectedTab = "Categorías" // Actualiza estado local
                        // Navega a la pantalla de categorías
                        navController.navigate("categories") {
                            popUpTo("start") { inclusive = false } // No elimina Home del historial
                            launchSingleTop = true // Evita duplicar la pantalla de categorías
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

                // Item Pedidos
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
                        selectedTab = "Pedidos" // Actualiza estado local
                        // Navega a la pantalla de pedidos
                        navController.navigate("pedidos") {
                            popUpTo("start") { inclusive = false } // No elimina Home del historial
                            launchSingleTop = true // Evita duplicar la pantalla de pedidos
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
        // Contenido Principal de la Pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Fondo gris claro
                .padding(paddingValues) // Padding de la Scaffold (para no solapar con BottomBar)
                .padding(horizontal = 16.dp), // Padding horizontal general
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espacio superior

            // Título "Administración"
            Text(
                text = "Administración",
                fontSize = 20.sp,
                color = Color(0xFFFF6B35), // Color naranja
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logo "NOVA-e"
            Text(
                text = "NOVA-e",
                fontSize = 48.sp,
                color = Color(0xFF2D1B4E), // Color morado oscuro
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Sección de Estadísticas (Condicional) ---
            if (uiState.isLoading) {
                // Muestra un indicador de carga si los datos aún no están listos
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else {
                // Muestra las cards una vez que los datos han cargado
                // Fila 1: Pendientes y Completados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cards
                ) {
                    StatCard(
                        title = "Pedidos pendientes",
                        value = uiState.pendingOrders.toString(), // 👈 Usa estado del VM
                        modifier = Modifier.weight(1f) // Ocupa mitad del espacio
                    )
                    StatCard(
                        title = "Pedidos completados",
                        value = uiState.completedOrders.toString(), // 👈 Usa estado del VM
                        modifier = Modifier.weight(1f) // Ocupa mitad del espacio
                    )
                }
                Spacer(modifier = Modifier.height(12.dp)) // Espacio entre filas de cards

                // Card Devoluciones (ocupa menos espacio)
                StatCard(
                    title = "Devoluciones",
                    value = uiState.returns.toString(), // 👈 Usa estado del VM
                    modifier = Modifier.fillMaxWidth(0.48f) // Aproximadamente mitad del ancho
                )

                // Muestra un mensaje de error si ocurrió uno al cargar
                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Empuja los botones de acción hacia el final
            Spacer(modifier = Modifier.weight(1f))

            // --- Sección de Botones de Acción ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Espacio inferior antes de la BottomBar
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre botones
            ) {
                ActionButton(
                    text = "Historial de ventas",
                    backgroundColor = Color(0xFF2D1B4E), // Morado oscuro
                    modifier = Modifier.weight(1f), // Ocupa mitad del espacio
                    onClick = { /* TODO: Navegar a la pantalla de historial de ventas */ }
                )
                ActionButton(
                    text = "Clientes registrados",
                    backgroundColor = Color(0xFFB695D4), // Morado claro
                    modifier = Modifier.weight(1f), // Ocupa mitad del espacio
                    onClick = { /* TODO: Navegar a la pantalla de lista de clientes */ }
                )
            }
        }
    }
}

// --- Composables Reutilizables (sin cambios) ---

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
            containerColor = Color(0xFFFF9D6B) // Naranja claro para las cards
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra ligera
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra el contenido verticalmente
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center, // Centra el título
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre título y valor
            Text(
                text = value,
                fontSize = 32.sp, // Valor grande
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
        modifier = modifier.height(50.dp), // Altura fija para los botones
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor // Color de fondo personalizado
        ),
        shape = RoundedCornerShape(12.dp) // Bordes redondeados
    ) {
        Text(
            text = text,
            color = Color.White, // Texto blanco
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center // Centra el texto en el botón
        )
    }
}