package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- Importar para scroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // <-- Importar para scroll
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
    uiState: HomeUiState // 👈 Recibe el estado del ViewModel (ahora actualizado)
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
                    selected = selectedTab == "Home",
                    onClick = { selectedTab = "Home" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D1B4E),
                        selectedTextColor = Color(0xFF2D1B4E),
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
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
        }
    ) { paddingValues ->
        // Contenido Principal (AHORA CON SCROLL)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // <-- AÑADIDO SCROLL
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Título "Administración"
            Text(
                text = "Administración",
                fontSize = 20.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logo "NOVA-e"
            Text(
                text = "NOVA-e",
                fontSize = 48.sp,
                color = Color(0xFF2D1B4E),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Sección de Estadísticas (ACTUALIZADA) ---
            if (uiState.isLoading) {
                // Muestra un indicador de carga
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else {
                // --- 1. Card de Ganancias Totales ---
                StatCard(
                    title = "Ganancias Totales",
                    // Formatea el BigDecimal (que puede ser null) a un String de moneda
                    value = "$${uiState.totalGanancias?.let { "%.2f".format(it) } ?: "0.00"}",
                    modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
                )

                Spacer(modifier = Modifier.height(24.dp)) // Espacio mayor

                // --- 2. Card de Productos Más Vendidos ---
                TopProductsCard(topProductos = uiState.topProductos) // Nuevo Composable

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
            // --- ------------------------------------ ---

            // Spacer(modifier = Modifier.weight(1f)) // Quitamos el spacer con weight

            // --- Sección de Botones de Acción (sin cambios) ---
            Spacer(modifier = Modifier.height(32.dp)) // Espacio fijo antes de botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Espacio inferior
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Historial de ventas",
                    backgroundColor = Color(0xFF2D1B4E),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Navegar a la pantalla de historial de ventas */ }
                )
                ActionButton(
                    text = "Clientes registrados",
                    backgroundColor = Color(0xFFB695D4),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Navegar a la pantalla de lista de clientes */ }
                )
            }
            Spacer(modifier = Modifier.height(16.dp)) // Espacio al final del scroll
        }
    }
}

// --- StatCard (sin cambios, sigue siendo útil) ---
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

// --- NUEVO COMPOSABLE: TopProductsCard ---
@Composable
fun TopProductsCard(
    topProductos: Map<String, Long>?, // Recibe el mapa (puede ser null)
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(), // Ocupa todo el ancho
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Fondo blanco
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sombra ligera
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la tarjeta
            Text(
                text = "Productos Más Vendidos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B4E) // Morado oscuro
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Comprueba si el mapa no es nulo o vacío
            if (topProductos.isNullOrEmpty()) {
                // Mensaje si no hay datos
                Text(
                    "No hay datos de productos.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                // Itera sobre el mapa (convertido a lista)
                // Usamos take(5) por si la API envía más de los pedidos
                topProductos.entries.toList().take(5).forEachIndexed { index, entry ->
                    ProductRankItem(
                        rank = index + 1,        // Rango (1, 2, 3...)
                        productName = entry.key, // Nombre del producto
                        count = entry.value      // Cantidad vendida
                    )
                    // Añade un divisor entre items, excepto en el último
                    if (index < topProductos.size - 1 && index < 4) { // No añadir divisor después del 5to
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))
                    }
                }
            }
        }
    }
}

// --- NUEVO COMPOSABLE: ProductRankItem (para la lista en TopProductsCard) ---
@Composable
private fun ProductRankItem(
    rank: Int,
    productName: String,
    count: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Rango y Nombre
        Text(
            text = "$rank. $productName",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f).padding(end = 8.dp), // Peso para que el texto se ajuste
            maxLines = 2, // Permite hasta 2 líneas por si el nombre es largo
            textAlign = TextAlign.Start // Alinea a la izquierda
        )
        // Cantidad
        Text(
            text = "$count Vendidos",
            fontSize = 14.sp,
            color = Color(0xFFFF6B35), // Naranja
            fontWeight = FontWeight.Bold
        )
    }
}


// --- ActionButton (sin cambios) ---
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