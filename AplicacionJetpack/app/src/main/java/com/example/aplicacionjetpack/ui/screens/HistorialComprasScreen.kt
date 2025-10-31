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
// --- 👇👇👇 ¡¡¡LOS IMPORTS CORRECTOS DE LA VICTORIA!!! 👇👇👇 ---
import com.example.aplicacionjetpack.data.dto.HistorialPedidoResponse
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.HistorialPedidoViewModel // ¡EL NOMBRE CORRECTO!
// --- ------------------------------------------------------------- ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    navController: NavController,
    // --- 👇 ¡USAMOS EL VIEWMODEL CON EL NOMBRE CORRECTO! 👇 ---
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
                // --- 👇 ¡LA UI STATE USA 'pedidos', NO 'historial'! 👇 ---
                uiState.pedidos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.History, "Sin Historial", modifier = Modifier.size(64.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Aún no tienes compras en tu historial.", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // --- 👇👇👇 ¡¡¡LA KEY ÚNICA Y DEFINITIVA QUE EVITA EL CRASH!!! 👇👇👇 ---
                        // Usamos 'idHistorialPedido' que SÍ es único para cada entrada.
                        items(uiState.pedidos, key = { it.idHistorialPedido }) { pedidoItem ->
                            HistorialCard(item = pedidoItem)
                        }
                        // --- ------------------------------------------------------------------ ---
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialCard(item: HistorialPedidoResponse) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    // Mostramos el ID del pedido, aunque la key de la lista sea otra.
                    text = "Pedido #${item.idPedido}",
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Estado: ${item.estado}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            Text(
                text = item.fecha, // 'fecha' sí existe y es correcto
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
