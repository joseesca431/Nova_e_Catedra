package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// Negro puro para textos fuertes (pediste BrandBlack)
private val BrandBlack = Color(0xFF000000)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val SoftBackground = Color(0xFFF8F7F5)

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

    // Dialog state para validación
    var showValidationDialog by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }

    // Cargar carrito (el userViewModel ya carga su estado en init)
    LaunchedEffect(key1 = Unit) {
        carritoViewModel.loadCarrito()
    }

    // Usar los campos del estado del viewmodel (evita referencias a `user`)
    val nombreUsuario = userUiState.username.ifBlank { "" }
    val correoUsuario = userUiState.email.ifBlank { "" }
    val telefonoUsuario = userUiState.telefono.ifBlank { "" }
    val direccion = checkoutUiState.direccion.orEmpty()
    val municipio = checkoutUiState.municipio.orEmpty()
    val departamento = checkoutUiState.departamento.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pago", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = SoftBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBackground)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                "DETALLES DE ENTREGA",
                style = MaterialTheme.typography.titleMedium,
                color = OrangeAccent,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (userUiState.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeAccent)
                }
            } else {
                InfoRow(label = "Nombre", value = if (nombreUsuario.isNotBlank()) nombreUsuario else "No disponible")
                InfoRow(label = "Correo", value = if (correoUsuario.isNotBlank()) correoUsuario else "No disponible")
                InfoRow(label = "Teléfono", value = if (telefonoUsuario.isNotBlank()) telefonoUsuario else "No disponible")
                InfoRow(label = "Dirección", value = listOf(direccion, municipio, departamento).filter { it.isNotBlank() }.joinToString(", ").ifBlank { "No disponible" })
            }

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            Text(
                "RESUMEN DE COMPRA",
                style = MaterialTheme.typography.titleMedium,
                color = OrangeAccent,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (carritoUiState.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeAccent)
                }
            } else if (carritoUiState.items.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay items en el resumen.", color = BrandBlack)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(carritoUiState.items) { item ->
                        ProductoResumenItem(item)
                        Divider(color = Color(0xFFEAEAEA), thickness = 1.dp)
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

            Button(
                onClick = {
                    // Validación previa al navegar a pago
                    val missing = mutableListOf<String>()
                    if (nombreUsuario.isBlank()) missing.add("Nombre de usuario")
                    if (correoUsuario.isBlank()) missing.add("Correo electrónico")
                    if (telefonoUsuario.isBlank()) missing.add("Teléfono")
                    if (direccion.isBlank()) missing.add("Dirección")
                    if (municipio.isBlank()) missing.add("Municipio")
                    if (departamento.isBlank()) missing.add("Departamento")
                    if (carritoUiState.items.isEmpty()) {
                        // protección adicional: botón normalmente estará deshabilitado, pero por seguridad
                        validationMessage = "Tu carrito está vacío. Añade productos antes de continuar."
                        showValidationDialog = true
                        return@Button
                    }
                    if (missing.isNotEmpty()) {
                        val listText = missing.joinToString(separator = "\n") { "• $it" }
                        validationMessage = "Faltan datos para el envío:\n\n$listText\n\nPor favor completa los campos faltantes antes de continuar."
                        showValidationDialog = true
                        return@Button
                    }

                    // Si todo OK, navegar a la pantalla de pago
                    navController.navigate("pago/$idCarrito")
                },
                enabled = carritoUiState.items.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continuar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialogo de validación
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("Entendido", color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Información incompleta", color = BrandBlack, fontWeight = FontWeight.SemiBold) },
            text = {
                Text(
                    validationMessage,
                    color = BrandBlack.copy(alpha = 0.95f),
                    fontSize = 14.sp
                )
            },
            containerColor = SurfaceWhite
        )
    }
}

// --- Funciones auxiliares ---

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = BrandBlack.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = BrandBlack)
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
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF2F2F2))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.producto?.nombre ?: "Producto", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = BrandBlack)
            Text("Cantidad: ${item.cantidad}", style = MaterialTheme.typography.bodySmall, color = BrandBlack.copy(alpha = 0.7f))
        }
        val priceFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(item.producto?.precio ?: 0.0)
        Text(priceFormatted, fontWeight = FontWeight.Medium, color = BrandBlack)
    }
}
