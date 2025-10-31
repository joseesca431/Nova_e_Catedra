package com.example.adminappnova.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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

// COLORES
private val PrimaryPurple = Color(0xFF2D1B4E)
private val AccentOrange = Color(0xFFFF6B35)
private val BackgroundGray = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    // ---------- LECTURA DEL UI STATE ----------
    // Importante: viewModel.uiState debe ser public StateFlow<OrderDetailUiState>
    //
    // !! CORRECCIÓN !!
    // Los errores "Receiver type mismatch" indican que viewModel.uiState es un Flow<T>
    // y no un StateFlow<T>. Un Flow<T> REQUIERE un valor inicial.
    //
    // Aquí usamos 'OrderDetailUiState()' como estado inicial.
    // Esta clase de 'data' está definida al final de este archivo como placeholder.
    // Si ya tienes esta clase definida en tu ViewModel, puedes borrar
    // la de este archivo (pero asegúrate de que tenga valores por defecto).
    // CORRECTO (Línea 51)
    val uiState = viewModel.uiState


    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

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
        containerColor = BackgroundGray
    ) { padding ->
        when {
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

            uiState.error != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            uiState.pedido != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen del pedido
                    item {
                        SectionCard(title = "Resumen del Pedido") {
                            val pedido = uiState.pedido!!
                            DetailItem(
                                "Estado:",
                                pedido.estado?.name?.replace("_", " ") ?: "N/A",
                                highlight = true,
                                highlightColor = pedidoEstadoColor(pedido.estado)
                            )
                            DetailItem("Fecha:", pedido.fechaInicio.substringBefore("T"))
                            DetailItem("Total:", formatCurrency(pedido.total))
                            DetailItem("Método de Pago:", pedido.tipoPago?.name?.replace("_", " ") ?: "N/A")
                        }
                    }

                    // Información del cliente
                    item {
                        SectionCard(title = "Información del Cliente") {
                            val usuario = uiState.usuario
                            if (usuario != null) {
                                DetailItem("Cliente ID:", usuario.idUser.toString())
                                DetailItem("Nombre:", "${usuario.primerNombre} ${usuario.primerApellido}")
                                DetailItem("Email:", usuario.email)
                                DetailItem("Username:", usuario.username)
                            } else {
                                Text("Cargando datos del cliente...")
                            }
                        }
                    }

                    // Encabezado productos
                    item {
                        SectionCard(title = "Productos del Pedido") {
                            if (uiState.pedidoItems.isEmpty()) {
                                Text("No se encontraron productos para este pedido.", color = Color.Gray)
                            }
                        }
                    }

                    // Lista de productos
                    items(items = uiState.pedidoItems, key = { item -> item.idProducto }) { item ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ProductoItemRow(item)
                            Divider(modifier = Modifier.padding(top = 8.dp), color = BackgroundGray)
                        }
                    }

                    // Acciones
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
                        }
                    }
                }
            }

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

    // Dialogo de cambio de estado
    if (showDialog) {
        ChangeStatusDialog(
            estados = viewModel.estadosSeleccionables,
            onDismiss = { showDialog = false },
            onConfirm = { nuevoEstado, motivo ->
                viewModel.cambiarEstado(nuevoEstado, motivo)
                showDialog = false
            }
        )
    }

    // Snackbar para actionError
    uiState.actionError?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
        }
    }
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth().padding(16.dp))
}


// -------------------- Auxiliares --------------------

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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedEstado?.name?.replace("_", " ") ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nuevo Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, unfocusedBorderColor = Color.Gray)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        estados.forEach { estado ->
                            DropdownMenuItem(text = { Text(estado.name.replace("_", " ")) }, onClick = { selectedEstado = estado; expanded = false })
                        }
                    }
                }

                if (needsMotivo) {
                    OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo de Cancelación (*)") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, unfocusedBorderColor = Color.Gray))
                }
            }
        },
        confirmButton = {
            Button(onClick = { selectedEstado?.let { onConfirm(it, if (needsMotivo) motivo else null) } }, enabled = selectedEstado != null && (!needsMotivo || motivo.isNotBlank()), colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = AccentOrange) }
        }
    )
}

private fun formatCurrency(value: BigDecimal?): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "SV")).format(value ?: BigDecimal.ZERO)
}

// Nombre único para evitar duplicados en el proyecto
private fun pedidoEstadoColor(estado: EstadoPedido?): Color {
    return when (estado) {
        EstadoPedido.PENDIENTE -> AccentOrange
        EstadoPedido.EN_PROCESO -> PrimaryPurple
        EstadoPedido.ENTREGADO -> Color(0xFF2E7D32)
        EstadoPedido.CANCELADO -> Color.Red
        else -> Color.Gray
    }
}

// -------------------- PLACEHOLDERS --------------------
// !! IMPORTANTE !!
// Estas son clases de ejemplo para que el código compile.
// Lo más probable es que YA TENGAS estas clases definidas en tu
// capa de 'data' o 'viewModel'. Si es así, puedes borrar
// estas definiciones y asegurarte de que tu clase UiState
// tenga valores por defecto.

/** Enum de ejemplo para el tipo de pago (basado en el uso de .name) */
enum class TipoPago {
    TARJETA_CREDITO,
    EFECTIVO,
    TRANSFERENCIA
}

/** Clase de ejemplo para el DTO del Pedido (basada en las propiedades que usas) */
data class PedidoDto(
    val idPedido: Int = 0,
    val estado: EstadoPedido? = null,
    val fechaInicio: String = "",
    val total: BigDecimal = BigDecimal.ZERO,
    val tipoPago: TipoPago? = null // Usamos el Enum de ejemplo
)

/** Clase de ejemplo para el DTO del Usuario (basada en las propiedades que usas) */
data class UsuarioDto(
    val idUser: Int = 0,
    val primerNombre: String = "",
    val primerApellido: String = "",
    val email: String = "",
    val username: String = ""
)

/**
 * Esta es la clase de estado que 'collectAsState' necesita como valor inicial.
 * Debe tener valores por defecto para todas sus propiedades para poder
 * crear una instancia vacía: OrderDetailUiState()
 */
data class OrderDetailUiState(
    val isLoading: Boolean = true, // Inicia como 'true' para mostrar el loader
    val error: String? = null,
    val actionError: String? = null,
    val pedido: PedidoDto? = null,
    val usuario: UsuarioDto? = null,
    val pedidoItems: List<PedidoItemDto> = emptyList()
)