package com.example.aplicacionjetpack.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.dto.DireccionResponse
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel

// HTML sin cambios para el mapa
private val mapHtml = """
<!DOCTYPE html>
<html>
<head>
    <title>Mapa Interactivo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        html, body, #map { margin: 0; padding: 0; width: 100%; height: 100%; cursor: crosshair; }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map').setView([13.7942, -88.8965], 9);
        var marker;
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        map.on('click', function(e) {
            var lat = e.latlng.lat;
            var lng = e.latlng.lng;
            if (marker) {
                map.removeLayer(marker);
            }
            marker = L.marker([lat, lng]).addTo(map);
            window.location.href = `app://address?lat=${'$'}{lat}&lon=${'$'}{lng}`;
        });
    </script>
</body>
</html>
""".trimIndent()

// Palette local/refinada (respeta tus colores)
private val BrandBlack = Color(0xFF000000)
private val SoftBg = Color(0xFFF7F5F9)
private val CardBg = Color(0xFFFFFFFF)
private val MutedGray = Color(0xFF7A7A7A)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ConfirmAddressScreen(
    navController: NavController,
    idCarrito: Long,
    viewModel: CheckoutViewModel
) {
    val uiState = viewModel.uiState
    var selectedDireccionId by remember { mutableStateOf<Long?>(null) }
    var mapTouched by remember { mutableStateOf(false) }

    // Dialog para cuando usuario intenta avanzar sin dirección válida
    var showMissingAddressDialog by remember { mutableStateOf(false) }

    // Sincroniza el ID seleccionado con el ViewModel
    LaunchedEffect(selectedDireccionId) {
        viewModel.setUsarDireccionExistenteId(selectedDireccionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Selecciona dirección",
                            fontSize = 18.sp,
                            color = PurpleDark,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Toca el mapa o elige una dirección guardada",
                            fontSize = 12.sp,
                            color = MutedGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_atras),
                            contentDescription = "Atrás",
                            tint = PurpleDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBg)
            )
        },
        containerColor = SoftBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SoftBg)
        ) {
            // MAPA - ocupa aprox la mitad superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F3F5)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            settings.javaScriptEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url ?: return false
                                    if (url.scheme == "app" && url.host == "address") {
                                        val lat = url.getQueryParameter("lat")?.toDoubleOrNull()
                                        val lon = url.getQueryParameter("lon")?.toDoubleOrNull()
                                        if (lat != null && lon != null) {
                                            mapTouched = true
                                            selectedDireccionId = null
                                            viewModel.fetchAddressFromCoordinates(lat, lon)
                                        }
                                        return true
                                    }
                                    return super.shouldOverrideUrlLoading(view, request)
                                }
                            }
                            loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                        }
                    },
                    update = { web ->
                        // no-op; mantenemos el mapa cargado
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Panel inferior: direcciones guardadas + dirección seleccionada + alias
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("Mis Direcciones", style = MaterialTheme.typography.titleMedium, color = PurpleDark, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.isLoadingDirecciones) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.direccionesGuardadas.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.direccionesGuardadas, key = { it.idDireccion }) { direccion ->
                            DireccionGuardadaCard(
                                direccion = direccion,
                                isSelected = selectedDireccionId == direccion.idDireccion,
                                onClick = {
                                    viewModel.onDireccionSeleccionada(direccion)
                                    selectedDireccionId = direccion.idDireccion
                                    mapTouched = false
                                },
                                onDeleteClick = {
                                    viewModel.deleteDireccion(direccion.idDireccion)
                                    if (selectedDireccionId == direccion.idDireccion) selectedDireccionId = null
                                }
                            )
                        }
                    }
                } else {
                    Text("No tienes direcciones guardadas.", style = MaterialTheme.typography.bodyMedium, color = MutedGray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Si es una dirección nueva (mapTouched o usar nueva), permitir alias
                if (uiState.usarDireccionExistenteId == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Nueva dirección seleccionada", fontWeight = FontWeight.SemiBold, color = BrandBlack)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.aliasDireccion,
                                onValueChange = viewModel::onAliasChange,
                                label = { Text("Alias (Ej: Casa, Oficina)", color = MutedGray) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PurpleDark,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedLabelColor = PurpleDark
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Preview de la dirección que vino del mapa (si existe)
                            if (mapTouched && uiState.isLoadingAddressFromMap) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buscando dirección...", color = MutedGray)
                                }
                            } else if (viewModel.isAddressValid) {
                                Text(
                                    text = uiState.direccion,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = BrandBlack,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text("Toca el mapa para seleccionar coordenadas", color = MutedGray)
                            }
                        }
                    }
                } else {
                    // Si se usa una existente, mostrar resumen
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Enviar a:", style = MaterialTheme.typography.titleSmall, color = MutedGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBg)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.direccion.ifBlank { "Dirección seleccionada" },
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.error != null) {
                    Text(uiState.error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón continuar: valida que exista dirección válida
                Button(
                    onClick = {
                        if (viewModel.isAddressValid) {
                            navController.navigate("detalles_pago/$idCarrito")
                        } else {
                            showMissingAddressDialog = true
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONTINUAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Dialogo amable si falta dirección
    if (showMissingAddressDialog) {
        AlertDialog(
            onDismissRequest = { showMissingAddressDialog = false },
            confirmButton = {
                TextButton(onClick = { showMissingAddressDialog = false }) {
                    Text("Entendido", color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Dirección no seleccionada", color = BrandBlack, fontWeight = FontWeight.SemiBold) },
            text = {
                Text(
                    "No has seleccionado una dirección válida. Toca el mapa para marcar el sitio o elige una dirección guardada.",
                    color = BrandBlack.copy(alpha = 0.95f)
                )
            },
            containerColor = CardBg
        )
    }
}

@Composable
private fun DireccionGuardadaCard(
    direccion: DireccionResponse,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box {
        Card(
            modifier = Modifier
                .width(220.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    direccion.alias,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(direccion.calle, maxLines = 2, fontSize = 12.sp, color = BrandBlack)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${direccion.ciudad}, ${direccion.departamento}", fontSize = 12.sp, color = MutedGray)
            }
        }

        // Indicador de selección y botón eliminar
        if (isSelected) {
            Box(
                modifier = Modifier
                    .offset(x = (220 - 28).dp, y = 8.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent)
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp)
                .background(Color.Gray.copy(alpha = 0.12f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.DarkGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
