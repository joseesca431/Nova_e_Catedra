package com.example.adminappnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // 👈 Importar items correcto
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // <-- Asegúrate de tener este import
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminappnova.R
import com.example.adminappnova.data.dto.CategoryResponse // 👈 Importar DTO
import com.example.adminappnova.ui.viewmodel.CategoriesUiState // 👈 Importar UiState

@Composable
fun CategoriesScreen(
    navController: NavController,
    uiState: CategoriesUiState, // 👈 Recibe estado del ViewModel
    onAddCategoryClick: () -> Unit, // 👈 Evento para FAB del ViewModel
    // Eventos para el diálogo desde el ViewModel
    onDismissAddDialog: () -> Unit,
    // El nombre del parámetro aquí puede ser 'onNewCategoryNameChange', pero llama a la función correcta del VM
    onNewCategoryNameChange: (String) -> Unit, // Renombrado para claridad en UI, mapea a onNewCategoryTypeChange en VM
    onConfirmAddCategory: () -> Unit
) {
    // selectedTab sigue siendo local para controlar la BottomBar
    var selectedTab by remember { mutableStateOf("Categorías") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // Item Home
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_home), "Home", Modifier.size(24.dp)) },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = selectedTab == "Home",
                    onClick = {
                        selectedTab = "Home"
                        navController.navigate("start") { popUpTo("start") { inclusive = false }; launchSingleTop = true }
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                // Item Categorías
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_categorias), "Categorías", Modifier.size(24.dp)) },
                    label = { Text("Categorías", fontSize = 10.sp) },
                    selected = selectedTab == "Categorías",
                    onClick = { selectedTab = "Categorías" /* Ya estás aquí */ },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                // Item Pedidos
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.ic_pedido), "Pedidos", Modifier.size(24.dp)) },
                    label = { Text("Pedidos", fontSize = 10.sp) },
                    selected = selectedTab == "Pedidos",
                    onClick = {
                        selectedTab = "Pedidos"
                        navController.navigate("pedidos") { popUpTo("start") { inclusive = false }; launchSingleTop = true }
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF2D1B4E), selectedTextColor = Color(0xFF2D1B4E), indicatorColor = Color.Transparent, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategoryClick, // Llama al evento del ViewModel para mostrar diálogo
                containerColor = Color(0xFFFF801F), // Naranja
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar categoría",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Fondo gris claro
                .padding(paddingValues) // Padding de Scaffold
                .padding(horizontal = 16.dp), // Padding horizontal
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espacio superior

            // Título "Administración"
            Text(
                text = "Administración",
                fontSize = 20.sp,
                color = Color(0xFFFF6B35), // Naranja
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Sección de Lista de Categorías (Condicional) ---
            if (uiState.isLoading) {
                // Muestra indicador si está cargando
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else if (uiState.categories.isEmpty() && uiState.error == null) {
                // Muestra mensaje si la lista está vacía (y no hay error)
                Text(
                    "No hay categorías para mostrar.\nPresiona '+' para agregar una.",
                    modifier = Modifier.padding(top = 50.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            } else {
                // Muestra la lista si hay categorías
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Ocupa el espacio restante
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre items
                ) {
                    items(uiState.categories, key = { it.idTipoProducto }) { category -> // Usa el ID como key
                        CategoryItem(
                            category = category, // Pasa el objeto CategoryResponse
                            navController = navController
                        )
                    }
                }
            }

            // Muestra mensaje de error de carga si existe
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } // Fin Column principal

        // --- Diálogo para Agregar Categoría ---
        if (uiState.showAddDialog) {
            AddCategoryDialog(
                // --- CORREGIDO AQUÍ ---
                newCategoryName = uiState.newCategoryType, // Pasa el valor correcto 'newCategoryType' del state
                // --------------------
                isAdding = uiState.isAdding,
                addError = uiState.addError,
                onDismiss = onDismissAddDialog,
                onNameChange = onNewCategoryNameChange, // Llama a la función correcta (que actualiza newCategoryType en VM)
                onConfirm = onConfirmAddCategory
            )
        }
    } // Fin Scaffold
}

// --- Composable para cada item de la lista ---
@Composable
fun CategoryItem(
    category: CategoryResponse, // Recibe el objeto DTO
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                // --- CORREGIDO AQUÍ ---
                navController.navigate("categories_detail/${category.tipo}") // Usa el campo 'tipo' para la ruta
                // --------------------
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Fondo blanco para la tarjeta
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Sin sombra por defecto
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border( // Borde morado
                    width = 2.dp,
                    color = Color(0xFF2D1B4E),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp), // Padding interno
            contentAlignment = Alignment.Center // Centra el texto
        ) {
            Text(
                // --- CORREGIDO AQUÍ ---
                text = category.tipo, // Muestra el campo 'tipo' del DTO
                // --------------------
                fontSize = 16.sp,
                color = Color(0xFF2D1B4E), // Texto morado
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- Composable para el Diálogo de Agregar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    newCategoryName: String, // El nombre del parámetro aquí sigue siendo 'Name' por claridad en la UI
    isAdding: Boolean,
    addError: String?,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit, // Esta función recibe el String del TextField
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss, // Se cierra si se toca fuera
        title = { Text("Agregar Nueva Categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = newCategoryName, // Usa el valor recibido (que viene de uiState.newCategoryType)
                    onValueChange = onNameChange, // Llama a la función recibida (que llama a onNewCategoryTypeChange en VM)
                    label = { Text("Nombre de la categoría") }, // El label puede seguir siendo "Nombre"
                    isError = addError != null, // Marca en rojo si hay error
                    singleLine = true, // Campo de una sola línea
                    modifier = Modifier.fillMaxWidth()
                )
                // Muestra el mensaje de error debajo del campo si existe
                addError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error, // Color de error del tema
                        style = MaterialTheme.typography.bodySmall, // Texto pequeño
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isAdding) { // Deshabilita mientras se agrega
                if (isAdding) {
                    // Muestra indicador de carga en el botón
                    CircularProgressIndicator(
                        Modifier.size(18.dp),
                        color = LocalContentColor.current, // Color del texto del botón
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Agregar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}