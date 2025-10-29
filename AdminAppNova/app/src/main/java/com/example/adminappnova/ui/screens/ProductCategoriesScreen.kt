package com.example.adminappnova.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // Importar KeyboardType
// --- CORREGIDO: Importación correcta ---
import androidx.compose.ui.text.style.TextAlign
// ------------------------------------
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.adminappnova.R
import com.example.adminappnova.ui.viewmodel.ProductDetailUiState // <-- Importar UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCategoriesScreen(
    navController: NavController,
    categoryName: String,               // <-- Nombre de la categoría (para UI)
    uiState: ProductDetailUiState,      // <-- Recibe estado del VM
    // --- Recibe TODOS los eventos del VM ---
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,   // <-- Nombre corregido
    onPrecioChange: (String) -> Unit,
    onCostoChange: (String) -> Unit,      // <-- Evento añadido
    onCantidadPuntosChange: (String) -> Unit, // <-- Evento añadido
    // onAddImageClick: () -> Unit,      // <-- Si manejas imágenes
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit
    // -------------------------------------
) {
    // Ya no necesitas: var nombre by remember... etc.

    // Efecto para navegar atrás
    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        if (uiState.saveSuccess || uiState.deleteSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (uiState.product == null) "Nuevo Producto" else "Editar Producto", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Volver", tint = Color.Black) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            // Contenido del formulario (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Campos del formulario (Nombre, Descripción, Cantidad, Costo, Precio, Puntos)
                FormTextField(label = "Nombre (*)", value = uiState.nombre, onValueChange = onNombreChange, isError = uiState.error?.contains("Nombre") == true)
                FormTextField(label = "Descripción", value = uiState.descripcion, onValueChange = onDescripcionChange, modifier = Modifier.height(100.dp), singleLine = false)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(label = "Cantidad (*)", value = uiState.cantidad, onValueChange = onCantidadChange, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), isError = uiState.error?.contains("Cantidad") == true || uiState.error?.contains("Stock") == true)
                    FormTextField(label = "Costo (*)", value = uiState.costo, onValueChange = onCostoChange, keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f), isError = uiState.error?.contains("Costo") == true)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(label = "Precio (*)", value = uiState.precio, onValueChange = onPrecioChange, keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f), isError = uiState.error?.contains("Precio") == true)
                    FormTextField(label = "Puntos (*)", value = uiState.cantidadPuntos, onValueChange = onCantidadPuntosChange, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f), isError = uiState.error?.contains("Puntos") == true)
                }

                // Sección Imagen
                Spacer(modifier = Modifier.height(16.dp))
                Text("Imagen", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { /* TODO: onAddImageClick() */ }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)), shape = RoundedCornerShape(8.dp), enabled = !uiState.isSaving && !uiState.isDeleting) {
                        Text("Añadir/Cambiar", color = Color.Gray, fontSize = 12.sp)
                    }
                    Card(modifier = Modifier.size(100.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
                        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(uiState.product?.imagen ?: R.drawable.ic_producto).crossfade(true).error(R.drawable.ic_producto).placeholder(R.drawable.ic_producto).build(), contentDescription = "Previsualización", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje de Error
                uiState.error?.let { error ->
                    Text(text = error, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(), textAlign = TextAlign.Center) // <-- Usa TextAlign importado
                }

                // Botones de Acción
                if (uiState.product != null) { // Botón Eliminar
                    Button(onClick = onDeleteClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)), shape = RoundedCornerShape(8.dp), enabled = !uiState.isSaving && !uiState.isDeleting) {
                        if (uiState.isDeleting) { CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp) } else { Text("Eliminar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF801F)), shape = RoundedCornerShape(8.dp), enabled = !uiState.isSaving && !uiState.isDeleting) { // Botón Guardar
                    if (uiState.isSaving) { CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp) } else { Text("Guardar cambios", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } // Fin Column
        } // Fin else
    } // Fin Scaffold
}

// --- FormTextField (sin cambios) ---
@Composable
private fun FormTextField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, singleLine: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text, isError: Boolean = false) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (isError) Color.Red else Color.Gray, unfocusedBorderColor = if (isError) Color.Red else Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, errorBorderColor = Color.Red), shape = RoundedCornerShape(8.dp), singleLine = singleLine, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), isError = isError)
    }
}