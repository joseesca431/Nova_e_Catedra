package com.example.adminappnova.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoItemDto
import com.example.adminappnova.ui.viewmodel.OrderDetailUiState
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

// Colores coherentes
private val PrimaryPurple = Color(0xFF2D1B4E)
private val AccentOrange = Color(0xFFFF6B35)
private val BackgroundLight = Color(0xFFF8F9FA)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    uiState: OrderDetailUiState,
    viewModel: com.example.adminappnova.ui.viewmodel.OrderDetailViewModel
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F0FF))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = PrimaryPurple
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Detalles del Pedido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        // 🖼️ MARCO EXTERIOR (borde alrededor de todo el contenido)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(Color.White, shape = RoundedCornerShape(24.dp))
        ) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryPurple,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Cargando detalles...",
                            color = Color(0xFF757575),
                            fontSize = 14.sp
                        )
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            uiState.error,
                            color = Color(0xFFC62828),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                uiState.pedido == null -> {
                    Text(
                        "No se encontró el pedido",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF757575)
                    )
                }

                else -> {
                    val pedido = uiState.pedido

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // Información General
                        item {
                            ModernSectionCard(
                                title = "Información General",
                                icon = Icons.Default.Receipt
                            ) {
                                ModernDetailItem(label = "ID Pedido", value = pedido.idPedido.toString())
                                ModernDetailItem(label = "Fecha Creación", value = formatDateTime(pedido.fechaInicio))
                                ModernDetailItem(label = "Fecha Finalización", value = formatDateTime(pedido.fechaFinal))
                                ModernDetailItem(
                                    label = "Estado Actual",
                                    value = estadoToString(pedido.estado),
                                    valueColor = estadoColor(pedido.estado),
                                    isBold = true
                                )
                                ModernDetailItem(label = "Tipo de Pago", value = pedido.tipoPago?.name ?: "N/A")
                                ModernDetailItem(
                                    label = "Total",
                                    value = "$${pedido.total?.let { "%.2f".format(it) } ?: "0.00"}",
                                    isBold = true,
                                    isLarge = true
                                )
                                ModernDetailItem(label = "Puntos Otorgados", value = pedido.puntosTotales?.toString() ?: "-")
                            }
                        }

                        // Información del Cliente
                        item {
                            ModernSectionCard(
                                title = "Cliente",
                                icon = Icons.Default.Person
                            ) {
                                val usuario = uiState.usuario
                                if (usuario != null) {
                                    // ✅ Nombre eliminado, como solicitaste
                                    ModernDetailItem(label = "Cliente ID", value = usuario.idUser.toString())
                                    ModernDetailItem(label = "Email", value = usuario.email ?: "N/A")
                                    ModernDetailItem(label = "Usuario", value = usuario.username ?: "N/A")
                                } else {
                                    Text("Cargando...", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }

                        // Información de Envío
                        item {
                            ModernSectionCard(
                                title = "Dirección de Envío",
                                icon = Icons.Default.LocalShipping
                            ) {
                                ModernDetailItem(label = "Alias", value = pedido.aliasDireccion ?: "-")
                                ModernDetailItem(
                                    label = "Dirección",
                                    value = "${pedido.calleDireccion ?: "-"}, ${pedido.ciudadDireccion ?: "-"}, ${pedido.departamentoDireccion ?: "-"}"
                                )
                            }
                        }

                        // Productos del Pedido
                        item {
                            ModernSectionCard(
                                title = "Productos",
                                icon = Icons.Default.Receipt
                            ) {
                                if (uiState.pedidoItems.isEmpty()) {
                                    Text("No hay productos en este pedido.", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }

                        items(uiState.pedidoItems, key = { it.idProducto }) { item ->
                            ProductoItemRow(item = item)
                        }

                        // Actualizar Estado
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        "Actualizar Estado",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryPurple
                                    )

                                    uiState.actionError?.let { error ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFFEBEE)
                                        ) {
                                            Text(
                                                error,
                                                color = Color(0xFFC62828),
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }

                                    val isFinalState = pedido.estado == EstadoPedido.ENTREGADO || pedido.estado == EstadoPedido.CANCELADO

                                    if (uiState.isUpdatingStatus) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = PrimaryPurple,
                                                strokeWidth = 3.dp
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text("Actualizando estado...", color = Color(0xFF757575))
                                        }
                                    } else {
                                        ExposedDropdownMenuBox(
                                            expanded = isDropdownExpanded && !isFinalState,
                                            onExpandedChange = {
                                                if (!isFinalState) isDropdownExpanded = it
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = estadoToString(pedido.estado),
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Cambiar estado a:") },
                                                trailingIcon = {
                                                    Icon(
                                                        imageVector = if (isDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                                        contentDescription = "Desplegar",
                                                        tint = if (isFinalState) Color.Gray else PrimaryPurple
                                                    )
                                                },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    disabledContainerColor = Color(0xFFFAFAFA),
                                                    focusedBorderColor = PrimaryPurple,
                                                    unfocusedBorderColor = if (isFinalState) Color.LightGray else Color(0xFFE0E0E0),
                                                    disabledTextColor = if (isFinalState) Color.Gray else Color.Black,
                                                    disabledLabelColor = Color.Gray,
                                                    disabledTrailingIconColor = Color.Gray,
                                                    focusedLabelColor = PrimaryPurple
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                enabled = !isFinalState
                                            )

                                            ExposedDropdownMenu(
                                                expanded = isDropdownExpanded && !isFinalState,
                                                onDismissRequest = { isDropdownExpanded = false },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                viewModel.estadosSeleccionables.forEach { estadoOpcion ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                Surface(
                                                                    shape = CircleShape,
                                                                    color = estadoColor(estadoOpcion).copy(alpha = 0.2f),
                                                                    modifier = Modifier.size(8.dp)
                                                                ) {}
                                                                Text(estadoToString(estadoOpcion))
                                                            }
                                                        },
                                                        onClick = {
                                                            isDropdownExpanded = false
                                                            if (estadoOpcion == EstadoPedido.CANCELADO) {
                                                                showCancelDialog = true
                                                            } else {
                                                                viewModel.cambiarEstado(estadoOpcion)
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de cancelación
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
                cancelReason = ""
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    "Cancelar Pedido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            },
            text = {
                Column {
                    Text(
                        "Por favor, indica el motivo de la cancelación",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Motivo de cancelación *") },
                        isError = uiState.actionError?.contains("motivo") == true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            focusedLabelColor = PrimaryPurple
                        ),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarEstado(EstadoPedido.CANCELADO, cancelReason)
                        showCancelDialog = false
                        cancelReason = ""
                    },
                    enabled = cancelReason.isNotBlank() && !uiState.isUpdatingStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirmar cancelación", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        cancelReason = ""
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Volver", color = Color(0xFF757575))
                }
            }
        )
    }
}

// ============ COMPONENTES MODERNOS ============

@Composable
private fun ModernSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            content()
        }
    }
}

@Composable
private fun ModernDetailItem(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1A1A1A),
    isBold: Boolean = false,
    isLarge: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = if (isLarge) 20.sp else 15.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ✅ LISTA DE PRODUCTOS MEJORADA (como en mi versión anterior)
@Composable
private fun ProductoItemRow(item: PedidoItemDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nombreProducto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryPurple,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.cantidad} unidad(es)",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = formatCurrency(item.precioUnitario),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
        }
    }
}

// ============ UTILS ============

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDateTime(dateTimeString: String?): String {
    if (dateTimeString == null) return "-"
    try {
        val parsedDateTime = java.time.LocalDateTime.parse(dateTimeString)
        return parsedDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    } catch (e: Exception) {
        return dateTimeString
    }
}

private fun estadoToString(estado: EstadoPedido?): String {
    return estado?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    } ?: "N/A"
}

private fun estadoColor(estado: EstadoPedido?): Color {
    return when (estado?.name?.uppercase(Locale.getDefault())) {
        "PENDIENTE" -> Color(0xFFFFA726)
        "PAGADO" -> Color(0xFF66BB6A)
        "EN_PROCESO", "ENPROCESO", "EN PROCESO" -> Color(0xFFAB47BC)
        "ENVIADO" -> Color(0xFF29B6F6)
        "ENTREGADO" -> Color(0xFF2E7D32)
        "CANCELADO" -> Color(0xFFEF5350)
        else -> Color.Gray
    }
}

private fun formatCurrency(value: BigDecimal?): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "SV")).format(value ?: BigDecimal.ZERO)
}