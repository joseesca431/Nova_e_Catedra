package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.dto.CarritoItemResponse
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CarritoScreen(
    navController: NavController,
    viewModel: CarritoViewModel = hiltViewModel(),
    onPagarClick: (Long) -> Unit
) {
    val uiState = viewModel.uiState

    // Item seleccionado para eliminación (para mostrar modal)
    var itemToRemove by remember { mutableStateOf<CarritoItemResponse?>(null) }
    // Estado para controlar si el diálogo está visible
    val showRemoveDialog = itemToRemove != null

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MI CARRITO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeAccent,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurpleDark)
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error, color = Color.Red)
                    }
                }
                uiState.items.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "Tu carrito está vacío", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.items, key = { it.idCarritoItem }) { item ->
                            CartItemCard(
                                item = item,
                                // ahora solo notificamos que queremos eliminar, se muestra modal
                                onRemoveClicked = { itemToRemove = it }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val totalFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(uiState.total)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(totalFormatted, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Botón PAGAR (usa onPagarClick)
                Button(
                    onClick = {
                        val idCarritoValido = uiState.carrito?.idCarrito
                        onPagarClick(idCarritoValido ?: 0L)
                    },
                    enabled = uiState.items.isNotEmpty() && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("PAGAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar item
    if (showRemoveDialog) {
        val item = itemToRemove
        if (item != null) {
            AlertDialog(
                onDismissRequest = { itemToRemove = null },
                title = {
                    Text(text = "¿Eliminar producto?", color = PurpleDark, fontWeight = FontWeight.SemiBold)
                },
                text = {
                    Column {
                        Text(
                            text = "¿Estás seguro que deseas eliminar \"${item.producto?.nombre ?: "este producto"}\" del carrito?",
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No te preocupes — podrás agregarlo de nuevo si cambias de opinión.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Llamamos al viewModel para eliminar realmente
                            viewModel.removeItem(item.idCarritoItem)
                            itemToRemove = null
                        }
                    ) {
                        Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToRemove = null }) {
                        Text("Cancelar", color = PurpleDark)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun CartItemCard(item: CarritoItemResponse, onRemoveClicked: (CarritoItemResponse) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.producto?.imagen,
                contentDescription = item.producto?.nombre,
                placeholder = painterResource(id = R.drawable.ic_producto),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.producto?.nombre ?: "Producto",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cantidad: ${item.cantidad}", fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(
                onClick = { onRemoveClicked(item) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
            }
        }
    }
}
