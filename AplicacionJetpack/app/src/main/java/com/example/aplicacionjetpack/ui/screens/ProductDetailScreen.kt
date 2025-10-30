package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// Se quita rememberScrollState y verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacionjetpack.R
// ¡IMPORTAR AuthManager DIRECTAMENTE!
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.ui.viewmodel.ProductDetailViewModel
import com.example.aplicacionjetpack.ui.viewmodel.ProductDetailUiState
// --- Imports Añadidos ---
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import kotlinx.coroutines.flow.collectLatest
// --- Fin de Imports Añadidos ---
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    // 1. Inyectar el CarritoViewModel
    carritoViewModel: CarritoViewModel = hiltViewModel()
) {
    // Estado local de cantidad
    var quantity by remember { mutableStateOf(1) } // Iniciar en 1 es más común

    // Observa el uiState del ViewModel
    val uiState = viewModel.uiState

    // Formulario de reseña: comentario y rating
    var comentarioState by remember { mutableStateOf(TextFieldValue("")) }
    var ratingState by remember { mutableStateOf(5.0) } // default 5.0
    var posting by remember { mutableStateOf(false) }

    // --- 2. Añadir estado para el Snackbar ---
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        carritoViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CarritoViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // Cargar datos al entrar o cuando cambie productId
    LaunchedEffect(productId) {
        if (productId != 0L) {
            viewModel.loadProductAndReviews(productId)
        }
    }

    Scaffold(
        // --- 3. Añadir el SnackbarHost al Scaffold ---
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.product?.nombre ?: "Detalle del producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface // Corregido
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface // Corregido
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Corregido
                )
            )
        },
        floatingActionButton = {
            // FAB para refrescar
            FloatingActionButton(onClick = { viewModel.refresh(productId) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
            }
        }
    ) { paddingValues ->

        // --- MANEJO DE ESTADOS DE PANTALLA COMPLETA ---
        if (uiState.isLoading && uiState.product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (uiState.error != null && uiState.product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = uiState.error ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        // --- CONTENIDO PRINCIPAL (usando LazyColumn) ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {

            // --- 1. IMAGEN DEL PRODUCTO ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    val imageUrl = uiState.product?.imagen
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = uiState.product?.nombre ?: "Producto",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        // Asegúrate que R.drawable.ic_producto exista
                        Image(
                            painter = painterResource(id = R.drawable.ic_producto),
                            contentDescription = "Producto",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // --- 2. INFORMACIÓN DEL PRODUCTO (tarjeta blanca) ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp)
                ) {
                    Text(
                        text = uiState.product?.nombre ?: "Nombre del producto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val priceText = uiState.product?.precio?.let { precio ->
                        try {
                            val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
                            nf.format(precio)
                        } catch (ex: Exception) {
                            "$${(uiState.product?.precio ?: BigDecimal.ZERO)}"
                        }
                    } ?: "$0.00"

                    Text(
                        text = priceText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary, // Corregido
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "DESCRIPCIÓN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = uiState.product?.descripcion
                            ?: "Breve descripción del producto, mostrando sus características y cualquier detalle más",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (quantity > 1) quantity-- // No permitir bajar de 1
                            },
                            modifier = Modifier.size(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary // Corregido
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "−",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary // Corregido
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { quantity++ },
                            modifier = Modifier.size(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary // Corregido
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "+",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary // Corregido
                            )
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // --- 4. Lógica del botón "AGREGAR" actualizada ---
                    Button(
                        onClick = {
                            if (quantity > 0) {
                                // Llama al CarritoViewModel para añadir el item
                                carritoViewModel.addItem(productId, quantity)
                            }
                            // Ya no navega a "cart" automáticamente
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary // Corregido
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = quantity > 0
                    ) {
                        Text(
                            text = "AGREGAR",
                            color = MaterialTheme.colorScheme.onPrimary, // Corregido
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            // --- 3. SECCIÓN DE RESEÑAS (Sin cambios) ---
            item {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Reseñas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )

                    if (uiState.error != null && uiState.product != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Formulario para añadir reseña (Sin cambios)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Deja tu reseña", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        RatingSelector(rating = ratingState, onRatingChange = { ratingState = it })

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = comentarioState,
                            onValueChange = { comentarioState = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Escribe tu comentario (opcional)") },
                            maxLines = 6
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val currentUserId = AuthManager.userId

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val uid = currentUserId!!
                                    posting = true
                                    viewModel.addReview(productId, uid, ratingState, comentarioState.text)
                                    comentarioState = TextFieldValue("")
                                    ratingState = 5.0
                                    posting = false
                                },
                                enabled = !posting && currentUserId != null
                            ) {
                                Text("Publicar")
                            }
                        }
                    }
                }
            }

            // --- 4. LISTA DE RESEÑAS (Sin cambios) ---
            items(
                items = uiState.reviews,
                key = { it.idResena }
            ) { r ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = r.nombreUsuario ?: "Usuario",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RatingDisplay(ratingStr = r.rating)
                    }
                    r.comentario?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                    }
                    r.fecha?.let {
                        Text(text = it, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    }
                    Divider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            // --- 5. FOOTER DE PAGINACIÓN (Sin cambios) ---
            item {
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    uiState.isLoadingMore -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.canLoadMore -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMoreReviews(productId) },
                                modifier = Modifier.padding(12.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Cargar más reseñas")
                            }
                        }
                    }

                    !uiState.canLoadMore && uiState.reviews.isNotEmpty() -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No hay más reseñas",
                                color = Color.Gray,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } // Fin de LazyColumn
    } // Fin de Scaffold
}

/** Muestra rating (usa r.rating string) */
@Composable
fun RatingDisplay(ratingStr: String?) {
    val value = ratingStringToDouble(ratingStr)
    Row {
        repeat(5) { index ->
            val pos = index + 1
            val icon = when {
                value >= pos -> Icons.Filled.Star
                value >= pos - 0.5 -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Selector interactivo de rating con pasos de 0.5 */
@Composable
fun RatingSelector(rating: Double, onRatingChange: (Double) -> Unit) {
    var current by remember { mutableStateOf(rating) }

    LaunchedEffect(rating) {
        current = rating
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Puntuación:", modifier = Modifier.padding(end = 8.dp))
        for (i in 1..5) {
            val pos = i.toDouble()
            val icon = when {
                current >= pos -> Icons.Filled.Star
                current >= pos - 0.5 -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        val newVal = pos
                        current = newVal
                        onRatingChange(newVal)
                    }
            )
            Spacer(Modifier.width(4.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(text = "%.1f".format(current), color = Color.Gray)
    }
}


/** Convierte rating string del backend a double (p. ej "FOUR_HALF" -> 4.5) */
fun ratingStringToDouble(ratingStr: String?): Double {
    return when (ratingStr?.uppercase()) {
        "ZERO" -> 0.0
        "HALF" -> 0.5
        "ONE" -> 1.0
        "ONE_HALF" -> 1.5
        "TWO" -> 2.0
        "TWO_HALF" -> 2.5
        "THREE" -> 3.0
        "THREE_HALF" -> 3.5
        "FOUR" -> 4.0
        "FOUR_HALF" -> 4.5
        "FIVE" -> 5.0
        else -> 0.0
    }
}