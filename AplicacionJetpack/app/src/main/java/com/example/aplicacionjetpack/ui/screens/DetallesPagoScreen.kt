package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel
import com.example.aplicacionjetpack.ui.viewmodel.UserViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    idCarrito: Long,
    checkoutViewModel: CheckoutViewModel,
    carritoViewModel: CarritoViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val checkoutUiState = checkoutViewModel.uiState
    val carritoUiState = carritoViewModel.uiState
    val userUiState = userViewModel.uiState

    // --- 👇👇👇 ¡¡¡LA LÓGICA SIMPLIFICADA Y CORRECTA!!! 👇👇👇 ---
    // Ya NO se crea ningún pedido aquí.
    // Solo nos aseguramos de que los datos visuales (carrito y usuario) estén cargados.
    LaunchedEffect(key1 = Unit) {
        carritoViewModel.loadCarrito()
        // El userViewModel ya se carga en su 'init', no es necesario llamarlo aquí.
    }
    // --- --------------------------------------------------- ---

    val nombreUsuario = userUiState.user?.username ?: "Cargando..."
    val correoUsuario = userUiState.user?.email ?: "Cargando..."
    val telefonoUsuario = userUiState.user?.telefono ?: "No disponible"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pago", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("DETALLES DE ENTREGA", style = MaterialTheme.typography.titleMedium, color = OrangeAccent, modifier = Modifier.padding(bottom = 16.dp))

            if (userUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                InfoRow(label = "Nombre", value = nombreUsuario)
                InfoRow(label = "Correo", value = correoUsuario)
                InfoRow(label = "Teléfono", value = telefonoUsuario)
                InfoRow(label = "Dirección", value = "${checkoutUiState.direccion}, ${checkoutUiState.municipio}, ${checkoutUiState.departamento}")
            }

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            Text("RESUMEN DE COMPRA", style = MaterialTheme.typography.titleMedium, color = OrangeAccent, modifier = Modifier.padding(bottom = 16.dp))

            if (carritoUiState.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (carritoUiState.items.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay items en el resumen.")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(carritoUiState.items) { item ->
                        ProductoResumenItem(item)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL:", style = MaterialTheme.typography.headlineSmall, color = PurpleDark, fontWeight = FontWeight.Bold)
                val totalFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(carritoUiState.total)
                Text(totalFormatted, style = MaterialTheme.typography.headlineMedium, color = PurpleDark, fontWeight = FontWeight.ExtraBold)
            }

            // --- 👇👇👇 ¡¡¡EL BOTÓN SIMPLIFICADO!!! 👇👇👇 ---
            Button(
                onClick = { navController.navigate("pago/$idCarrito") },
                // El botón siempre está habilitado a menos que el carrito esté vacío.
                enabled = carritoUiState.items.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continuar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            // --- -------------------------------------------- ---
        }
    }
}

// --- Componentes auxiliares (sin cambios) ---

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
    }
}

@Composable
private fun ProductoResumenItem(item: CarritoItemResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.producto?.imagen,
            contentDescription = item.producto?.nombre,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.producto?.nombre ?: "Producto", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Cantidad: ${item.cantidad}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        val priceFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(item.producto?.precio ?: 0.0)
        Text(priceFormatted, fontWeight = FontWeight.Medium)
    }
}
