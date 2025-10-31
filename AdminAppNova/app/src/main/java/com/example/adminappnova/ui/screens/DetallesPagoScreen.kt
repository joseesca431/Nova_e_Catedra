package com.example.adminappnova.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.ui.viewmodel.OrderDetailViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

// Colores del tema local (puedes moverlos a un archivo común)
private val PrimaryPurple = Color(0xFF2D1B4E)
private val AccentOrange = Color(0xFFFF6B35)
private val BackgroundGray = Color(0xFFF5F5F5)

/**
 * Pantalla de detalles del pedido.
 * Asume que OrderDetailViewModel expone:
 *   var uiState by mutableStateOf(OrderDetailUiState(...))
 * y funciones: loadOrderDetails(), cambiarEstado(...)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    // LEEMOS directamente la propiedad uiState del ViewModel
    val uiState = viewModel.uiState

    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    // Cargar datos la primera vez (el ViewModel ya extrae ids del SavedStateHandle)
    LaunchedEffect(Unit) {
        viewModel.loadOrderDetails()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedido #${uiState.pedido?.idPedido ?: "..."}", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple)
            )
        },
        containerColor = BackgroundGray,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            // Loading
            uiState.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }

            // Error general
            uiState.error != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error ?: "Error desconocido", color = Color.Red, textAlign = TextAlign.Center)
                }
            }

            // Mostrar pedido cuando exista
            uiState.pedido != null -> {
                val pedido = uiState.pedido!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SectionCard(title = "Resumen del Pedido") {
                            val estado = pedido.estado
                            DetailItem(
                                "Estado:",
                                estado?.name?.replace("_", " ") ?: "N/A",
                                highlight = true,
                                highlightColor = estadoColor(estado)
                            )
                            DetailItem("Fecha:", pedido.fechaInicio.substringBefore("T").ifEmpty { "N/A" })
                            DetailItem("Total:", formatCurrency(pedido.total))
                            DetailItem("Método de Pago:", pedido.tipoPago?.name?.replace("_", " ") ?: "N/A")
                        }
                    }

                    item {
                        SectionCard(title = "Información del Cliente") {
                            val usuario = uiState.usuario
                            if (usuario != null) {
                                val clienteIdStr = usuario.idUser.toString()
                                val nombreCompleto = listOf(
                                    usuario.primerNombre ?: "",
                                    usuario.primerApellido ?: ""
                                ).filter { it.isNotBlank() }.joinToString(" ").ifEmpty { "N/A" }

                                DetailItem("Cliente ID:", clienteIdStr)
                                DetailItem("Nombre:", nombreCompleto)
                                DetailItem("Email:", usuario.email ?: "N/A")
                                DetailItem("Username:", usuario.username ?: "N/A")
                            } else {
                                Text("Cargando datos del cliente...", color = Color.Gray)
                            }
                        }
                    }

                    item {
                        SectionCard(title = "Dirección de Envío") {
                            val calle = pedido.calleDireccion
                            if (!calle.isNullOrBlank()) {
                                val aliasSafe = pedido.aliasDireccion ?: "N/A"
                                val direccionSafe = "${pedido.calleDireccion ?: "N/A"}, ${pedido.ciudadDireccion ?: "N/A"}"
                                val deptoSafe = pedido.departamentoDireccion ?: "N/A"

                                DetailItem("Alias:", aliasSafe)
                                DetailItem("Dirección:", direccionSafe)
                                DetailItem("Departamento:", deptoSafe)
                            } else {
                                Text("Dirección no especificada en el pedido.", color = Color.Gray)
                            }
                        }
                    }

                    item {
                        SectionCard(title = "Productos del Pedido") {
                            if (uiState.pedidoItems.isEmpty()) {
                                Text("No se encontraron productos para este pedido.", color = Color.Gray)
                            }
                        }
                    }

                    items(items = uiState.pedidoItems, key = { it.idProducto }) { item ->
                        ProductoItemRow(item = item)
                        Divider(modifier = Modifier.padding(top = 8.dp))
                    }

                    item {
                        SectionCard(title = "Acciones") {
                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cambiar Estado del Pedido", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (uiState.isUpdatingStatus) {
                                Text("Actualizando estado...", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // fallback
            else -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay información disponible.", color = Color.Gray)
                }
            }
        }
    }

    // Diálogo para cambiar estado
    if (showDialog) {
        ChangeStatusDialog(
            estados = viewModel.estadosSeleccionables,
            onDismiss = { showDialog = false },
            onConfirm = { nuevoEstado, motivo ->
                // Llamada segura a la función del ViewModel (actualiza uiState ahí)
                viewModel.cambiarEstado(nuevoEstado, motivo)
                showDialog = false
            }
        )
    }

    // Snackbar para errores de acción (actionError)
    uiState.actionError?.let { ae ->
        LaunchedEffect(ae) {
            snackbarHostState.showSnackbar(ae)
        }
    }
}

/* ---------- Composables auxiliares ---------- */

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryPurple)
            Divider(color = BackgroundGray, thickness = 1.dp)
            content()
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, highlight: Boolean = false, highlightColor: Color = AccentOrange) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(120.dp), color = Color.DarkGray)
        Text(value, fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal, color = if (highlight) highlightColor else Color.Black)
    }
}

@Composable
private fun ProductoItemRow(item: PedidoItemDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${item.cantidad}x ${item.nombreProducto}", modifier = Modifier.weight(1f), maxLines = 2, color = Color.DarkGray, fontSize = 14.sp)
        Text(text = formatCurrency(item.precioUnitario), fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 12.dp), fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeStatusDialog(estados: List<EstadoPedido>, onDismiss: () -> Unit, onConfirm: (EstadoPedido, String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf(estados.firstOrNull()) }
    var motivo by remember { mutableStateOf("") }
    val needsMotivo = selectedEstado == EstadoPedido.CANCELADO

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Estado del Pedido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedEstado?.name?.replace("_", " ") ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nuevo Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        estados.forEach { estado ->
                            DropdownMenuItem(text = { Text(estado.name.replace("_", " ")) }, onClick = { selectedEstado = estado; expanded = false })
                        }
                    }
                }

                if (needsMotivo) {
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo de Cancelación (*)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedEstado?.let { onConfirm(it, if (needsMotivo) motivo else null) } },
                enabled = selectedEstado != null && (!needsMotivo || motivo.isNotBlank())
            ) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = AccentOrange) }
        }
    )
}

/* ---------- Utilitarios ---------- */

private fun formatCurrency(value: BigDecimal?): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "SV")).format(value ?: BigDecimal.ZERO)
}

