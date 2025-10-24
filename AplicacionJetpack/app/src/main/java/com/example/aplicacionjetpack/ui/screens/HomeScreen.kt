package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.aplicacionjetpack.model.Product

// Colores personalizados (basados en las vistas previas)
val PurpleDark = Color(0xFF2D1B4E)
val OrangeAccent = Color(0xFFFF6B35)


// Lista de productos de muestra (asume R.drawable.ic_product_placeholder existe)
val sampleProducts = List(2) {
    Product(
        name = "Lampara de Brook One Piece",
        price = "$40.00",
        // Aquí usa una imagen placeholder que definas en tu carpeta 'drawable'
        imageResId = R.drawable.ic_producto
    )
}

@Composable
fun ProductCard(product: Product, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp)) // Agrega esto antes del clickable
            .background(Color.White)
            .clickable {
                // Navegar al detalle del producto
                try {
                    navController.navigate("product_detail")
                } catch (e: Exception) {
                    // Manejo de error por si acaso
                    e.printStackTrace()
                }
            }
            .padding(4.dp) // Padding interno después del background
    ) {
        // Espacio para la Imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFEFEFEF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_producto),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Nombre del Producto
        Text(
            text = product.name,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
        )

        // Precio del Producto
        Text(
            text = product.price,
            fontSize = 16.sp,
            color = PurpleDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
    }
}

// Y actualiza también la función HomeScreen para pasar el navController:

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = { HomeTopBar(navController) },
        bottomBar = {
            HomeBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sampleProducts) { product ->
                ProductCard(
                    product = product,
                    navController = navController // Pasa el navController aquí
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(navController: NavController) {
    // Definimos el color naranja y púrpura para usarlo de manera consistente

    Row(
        // Modificador clave para posicionarse debajo de la barra de estado
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .height(56.dp) // Altura estándar para una App Bar
            .padding(horizontal = 16.dp), // Espaciado a los lados
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo NOVA+e (Izquierda)
        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier
                .width(90.dp)
                .height(40.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja el siguiente elemento a la derecha

        // Botón/Ícono de Búsqueda (Derecha)
        Button(
            onClick = { navController.navigate("busqueda") }, // Cambia esta línea
            modifier = Modifier.size(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeAccent
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class BottomNavItem(val label: String, val iconResId: Int, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home", R.drawable.ic_home, "home"),
    BottomNavItem("Perfil", R.drawable.ic_perfil, "profile"),
    BottomNavItem("Carrito", R.drawable.ic_carrito, "cart")
)

@Composable
fun HomeBottomBar(navController: NavController,
                  selectedTab: String,
                  onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = selectedTab == item.label
            // Usamos el color principal de tu app para el ícono y texto seleccionado
            val activeColor = PurpleDark
            val inactiveColor = Color.Gray

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onTabSelected(item.label)
                    navController.navigate(item.route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true}
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) activeColor else inactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                },
                // El indicador se mantiene simple (o transparente) para no oscurecer el diseño
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White, // Fondo limpio
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor
                )
            )
        }
    }
}