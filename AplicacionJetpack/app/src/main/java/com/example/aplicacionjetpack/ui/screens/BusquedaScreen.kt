package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Importar
import androidx.compose.material.icons.filled.ArrowBack // Importar
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
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.dto.ProductResponse // Importar DTO real
import com.example.aplicacionjetpack.ui.viewmodel.SearchUiState // Importar UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    navController: NavController,
    uiState: SearchUiState, // <-- Recibe estado
    onQueryChange: (String) -> Unit, // <-- Recibe evento
    onProductClick: (ProductResponse) -> Unit // <-- Recibe evento
) {
    // Ya no necesitas: var searchText by remember ...

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Barra de búsqueda", fontSize = 18.sp, color = Color(0xFF2D1B4E), fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) { // Botón Volver
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = Color(0xFF2D1B4E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Muestra la barra inferior, 'Home' seleccionado, 'onClick' no hace nada
            HomeBottomBar(
                selectedTab = "Home",
                onTabSelected = { /* No cambiar tab desde búsqueda */ },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // --- Sección de Búsqueda y Filtros ---
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de búsqueda
                OutlinedTextField(
                    value = uiState.searchQuery, // <-- Usa UiState
                    onValueChange = onQueryChange, // <-- Llama evento del VM
                    placeholder = { Text("Buscar productos...", color = Color.Gray, fontSize = 14.sp) }, // Placeholder mejorado
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1B4E), unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = uiState.error != null && uiState.searchQuery.isBlank()
                )

                // Botón de Filtros (funcionalidad no implementada)
                Button(
                    onClick = { /* TODO: Implementar filtros */ },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    // --- 👇 ROW CORREGIDO CON PARÁMETROS NOMBRADOS 👇 ---
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp), // <-- Define espaciado
                        verticalAlignment = Alignment.CenterVertically    // <-- Define alineación
                    ) {
                        // --- -------------------------------------------- ---
                        Text("Filtros", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Icon(painterResource(id = R.drawable.ic_atras), "Filtros", tint = Color.White, modifier = Modifier.size(20.dp)) // TODO: Usar un icono de filtro real
                    }
                }
            } // Fin Row Búsqueda

            // --- Grid de Productos (Resultados) ---
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    // Muestra carga si la lista de fondo aún no ha llegado
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).padding(top = 32.dp))
                } else if (uiState.error != null) {
                    // Muestra error si la carga inicial falló
                    Text(
                        uiState.error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (uiState.searchQuery.isNotBlank() && uiState.searchResults.isEmpty()) {
                    // Muestra "No encontrado" si se buscó pero no hay resultados
                    Text(
                        "No se encontraron resultados para '${uiState.searchQuery}'",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (uiState.searchQuery.isBlank()) {
                    // Mensaje inicial si no se ha buscado nada
                    Text(
                        "Escribe en la barra para buscar productos...",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Muestra la grilla de resultados
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.searchResults, key = { it.idProducto }) { product ->
                            // Usa el ProductCard reutilizable (definido en HomeScreen.kt)
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product) } // Llama evento
                            )
                        }
                    }
                }
            } // Fin Box Grid
        } // Fin Column principal
    } // Fin Scaffold
}