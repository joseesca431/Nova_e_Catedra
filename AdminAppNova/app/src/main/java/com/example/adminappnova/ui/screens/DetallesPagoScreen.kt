package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.ui.viewmodel.OrderDetailUiState
import com.example.adminappnova.ui.screens.estadoColor // Importa desde PedidosScreen.kt
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    uiState: OrderDetailUiState,
    // --- 👇 SOLO NECESITAMOS UN EVENTO GENÉRICO (y los de VM) 👇 ---
    viewModel: com.example.adminappnova.ui.viewmodel.OrderDetailViewModel // Pasamos el VM entero para acceder a la lista de estados
    // onConfirmarPedido: () -> Unit, // Ya no se usa
    // onIniciarEnvio: () -> Unit, // Ya no se usa
    // onMarcarEntregado: () -> Unit // Ya no se usa
    // ---------------------------------------------------------
) {
    // --- State para el Dropdown y el diálogo de cancelación ---
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    // ---------------------------------------------------

    Scaffold(
        topBar = { /* ... (TopAppBar sin cambios) ... */ }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // --- Estados de Carga/Error/Vacío (sin cambios) ---
            if (uiState.isLoading) { /* ... */ }
            else if (uiState.error != null) { /* ... */ }
            else if (uiState.pedido == null) { /* ... */ }
            // ---------------------------------------------
            else {
                // --- Muestra los Detalles del Pedido ---
                val pedido = uiState.pedido

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Información General (sin cambios) ---
                    Text("Información General", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))
                    DetailItem(label = "ID Pedido", value = pedido.idPedido.toString())
                    DetailItem(label = "Fecha Creación", value = formatDateTime(pedido.fechaInicio))
                    DetailItem(label = "Fecha Finalización", value = formatDateTime(pedido.fechaFinal))
                    DetailItem(label = "Estado Actual", value = pedido.estado?.name ?: "N/A", valueColor = estadoColor(pedido.estado))
                    DetailItem(label = "Tipo de Pago", value = pedido.tipoPago?.name ?: "N/A")
                    DetailItem(label = "Total", value = "$${pedido.total?.let { "%.2f".format(it) } ?: "0.00"}", isBoldValue = true)
                    DetailItem(label = "Puntos Otorgados", value = pedido.puntosTotales?.toString() ?: "-")

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // --- Información de Envío (sin cambios) ---
                    Text("Información de Envío", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))
                    DetailItem(label = "Alias", value = pedido.aliasDireccion ?: "-")
                    DetailItem(label = "Dirección", value = "${pedido.calleDireccion ?: "-"}, ${pedido.ciudadDireccion ?: "-"}, ${pedido.departamentoDireccion ?: "-"}")

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // --- 👇 SECCIÓN DE ACCIONES (REEMPLAZADA) 👇 ---
                    Text("Actualizar Estado", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))

                    // Muestra error de acción si existe
                    uiState.actionError?.let { error ->
                        Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // Comprueba si el pedido está en un estado final
                    val isFinalState = pedido.estado == EstadoPedido.ENTREGADO || pedido.estado == EstadoPedido.CANCELADO

                    // Muestra indicador de carga si se está actualizando
                    if (uiState.isUpdatingStatus) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizando estado...", color = Color.Gray)
                        }
                    } else {
                        // Muestra el Menú Desplegable
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded && !isFinalState, // No se expande si el estado es final
                            onExpandedChange = {
                                if (!isFinalState) isDropdownExpanded = it // No permite abrir si el estado es final
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // TextField que muestra el estado actual
                            OutlinedTextField(
                                value = estadoToString(pedido.estado), // Muestra estado actual
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cambiar estado a:") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                        contentDescription = "Desplegar",
                                        tint = if (isFinalState) Color.Gray else Color.Black // Icono gris si es estado final
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    disabledContainerColor = Color(0xFFFAFAFA), // Color gris claro si deshabilitado
                                    focusedBorderColor = Color(0xFF2D1B4E),
                                    unfocusedBorderColor = if (isFinalState) Color.LightGray else Color.Gray,
                                    // Color de texto gris si es estado final
                                    disabledTextColor = if (isFinalState) Color.Gray else Color.Black,
                                    disabledLabelColor = Color.Gray,
                                    disabledTrailingIconColor = Color.Gray
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                enabled = !isFinalState // Deshabilita el TextField si es estado final
                            )

                            // Opciones del Menú
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded && !isFinalState,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Itera sobre la lista de estados seleccionables del ViewModel
                                viewModel.estadosSeleccionables.forEach { estadoOpcion ->
                                    DropdownMenuItem(
                                        text = { Text(estadoToString(estadoOpcion)) },
                                        onClick = {
                                            isDropdownExpanded = false
                                            if (estadoOpcion == EstadoPedido.CANCELADO) {
                                                // Si es "Cancelar", muestra el diálogo de motivo
                                                showCancelDialog = true
                                            } else {
                                                // Para cualquier otro estado, llama al VM directamente
                                                viewModel.cambiarEstado(estadoOpcion)
                                            }
                                        }
                                    ) // Fin DropdownMenuItem
                                } // Fin forEach
                            } // Fin ExposedDropdownMenu
                        } // Fin ExposedDropdownMenuBox
                    } // Fin else (muestra Dropdown)

                    Spacer(modifier = Modifier.height(16.dp)) // Espacio final
                } // Fin Column principal
            } // Fin else (muestra detalles)
        } // Fin Box contenedor
    } // Fin Scaffold

    // --- Diálogo para Motivo de Cancelación ---
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
                cancelReason = "" // Limpia el motivo
            },
            title = { Text("Cancelar Pedido") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    label = { Text("Motivo de cancelación (*)") },
                    isError = uiState.actionError?.contains("motivo") == true // Marca error si VM lo indica
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarEstado(EstadoPedido.CANCELADO, cancelReason)
                        showCancelDialog = false // Cierra el diálogo
                        cancelReason = "" // Limpia el motivo
                    },
                    // Deshabilita si no hay motivo o si ya está cargando
                    enabled = cancelReason.isNotBlank() && !uiState.isUpdatingStatus
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    cancelReason = ""
                }) {
                    Text("Volver")
                }
            }
        )
    }
}

// --- Composable DetailItem (sin cambios) ---
@Composable
private fun DetailItem(label: String, value: String, valueColor: Color = Color.Black, isBoldValue: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 14.sp, color = valueColor, fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Normal)
    }
}

// --- Composable ActionButtonAdmin (YA NO SE USA DIRECTAMENTE, pero puedes dejarlo por si acaso) ---
@Composable
private fun ActionButtonAdmin(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF2D1B4E)
) { /* ... (código sin cambios) ... */ }


// --- Función Helper formatDateTime (sin cambios) ---
private fun formatDateTime(dateTimeString: String?): String {
    if (dateTimeString == null) return "-"
    try {
        val parsedDateTime = java.time.LocalDateTime.parse(dateTimeString)
        return parsedDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    } catch (e: Exception) {
        return dateTimeString
    }
}

// --- Función Helper estadoToString (Añadida, igual que en PedidosScreen) ---
private fun estadoToString(estado: EstadoPedido?): String {
    return estado?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "N/A"
}