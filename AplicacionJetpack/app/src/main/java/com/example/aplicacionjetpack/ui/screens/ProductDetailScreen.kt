package com.example.aplicacionjetpack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.ui.theme.*
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import com.example.aplicacionjetpack.ui.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    carritoViewModel: CarritoViewModel = hiltViewModel()
) {
    val uiState by remember { derivedStateOf { viewModel.uiState } }

    // Palette (for this screen we force a coherent white + orange look using your palette)
    val brandPrimary = PurpleDark           // dark text / brand
    val brandAccent = OrangeAccent          // CTA (naranja)
    val surfaceWhite = Color(0xFFFFFFFF)    // fondo principal de tarjetas / inputs (blanco)
    val backgroundSoft = PurpleGrey80       // fondo suave (usar con moderación)
    val subtleBorder = PurpleGrey40         // borde y outline
    val starTint = OrangeAccent             // estrellas en naranja

    // Local UI state
    var quantity by rememberSaveable { mutableStateOf(1) }
    var comentarioState by remember { mutableStateOf("") }
    var ratingState by rememberSaveable { mutableStateOf(5.0) }
    var posting by remember { mutableStateOf(false) }
    var descriptionExpanded by rememberSaveable { mutableStateOf(false) }

    // Snackbar host for carrito events
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = carritoViewModel) {
        carritoViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CarritoViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message, duration = SnackbarDuration.Short)
                }
            }
        }
    }

    // Load product when productId changes
    LaunchedEffect(productId) {
        if (productId != 0L) viewModel.loadProductAndReviews(productId)
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = uiState.product?.nombre ?: "Detalle del producto",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = brandPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = brandPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh(productId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = brandPrimary)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = surfaceWhite)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        containerColor = backgroundSoft
    ) { innerPadding ->

        // Full screen loading / error handling
        if (uiState.isLoading && uiState.product == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = brandAccent)
            }
            return@Scaffold
        }

        if (uiState.error != null && uiState.product == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(uiState.error ?: "Error desconocido", color = brandPrimary)
            }
            return@Scaffold
        }

        // compute average rating from reviews (safe)
        val avgRating: Double? = remember(uiState.reviews) {
            if (uiState.reviews.isNotEmpty()) {
                val nums = uiState.reviews.map { ratingStringToDouble(it.rating) }
                nums.average()
            } else null
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                // Image hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(surfaceWhite),
                    contentAlignment = Alignment.Center
                ) {
                    val imageUrl = uiState.product?.imagen
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = uiState.product?.nombre ?: "Producto",
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            fallback = painterResource(id = R.drawable.ic_producto),
                            error = painterResource(id = R.drawable.ic_producto)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_producto),
                            contentDescription = "Producto",
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Product card (blanco)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-28).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = uiState.product?.nombre ?: "Nombre del producto",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = brandPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(8.dp))

                        // Price + rating row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val priceText = uiState.product?.precio?.let { precio ->
                                try {
                                    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(precio)
                                } catch (_: Exception) {
                                    "$${uiState.product?.precio ?: BigDecimal.ZERO}"
                                }
                            } ?: "$0.00"

                            Text(
                                text = priceText,
                                style = MaterialTheme.typography.headlineMedium,
                                color = brandAccent,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(Modifier.weight(1f))

                            RatingSummary(
                                ratingValue = avgRating,
                                reviewsCount = uiState.reviews.size,
                                starColor = starTint,
                                textColor = brandPrimary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Description with expand/collapse
                        val maxLines = if (descriptionExpanded) Int.MAX_VALUE else 3
                        Text(
                            text = uiState.product?.descripcion
                                ?: "Breve descripción del producto, mostrando sus características y cualquier detalle más",
                            style = MaterialTheme.typography.bodyMedium,
                            color = brandPrimary.copy(alpha = 0.9f),
                            maxLines = maxLines,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        )

                        TextButton(
                            onClick = { descriptionExpanded = !descriptionExpanded },
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(
                                if (descriptionExpanded) "Mostrar menos" else "Mostrar más",
                                color = brandAccent
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Quantity selector + Add to cart
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Quantity controls (light background)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 2.dp,
                                color = surfaceWhite,
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Disminuir", tint = brandPrimary)
                                    }

                                    Text(
                                        text = quantity.toString(),
                                        modifier = Modifier.width(36.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = brandPrimary
                                    )

                                    IconButton(
                                        onClick = { quantity++ },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Aumentar", tint = brandPrimary)
                                    }
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            // Add button (naranja)
                            Button(
                                onClick = { carritoViewModel.addItem(productId, quantity) },
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = brandAccent, contentColor = surfaceWhite)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = surfaceWhite)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Agregar al carrito", color = surfaceWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Reviews header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Reseñas", style = MaterialTheme.typography.titleMedium, color = brandPrimary)
                    Spacer(Modifier.height(6.dp))
                    if (uiState.error != null && uiState.product != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = brandAccent,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Add review card (blanco con borde sutil)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Deja tu reseña", fontWeight = FontWeight.SemiBold, color = brandPrimary)
                        Spacer(Modifier.height(8.dp))
                        RatingSelector(
                            rating = ratingState,
                            onRatingChange = { ratingState = it },
                            starTint = starTint
                        )
                        Spacer(Modifier.height(8.dp))

                        // OutlinedTextField con fondo blanco y borde naranja al focus
                        OutlinedTextField(
                            value = comentarioState,
                            onValueChange = { comentarioState = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = {
                                Text("Escribe tu comentario (opcional)", color = brandPrimary.copy(alpha = 0.45f))
                            },
                            maxLines = 6,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = brandAccent,
                                unfocusedBorderColor = subtleBorder.copy(alpha = 0.6f),
                                containerColor = surfaceWhite,
                                focusedTextColor = brandPrimary,
                                unfocusedTextColor = brandPrimary.copy(alpha = 0.9f),
                                cursorColor = brandAccent,

                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            val currentUserId = AuthManager.userId
                            Button(
                                onClick = {
                                    currentUserId?.let { uid ->
                                        posting = true
                                        viewModel.addReview(productId, uid, ratingState, comentarioState)
                                        comentarioState = ""
                                        ratingState = 5.0
                                        posting = false
                                    }
                                },
                                enabled = currentUserId != null && comentarioState.isNotBlank() && !posting,
                                colors = ButtonDefaults.buttonColors(containerColor = brandAccent, contentColor = surfaceWhite)
                            ) {
                                Text("Publicar")
                            }
                        }
                    }
                }
            }

            // Reviews list
            items(uiState.reviews, key = { it.idResena }) { r ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = r.nombreUsuario ?: "Usuario",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = brandPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        RatingDisplay(ratingStr = r.rating, starTint = starTint)
                    }
                    r.comentario?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp), color = brandPrimary)
                    }
                    r.fecha?.let {
                        Text(text = it, fontSize = 12.sp, color = brandPrimary.copy(alpha = 0.6f), modifier = Modifier.padding(top = 6.dp))
                    }
                    Divider(modifier = Modifier.padding(top = 8.dp), color = subtleBorder.copy(alpha = 0.6f))
                }
            }

            // Pagination footer
            item {
                Spacer(modifier = Modifier.height(12.dp))
                when {
                    uiState.isLoadingMore -> {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = brandAccent)
                        }
                    }
                    uiState.canLoadMore -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = { viewModel.loadMoreReviews(productId) },
                                modifier = Modifier.padding(12.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = surfaceWhite, contentColor = brandPrimary)
                            ) {
                                Text("Cargar más reseñas")
                            }
                        }
                    }
                    !uiState.canLoadMore && uiState.reviews.isNotEmpty() -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text(text = "No hay más reseñas", color = brandPrimary.copy(alpha = 0.6f), modifier = Modifier.padding(12.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        } // LazyColumn end
    } // Scaffold end
}

/** Compact rating summary shown near price */
@Composable
private fun RatingSummary(
    ratingValue: Double?,
    reviewsCount: Int,
    starColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    val display = ratingValue ?: 0.0
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Star, contentDescription = null, tint = starColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = "%.1f".format(display), fontWeight = FontWeight.Medium, color = textColor)
        Spacer(Modifier.width(8.dp))
        Text(text = "(${reviewsCount})", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.85f))
    }
}

/** Muestra rating (usa r.rating string) */
@Composable
fun RatingDisplay(ratingStr: String?, starTint: androidx.compose.ui.graphics.Color) {
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
                tint = starTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(2.dp))
        }
    }
}

/** Selector interactivo de rating con pasos enteros (click en estrella) */
@Composable
fun RatingSelector(rating: Double, onRatingChange: (Double) -> Unit, starTint: androidx.compose.ui.graphics.Color) {
    var current by remember { mutableStateOf(rating) }
    LaunchedEffect(rating) { current = rating }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Puntuación:", modifier = Modifier.padding(end = 8.dp), color = PurpleDark)
        for (i in 1..5) {
            val pos = i.toDouble()
            val icon = when {
                current >= pos -> Icons.Filled.Star
                else -> Icons.Outlined.StarOutline
            }
            val alpha by animateFloatAsState(targetValue = if (current >= pos) 1f else 0.6f)
            IconButton(
                onClick = {
                    current = pos
                    onRatingChange(pos)
                },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = starTint, modifier = Modifier.alpha(alpha))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(text = "%.0f".format(current), color = PurpleDark.copy(alpha = 0.85f))
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
