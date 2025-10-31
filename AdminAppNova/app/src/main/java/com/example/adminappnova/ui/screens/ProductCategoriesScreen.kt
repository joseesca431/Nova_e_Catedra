package com.example.adminappnova.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.adminappnova.R
import com.example.adminappnova.ui.viewmodel.ProductDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCategoriesScreen(
    navController: NavController,
    categoryName: String,
    uiState: ProductDetailUiState,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onCostoChange: (String) -> Unit,
    onCantidadPuntosChange: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        if (uiState.saveSuccess || uiState.deleteSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.product == null) "Nuevo Producto" else "Editar Producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D1B4E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF2D1B4E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2D1B4E))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Nombre
                ModernFormTextField(
                    label = "Nombre (*)",
                    value = uiState.nombre,
                    onValueChange = onNombreChange,
                    isError = uiState.error?.contains("Nombre") == true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                ModernFormTextField(
                    label = "Descripción",
                    value = uiState.descripcion,
                    onValueChange = onDescripcionChange,
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cantidad y Costo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernFormTextField(
                        label = "Cantidad (*)",
                        value = uiState.cantidad,
                        onValueChange = onCantidadChange,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        isError = uiState.error?.contains("Cantidad") == true || uiState.error?.contains("Stock") == true
                    )
                    ModernFormTextField(
                        label = "Costo (*)",
                        value = uiState.costo,
                        onValueChange = onCostoChange,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f),
                        isError = uiState.error?.contains("Costo") == true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Precio y Puntos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernFormTextField(
                        label = "Precio (*)",
                        value = uiState.precio,
                        onValueChange = onPrecioChange,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f),
                        isError = uiState.error?.contains("Precio") == true
                    )
                    ModernFormTextField(
                        label = "Puntos (*)",
                        value = uiState.cantidadPuntos,
                        onValueChange = onCantidadPuntosChange,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        isError = uiState.error?.contains("Puntos") == true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Imagen
                Text(
                    text = "Imagen del producto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D1B4E)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.size(110.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uiState.product?.imagen ?: R.drawable.ic_producto)
                                .crossfade(true)
                                .error(R.drawable.ic_producto)
                                .placeholder(R.drawable.ic_producto)
                                .build(),
                            contentDescription = "Previsualización",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Si en el futuro quieres habilitar edición de imagen, descomenta esto:
                    /*
                    Button(
                        onClick = { /* onAddImageClick() */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D1B4E).copy(alpha = 0.1f),
                            contentColor = Color(0xFF2D1B4E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isSaving && !uiState.isDeleting
                    ) {
                        Text("Cambiar imagen", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                     */
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje de error (estilo moderno)
                uiState.error?.let { error ->
                    ErrorCard(error = error)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botones de acción
                if (uiState.product != null) {
                    Button(
                        onClick = onDeleteClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !uiState.isSaving && !uiState.isDeleting
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("Eliminar producto", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1B4E)),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !uiState.isSaving && !uiState.isDeleting
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            if (uiState.product == null) "Crear producto" else "Guardar cambios",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ModernFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF2D1B4E),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color(0xFFFF6B35) else Color(0xFF2D1B4E),
                unfocusedBorderColor = if (isError) Color(0xFFFF6B35).copy(alpha = 0.6f) else Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF2D1B4E),
                unfocusedLabelColor = Color.Gray,
                errorBorderColor = Color(0xFFFF6B35)
            ),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else maxLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError
        )
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
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6B35).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚠", fontSize = 16.sp, color = Color(0xFFFF6B35))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = error,
                color = Color(0xFFC62828),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}