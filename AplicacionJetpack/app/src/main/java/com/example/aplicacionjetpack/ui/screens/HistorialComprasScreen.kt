package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.HistorialPedidoViewModel

// --- 👇👇👇 ¡¡¡NUEVOS COLORES PARA LOS ESTADOS!!! 👇👇👇 ---
val EstadoVerde = Color(0xFF4CAF50)      // Verde para Entregado/Pagado
val EstadoAmarillo = Color(0xFFFFA000)   // Naranja/Amarillo para Pendiente/En_Proceso
val EstadoRojo = Color(0xFFD32F2F)       // Rojo para Cancelado
// --- ---------------------------------------------------- ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    navController: NavController,
    viewModel: HistorialPedidoViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Compras", color = PurpleDark, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurpleDark)
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.error,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                uiState.pedidos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.History, "Sin Historial", modifier = Modifier.size(64.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes compras relevantes en tu historial.", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.pedidos, key = { it.idHistorialPedido }) { pedidoItem ->
                            HistorialCard(item = pedidoItem)
                        }
                    }
                }
            }
        }
    }
}

// --- 👇👇👇 ¡AQUÍ INYECTAMOS EL COLOR! 👇👇👇 ---
@Composable
private fun HistorialCard(item: HistorialPedidoResponse) {
    // Obtenemos el color basado en el estado del item
    val estadoColor = obtenerColorPorEstado(item.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pedido #${item.idPedido}",
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Estado: ", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    Text(
                        text = item.estado?.replace("_", " ") ?: "DESCONOCIDO", // Reemplaza "_" por espacio
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = estadoColor // ¡COLOR APLICADO!
                    )
                }
            }
            Text(
                text = item.fecha?.substringBefore("T") ?: "", // Mostramos solo la fecha
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

/**
 * Función helper que devuelve un color basado en el string del estado.
 */
@Composable
private fun obtenerColorPorEstado(estado: String?): Color {
    return when (estado?.uppercase()) {
        "ENTREGADO", "PAGADO" -> EstadoVerde
        "CANCELADO" -> EstadoRojo
        "EN_PROCESO" -> EstadoAmarillo
        else -> Color.Gray // Color por defecto para estados desconocidos o nulos
    }
}
