package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
// --- 👇 AÑADE ESTAS IMPORTACIONES DE ICONOS 👇 ---
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp

import androidx.compose.material.icons.filled.Check


// --- ------------------------------------- ---
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf // Importa derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.flow.collect // Importa collect
// Ya no necesitamos snapshotFlow

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
    val filterOptions = listOf<EstadoPedido?>(null) + EstadoPedido.values()

    // --- State para el Dropdown ---
    var isDropdownExpanded by remember { mutableStateOf(false) }
    // ----------------------------

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) { // Abre llaves NavigationBar
                // Item Home
                NavigationBarItem(
                    selected = selectedTab == "Home",
                    onClick = {
                        selectedTab = "Home"
                        navController.navigate("start") { popUpTo("start") { inclusive = false }; launchSingleTop = true }
                    },
                    icon = { Icon(painterResource(id = R.drawable.ic_home), "Home", Modifier.size(24.dp)) },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                // Item Categorías
                NavigationBarItem(
                    selected = selectedTab == "Categorías",
                    onClick = {
                        selectedTab = "Categorías"
                        navController.navigate("categories") { popUpTo("start") { inclusive = false }; launchSingleTop = true }
                    },
                    icon = { Icon(painterResource(id = R.drawable.ic_categorias), "Categorías", Modifier.size(24.dp)) },
                    label = { Text("Categorías", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                // Item Pedidos
                NavigationBarItem(
                    selected = selectedTab == "Pedidos",
                    onClick = { selectedTab = "Pedidos" },
                    icon = { Icon(painterResource(id = R.drawable.ic_pedido), "Pedidos", Modifier.size(24.dp)) },
                    label = { Text("Pedidos", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
            } // Cierra llaves NavigationBar
        } // Cierra bottomBar
    ) { paddingValues -> // Abre contenido Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues), // Padding solo del Scaffold aquí
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Administración de Pedidos",
                fontSize = 20.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            // --- MENÚ DESPLEGABLE PARA FILTROS ---
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // --- TextField (CORREGIDO CON ICONOS) ---
                    OutlinedTextField(
                        value = estadoToString(uiState.filtroEstado),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por estado") },
                        trailingIcon = {
                            // --- 👇 USA LOS ICONOS IMPORTADOS 👇 ---
                            Icon(
                                imageVector = if (isDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = if (isDropdownExpanded) "Cerrar menú" else "Abrir menú"
                            )
                            // --- -------------------------------- ---
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF2D1B4E),
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    ) // Fin OutlinedTextField

                    // --- Menú (sin cambios) ---
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filterOptions.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estadoToString(estado)) },
                                onClick = {
                                    onChangeFilter(estado)
                                    isDropdownExpanded = false
                                },
                                colors = if (uiState.filtroEstado == estado) MenuDefaults.itemColors(textColor = Color(0xFF2D1B4E)) else MenuDefaults.itemColors()
                            )
                        }
                    } // Fin ExposedDropdownMenu
                } // Fin ExposedDropdownMenuBox
            } // Fin Box dropdown
            Spacer(modifier = Modifier.height(16.dp))
            // --- ----------------------------------- ---

            // SwipeRefresh y Lista (sin cambios)
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading && uiState.pedidosFiltrados.isNotEmpty())
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = onRefresh,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading && uiState.pedidosFiltrados.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (uiState.pedidosFiltrados.isEmpty() && uiState.error == null && !uiState.isLoading) {
                        Text(
                            text = if (uiState.filtroEstado == null) "No hay pedidos." else "No hay pedidos con estado '${estadoToString(uiState.filtroEstado)}'.",
                            modifier = Modifier.align(Alignment.Center).padding(horizontal=16.dp),
                            textAlign = TextAlign.Center, color = Color.Gray
                        )
                    } else {
                        // Lista de Pedidos
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = if (uiState.isLoadingMore || uiState.canLoadMore) 40.dp else 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.pedidosFiltrados, key = { it.idPedido }) { pedido ->
                                PedidoCard(
                                    pedido = pedido,
                                    onClick = { navController.navigate("detalles_pago/${pedido.idPedido}") }
                                )
                            }
                            // Indicador Carga Más / Fin Lista
                            item {
                                if (uiState.isLoadingMore) {
                                    Row(Modifier.fillMaxWidth().padding(vertical=8.dp), Arrangement.Center) { CircularProgressIndicator(Modifier.size(24.dp)) }
                                } else if (!uiState.canLoadMore && uiState.pedidosFiltrados.isNotEmpty()) {
                                    Text("Fin de la lista", Modifier.fillMaxWidth().padding(vertical=8.dp), textAlign=TextAlign.Center, color=Color.Gray, fontSize=12.sp)
                                }
                            }
                        } // Fin LazyColumn

                        // Lógica Carga Más con derivedStateOf
                        val endOfListReached by remember {
                            derivedStateOf {
                                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 6
                            }
                        }
                        LaunchedEffect(endOfListReached) {
                            if (endOfListReached && uiState.canLoadMore && !uiState.isLoadingMore && !uiState.isLoading) {
                                onLoadNextPage()
                            }
                        } // Fin LaunchedEffect
                    } // Fin else (muestra lista)

                    // Snackbar de Error
                    uiState.error?.let { error ->
                        Snackbar( Modifier.align(Alignment.BottomCenter).padding(16.dp), action = { Button(onClick = onRefresh) { Text("Reintentar") } } ) { Text(text = error) }
                    }
                } // Fin Box Contenido Principal
            } // Fin SwipeRefresh
        } // Fin Column principal
    } // Fin Scaffold
}

// --- PedidoCard (sin cambios) ---
@Composable
fun PedidoCard(pedido: PedidoResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Text("Pedido #${pedido.idPedido}", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(pedido.fechaInicio?.substringBefore('T') ?: "-", fontSize = 10.sp, color = Color.LightGray)
            }
            Text("Cliente ID: ${pedido.idCarrito ?: "N/A"}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Normal)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
                Text(pedido.estado?.name ?: "N/A", fontSize = 12.sp, color = estadoColor(pedido.estado), fontWeight = FontWeight.Medium)
                Text("$${pedido.total?.let { "%.2f".format(it) } ?: "0.00"}", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- estadoColor (sin cambios) ---
@Composable
fun estadoColor(estado: EstadoPedido?): Color {
    return when (estado) {
        EstadoPedido.PENDIENTE -> Color(0xFFFFA726) // Naranja
        EstadoPedido.CONFIRMADO -> Color(0xFF42A5F5) // Azul
        EstadoPedido.PAGADO -> Color(0xFF66BB6A)     // Verde
        EstadoPedido.EN_ENVIO -> Color(0xFF29B6F6)   // Celeste
        EstadoPedido.ENTREGADO -> Color(0xFFBDBDBD)  // Gris
        EstadoPedido.CANCELADO -> Color(0xFFEF5350)  // Rojo
        else -> Color.Gray
    }
}

// --- estadoToString (sin cambios) ---
private fun estadoToString(estado: EstadoPedido?): String {
    return estado?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Todos"
}