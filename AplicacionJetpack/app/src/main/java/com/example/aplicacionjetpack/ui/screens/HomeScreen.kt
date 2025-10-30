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
import androidx.compose.runtime.mutableStateOf // Necesario para selectedTab
import androidx.compose.runtime.remember // Necesario para selectedTab
import androidx.compose.runtime.getValue // Necesario para selectedTab
import androidx.compose.runtime.setValue // Necesario para selectedTab
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Para Coil
import androidx.compose.ui.platform.LocalContext // Para Coil
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // Importar
import androidx.compose.ui.text.style.TextOverflow // Para nombres largos
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // <-- Importar Coil
import coil.request.ImageRequest // <-- Importar Coil
import com.example.aplicacionjetpack.R
// Importa el DTO real, no el modelo simulado
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.ui.viewmodel.HomeUiState // Importar UiState
import com.google.accompanist.swiperefresh.SwipeRefresh // Para Pull-to-refresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState // Para Pull-to-refresh


// Colores (puedes moverlos a ui/theme/Color.kt)
val PurpleDark = Color(0xFF2D1B4E)
val OrangeAccent = Color(0xFFFF6B35)

// Ya no necesitas 'sampleProducts' ni 'data class Product'

@Composable
fun ProductCard(
    product: ProductResponse, // <-- Acepta el DTO real
    onClick: () -> Unit // <-- Evento click
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick) // Llama al evento
            .padding(4.dp)
    ) {
        // Espacio para la Imagen (con Coil)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Mantiene la proporción cuadrada
                .background(Color(0xFFEFEFEF)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imagen) // <-- Usa la URL de la API
                    .crossfade(true)
                    .placeholder(R.drawable.ic_producto) // Placeholder
                    .error(R.drawable.ic_producto) // Imagen de error
                    .build(),
                contentDescription = product.nombre,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop // Escala la imagen
            )
        }

        // Nombre del Producto
        Text(
            text = product.nombre, // <-- Usa dato real
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
            maxLines = 2, // Limita el nombre a 2 líneas
            overflow = TextOverflow.Ellipsis // Añade "..." si es muy largo
        )

        // Precio del Producto
        Text(
            text = "$${"%.2f".format(product.precio)}", // <-- Usa y formatea dato real
            fontSize = 16.sp,
            color = PurpleDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 4.dp)
        )
    }
}

// HomeScreen AHORA ACEPTA UiState y Eventos
@Composable
fun HomeScreen(
    navController: NavController,
    uiState: HomeUiState, // <-- Recibe el UiState
    onRefresh: () -> Unit, // <-- Recibe evento de refresh
    onProductClick: (ProductResponse) -> Unit, // <-- Recibe evento de click
    onSearchClick: () -> Unit // <-- Recibe evento de click en búsqueda
) {
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = { HomeTopBar(onSearchClick = onSearchClick) }, // Pasa el evento
        bottomBar = {
            HomeBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController
            )
        }
    ) { paddingValues ->
        // Contenedor para Pull-to-refresh y manejo de estados
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = uiState.isLoading && uiState.products.isNotEmpty() // Muestra refresh solo si ya hay items
        )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = onRefresh, // Llama al ViewModel
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                if (uiState.isLoading && uiState.products.isEmpty()) {
                    // Carga inicial
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    // Error
                    Text(
                        text = uiState.error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (uiState.products.isEmpty() && !uiState.isLoading) {
                    // Lista vacía
                    Text(
                        text = "No se encontraron productos.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Contenido: Grilla de Productos
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.products, key = { it.idProducto }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product) } // Llama al evento
                            )
                        }
                        // TODO: Añadir item al final para paginación (cargar más)
                    }
                }
            } // Fin Box
        } // Fin SwipeRefresh
    } // Fin Scaffold
}

@Composable
fun HomeTopBar(onSearchClick: () -> Unit) { // <-- Acepta evento
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.novainicio),
            contentDescription = "Logo NOVA+e",
            modifier = Modifier.width(90.dp).height(40.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSearchClick, // <-- Llama al evento
            modifier = Modifier.size(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Search, "Buscar", tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

// --- BottomBar (sin cambios) ---
data class BottomNavItem(val label: String, val iconResId: Int, val route: String)
val bottomNavItems = listOf(
    BottomNavItem("Home", R.drawable.ic_home, "home"),
    BottomNavItem("Perfil", R.drawable.ic_perfil, "profile"),
    BottomNavItem("Carrito", R.drawable.ic_carrito, "cart")
)
@Composable
fun HomeBottomBar(navController: NavController, selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = selectedTab == item.label
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
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White,
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor
                )
            )
        }
    }
}