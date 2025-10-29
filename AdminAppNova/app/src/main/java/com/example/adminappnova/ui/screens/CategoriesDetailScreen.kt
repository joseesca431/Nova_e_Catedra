package com.example.adminappnova.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Importar para escalar imagen
import androidx.compose.ui.platform.LocalContext // Para Coil (si usas imágenes)
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // Importar Coil si usas URLs de imagen
import coil.request.ImageRequest // Importar Coil si usas URLs de imagen
import com.example.adminappnova.R
import com.example.adminappnova.data.dto.ProductResponse // <-- Importar DTO
import com.example.adminappnova.ui.viewmodel.ProductListUiState // <-- Importar UiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

// Ya no necesitas la data class Product local

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDetailScreen(
    navController: NavController,
    categoryName: String,               // <-- Nombre para el título
    uiState: ProductListUiState,      // <-- Recibe estado del VM
    onProductClick: (ProductResponse) -> Unit, // <-- Evento click
    onAddProductClick: () -> Unit,       // <-- Evento FAB
    onRefresh: () -> Unit                // <-- Evento refresh
) {
    // Ya no necesitas: val products = remember { mutableStateListOf(...) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName, // Muestra el nombre de la categoría
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Botón Volver
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick, // Llama evento del VM
                containerColor = Color(0xFFFF801F), // Naranja
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Agregar producto", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        // --- SwipeRefresh para Pull-to-refresh ---
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading && uiState.products.isNotEmpty())
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = onRefresh, // Llama evento del VM
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues) // Padding de Scaffold
        ) {
            // --- Contenido principal (Lista o mensajes) ---
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.products.isEmpty()) {
                    // Carga inicial
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.products.isEmpty() && uiState.error == null && !uiState.isLoading) {
                    // Lista vacía
                    Text(
                        "No hay productos en esta categoría.\nPresiona '+' para agregar uno.",
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    // Muestra la lista de productos
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), // Ocupa espacio del SwipeRefresh
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp), // Padding + espacio para FAB
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cards
                    ) {
                        items(uiState.products, key = { it.idProducto }) { product -> // Usa el DTO y el ID real
                            ProductCard(
                                product = product, // Pasa el ProductResponse
                                onClick = { onProductClick(product) } // Llama evento del VM pasando el producto
                            )
                        }
                    } // Fin LazyColumn
                } // Fin else (muestra lista)

                // Muestra error de carga si existe (encima de la lista)
                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                        action = { Button(onClick = onRefresh) { Text("Reintentar") } }
                    ) { Text(text = error) }
                }
            } // Fin Box
        } // Fin SwipeRefresh
    } // Fin Scaffold
}

// --- Composable para cada tarjeta de producto ---
@Composable
fun ProductCard(
    product: ProductResponse, // <-- Recibe el ProductResponse DTO
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Altura de la tarjeta
            .clickable(onClick = onClick), // Llama al evento onClick recibido
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre imagen y texto
        ) {
            // --- Imagen del producto (Usando Coil para URL) ---
            Card(
                modifier = Modifier.size(116.dp), // Tamaño ajustado
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Fondo gris claro
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imagen ?: R.drawable.ic_producto) // Usa URL o placeholder
                        .crossfade(true)
                        .error(R.drawable.ic_producto) // Placeholder si falla la carga
                        .placeholder(R.drawable.ic_producto) // Placeholder mientras carga
                        .build(),
                    contentDescription = product.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio
                )
            } // Fin Card de Imagen

            // --- Información del producto ---
            Column(
                modifier = Modifier
                    .fillMaxHeight() // Ocupa toda la altura disponible
                    .weight(1f), // Ocupa el espacio restante
                verticalArrangement = Arrangement.SpaceBetween // Distribuye espacio
            ) {
                // Nombre del producto
                Text(
                    text = product.nombre,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2 // Limita a 2 líneas
                )

                // Stock y Precio
                Column {
                    // Stock (Cantidad)
                    Text(
                        // Muestra "Cantidad" y el valor, o "N/A" si es null
                        text = "Cantidad: ${product.cantidad?.toString() ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Precio
                    Text(
                        // Formatea el BigDecimal a 2 decimales
                        text = "$${"%.2f".format(product.precio)}",
                        fontSize = 20.sp,
                        color = Color(0xFFFF6B35), // Naranja
                        fontWeight = FontWeight.Bold
                    )
                }
            } // Fin Column de Información
        } // Fin Row principal
    } // Fin Card principal
}