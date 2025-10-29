package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.* // Necesario para @Composable, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.res.painterResource // No se usa en este archivo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// import com.example.adminappnova.R // No se usa R directamente
import com.example.adminappnova.data.dto.EstadoPedido // Importar Enum EstadoPedido
import com.example.adminappnova.ui.viewmodel.OrderDetailUiState // <-- Importar UiState
// --- CORREGIDO: Importa la función pública desde donde esté definida ---
import com.example.adminappnova.ui.screens.estadoColor // Importa desde PedidosScreen.kt (o ui.utils si la moviste)
// -----------------------------------------------------------------
import java.time.format.DateTimeFormatter // Para formatear fechas (si usas java.time)
import java.time.format.FormatStyle // Para formatear fechas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesPagoScreen(
    navController: NavController,
    uiState: OrderDetailUiState,      // <-- Recibe estado del VM
    // --- Recibe eventos del VM ---
    onConfirmarPedido: () -> Unit,
    onIniciarEnvio: () -> Unit,
    onMarcarEntregado: () -> Unit
    // onCancelarPedido: (String) -> Unit // <-- Si añades cancelación con diálogo
    // ----------------------------
) {
    // Variable local para el diálogo de cancelación (si se implementa)
    // var showCancelDialog by remember { mutableStateOf(false) }
    // var cancelReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // Muestra ID del pedido o un título genérico si aún no carga
                        text = uiState.pedido?.let { "Pedido #${it.idPedido}" } ?: "Detalles del Pedido",
                        fontSize = 18.sp,
                        color = Color(0xFF2D1B4E), // Color Morado
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    // Botón para volver a la pantalla anterior
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White) // Fondo blanco
            )
        }
    ) { paddingValues ->
        // Contenedor principal para manejar estados de carga/error/contenido
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) { // Aplica padding del Scaffold

            // --- Estado de Carga Inicial ---
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // --- Estado de Error Principal ---
            else if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Error al cargar el pedido:",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        uiState.error,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Botón para reintentar la carga (asume que tienes la función en VM)
                    Button(onClick = { /* TODO: Llamar a viewModel.loadOrderDetails() */ }) {
                        Text("Reintentar")
                    }
                }
            }
            // --- Estado sin Pedido (inesperado si no hubo error) ---
            else if (uiState.pedido == null) {
                Text(
                    "No se encontraron detalles para este pedido.",
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center, color = Color.Gray
                )
            }
            // --- Estado con Pedido Cargado: Muestra Detalles ---
            else {
                val pedido = uiState.pedido // Acceso más corto para no repetir uiState.pedido

                // Columna principal scrollable para los detalles
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White) // Fondo blanco para el contenido
                        .padding(horizontal = 24.dp, vertical = 16.dp) // Padding interno
                        .verticalScroll(rememberScrollState()), // Permite scroll
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre secciones de detalles
                ) {
                    // --- Sección: Información General ---
                    Text("Información General", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))
                    DetailItem(label = "ID Pedido", value = pedido.idPedido.toString())
                    DetailItem(label = "Fecha Creación", value = formatDateTime(pedido.fechaInicio))
                    DetailItem(label = "Fecha Finalización", value = formatDateTime(pedido.fechaFinal)) // Muestra "-" si es null
                    // Llama a la función 'estadoColor' importada para el color
                    DetailItem(label = "Estado Actual", value = pedido.estado?.name ?: "N/A", valueColor = estadoColor(pedido.estado))
                    DetailItem(label = "Tipo de Pago", value = pedido.tipoPago?.name ?: "N/A")
                    DetailItem(label = "Total", value = "$${"%.2f".format(pedido.total)}", isBoldValue = true)
                    DetailItem(label = "Puntos Otorgados", value = pedido.puntosTotales?.toString() ?: "-")

                    Divider(modifier = Modifier.padding(vertical = 8.dp)) // Separador

                    // --- Sección: Información de Envío ---
                    Text("Información de Envío", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))
                    DetailItem(label = "Alias", value = pedido.aliasDireccion ?: "-")
                    // Combina calle, ciudad y departamento en una sola línea
                    DetailItem(label = "Dirección", value = "${pedido.calleDireccion ?: "-"}, ${pedido.ciudadDireccion ?: "-"}, ${pedido.departamentoDireccion ?: "-"}")
                    // TODO: Mostrar Teléfono - Necesitaría añadirlo al PedidoResponse o hacer otra llamada para obtener datos del User
                    // DetailItem(label = "Teléfono Cliente", value = "...")

                    // TODO: Mostrar Productos del Pedido
                    // Esto requeriría que PedidoResponse incluyera una lista de DTOs de items del pedido,
                    // o hacer una llamada separada al backend para obtener los items de este pedidoId.
                    // Divider(modifier = Modifier.padding(vertical = 8.dp))
                    // Text("Productos", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))
                    // LazyColumn(...) // O Column si no son muchos

                    Divider(modifier = Modifier.padding(vertical = 8.dp)) // Separador

                    // --- Sección: Botones de Acción para Admin ---
                    Text("Acciones", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFF6B35))

                    // Muestra error específico de una acción si ocurrió
                    uiState.actionError?.let { error ->
                        Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // Fila para botones principales (Confirmar, Enviar, Entregar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre botones
                    ) {
                        // Muestra "Confirmar" solo si el estado es PENDIENTE
                        if (pedido.estado == EstadoPedido.PENDIENTE) {
                            ActionButtonAdmin(
                                text = "Confirmar",
                                onClick = onConfirmarPedido,
                                isLoading = uiState.isConfirming,
                                enabled = !uiState.isLoading, // Deshabilita si carga principal
                                modifier = Modifier.weight(1f) // Ocupa espacio equitativo
                            )
                        }
                        // Muestra "Enviar" si está PAGADO o CONFIRMADO (ajusta según tu lógica de negocio)
                        if (pedido.estado == EstadoPedido.PAGADO || pedido.estado == EstadoPedido.CONFIRMADO) {
                            ActionButtonAdmin(
                                text = "Marcar Enviado", // Texto más claro
                                onClick = onIniciarEnvio,
                                isLoading = uiState.isSending,
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Muestra "Entregar" si está EN_ENVIO
                        if (pedido.estado == EstadoPedido.EN_ENVIO) {
                            ActionButtonAdmin(
                                text = "Marcar Entregado", // Texto más claro
                                onClick = onMarcarEntregado,
                                isLoading = uiState.isDelivering,
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } // Fin Row botones principales

                    // Muestra "Cancelar" si el pedido NO está CANCELADO ni ENTREGADO
                    if (pedido.estado != EstadoPedido.CANCELADO && pedido.estado != EstadoPedido.ENTREGADO) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionButtonAdmin(
                            text = "Cancelar Pedido",
                            // TODO: Implementar diálogo para obtener motivo
                            // onClick = { showCancelDialog = true },
                            onClick = { /* Placeholder */ },
                            isLoading = uiState.isCancelling,
                            enabled = !uiState.isLoading,
                            containerColor = Color(0xFFD32F2F), // Color Rojo para acción destructiva
                            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Espacio final en el scroll
                } // Fin Column principal de detalles
            } // Fin else (muestra detalles)
        } // Fin Box contenedor
    } // Fin Scaffold
}

// --- Composable reutilizable para mostrar un par Label/Value ---
@Composable
private fun DetailItem(label: String, value: String, valueColor: Color = Color.Black, isBoldValue: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Etiqueta (Label) en gris y tamaño pequeño
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        // Valor (Value)
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor, // Permite cambiar color (ej: para el estado)
            fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Normal // Permite negrita (ej: para el total)
        )
    }
}

// --- Composable reutilizable para los botones de acción del admin ---
@Composable
private fun ActionButtonAdmin(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false, // Para mostrar indicador de carga
    enabled: Boolean = true, // Para deshabilitar el botón
    containerColor: Color = Color(0xFF2D1B4E) // Color Morado por defecto
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(45.dp), // Altura estándar para estos botones
        enabled = enabled && !isLoading, // Deshabilitado si no está enabled o si está cargando
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp) // Bordes redondeados
    ) {
        // Muestra indicador o texto según el estado isLoading
        if (isLoading) {
            CircularProgressIndicator(
                Modifier.size(18.dp), // Tamaño pequeño para el indicador dentro del botón
                color = Color.White, // Color blanco para contraste
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp, // Texto un poco más pequeño para botones de acción
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// --- Función Helper para formatear Fecha/Hora ---
// Se mantiene igual, asegúrate que las importaciones de java.time sean correctas
// o usa org.threeten.bp si usas la librería de compatibilidad.
private fun formatDateTime(dateTimeString: String?): String {
    if (dateTimeString == null) return "-" // Devuelve guion si la fecha es nula
    return try {
        // Intenta parsear como LocalDateTime (formato ISO por defecto)
        val parsedDateTime = java.time.LocalDateTime.parse(dateTimeString)
        // Formatea a un estilo MEDIUM para fecha y SHORT para hora (ej: 27 oct 2025 14:30)
        parsedDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    } catch (e: Exception) {
        // Si falla el parseo, devuelve el String original
        dateTimeString
    }
}