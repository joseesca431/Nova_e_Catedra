package com.example.adminappnova.ui.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R
import com.example.adminappnova.data.dto.EstadoPedido
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.ui.viewmodel.PedidosUiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    navController: NavController,
    uiState: PedidosUiState,
    onLoadNextPage: () -> Unit,
    onRefresh: () -> Unit,
    onChangeFilter: (EstadoPedido?) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Pedidos") }
    val listState = rememberLazyListState()
    val filterOptions = listOf<EstadoPedido?>(null) + EstadoPedido.values().filter { it != EstadoPedido.CARRITO }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            ModernNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "Home" -> navController.navigate("start") {
                            popUpTo("start") { inclusive = false }
                            launchSingleTop = true
                        }
                        "Categorías" -> navController.navigate("categories") {
                            popUpTo("start") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Administración de Pedidos",
                fontSize = 24.sp,
                color = Color(0xFF2D1B4E),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dropdown moderno
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = estadoToString(uiState.filtroEstado),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                "Filtrar por estado",
                                color = Color(0xFF757575)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                if (isDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Abrir/Cerrar menú",
                                tint = Color(0xFF2D1B4E)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2D1B4E),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF2D1B4E),
                            unfocusedLabelColor = Color(0xFF757575)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                    ) {
                        filterOptions.forEach { estado ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = estadoToString(estado),
                                        color = if (uiState.filtroEstado == estado) Color(0xFF2D1B4E) else Color.Black,
                                        fontWeight = if (uiState.filtroEstado == estado) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onChangeFilter(estado)
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading && uiState.pedidosFiltrados.isNotEmpty())
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading && uiState.pedidosFiltrados.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2D1B4E))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando pedidos...", color = Color.Gray)
                        }
                    } else if (uiState.pedidosFiltrados.isEmpty() && uiState.error == null && !uiState.isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pedido),
                                contentDescription = null,
                                tint = Color(0xFFE0E0E0),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.filtroEstado == null) "No hay pedidos aún." else "No hay pedidos con estado '${estadoToString(uiState.filtroEstado)}'.",
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(uiState.pedidosFiltrados, key = { it.idPedido }) { pedido ->
                                PedidoCard(pedido = pedido) {
                                    if (pedido.idUser != null) {
                                        navController.navigate("detalles_pago/${pedido.idPedido}/${pedido.idUser}")
                                    } else {
                                        Log.e("PedidosScreen", "CRÍTICO: idUser es nulo para el pedido ${pedido.idPedido}.")
                                    }
                                }
                            }
                            item {
                                if (uiState.isLoadingMore) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color(0xFF2D1B4E)
                                        )
                                    }
                                } else if (!uiState.canLoadMore && uiState.pedidosFiltrados.isNotEmpty()) {
                                    Text(
                                        "Fin de la lista",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Cargar más al llegar al final
                        val endOfListReached by remember {
                            derivedStateOf {
                                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                lastVisible != null && lastVisible.index >= listState.layoutInfo.totalItemsCount - 5
                            }
                        }
                        LaunchedEffect(endOfListReached) {
                            if (endOfListReached && uiState.canLoadMore && !uiState.isLoadingMore && !uiState.isLoading) {
                                onLoadNextPage()
                            }
                        }
                    }

                    // Error moderno
                    uiState.error?.let { error ->
                        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                            ErrorCard(error = error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============ COMPONENTES MODERNOS ============

@Composable
fun PedidoCard(pedido: PedidoResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Pedido #${pedido.idPedido}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1B4E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cliente ID: ${pedido.idUser ?: "N/A"}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = pedido.fechaInicio.substringBefore('T'),
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = estadoColor(pedido.estado).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = pedido.estado?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "N/A",
                        fontSize = 12.sp,
                        color = estadoColor(pedido.estado),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "$${pedido.total?.let { "%.2f".format(it) } ?: "0.00"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1B4E)
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
            Text(
                text = error,
                color = Color(0xFFC62828),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============ MODERN NAVIGATION BAR (igual que en HomeScreen) ============

@Composable
private fun ModernNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                Triple("Home", R.drawable.ic_home, "Home"),
                Triple("Categorías", R.drawable.ic_categorias, "Categorías"),
                Triple("Pedidos", R.drawable.ic_pedido, "Pedidos")
            ).forEach { (label, icon, tab) ->
                ModernNavItem(
                    label = label,
                    icon = icon,
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun ModernNavItem(
    label: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF2D1B4E) else Color.Gray,
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color(0xFFF5F0FF) else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = animatedColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = animatedColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ============ UTILS ============

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

private fun estadoToString(estado: EstadoPedido?): String {
    return estado?.name
        ?.replace("_", " ")
        ?.lowercase()
        ?.replaceFirstChar { it.titlecase() }
        ?: "Todos"
}