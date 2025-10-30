// src/main/java/com/example/aplicacionjetpack/ui/screens/ConfirmAddressScreen.kt
package com.example.aplicacionjetpack.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// --- 👇👇👇 ¡¡¡EL JAVASCRIPT HA SIDO MEJORADO!!! 👇👇👇 ---
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
            
            // --- ¡LA MAGIA AHORA ES ESTA! ---
            // En lugar de llamar a una interfaz, cambiamos la URL.
            window.location.href = `app://address?lat=${"$"}{lat}&lon=${"$"}{lng}`;
        });
    </script>
</body>
</html>
""".trimIndent()


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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Toca el mapa para seleccionar", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            settings.javaScriptEnabled = true

                            // --- 👇👇👇 ¡¡¡LA SOLUCIÓN DEFINITIVA!!! 👇👇👇 ---
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url ?: return false
                                    // Si la URL que se intenta cargar empieza con nuestro esquema personalizado...
                                    if (url.scheme == "app" && url.host == "address") {
                                        // Extraemos los parámetros lat y lon
                                        val lat = url.getQueryParameter("lat")?.toDoubleOrNull()
                                        val lon = url.getQueryParameter("lon")?.toDoubleOrNull()

                                        if (lat != null && lon != null) {
                                            mapTouched = true
                                            viewModel.fetchAddressFromCoordinates(lat, lon)
                                        }
                                        // Devolvemos 'true' para decirle al WebView:
                                        // "No navegues a esta URL, ya la hemos manejado nosotros".
                                        return true
                                    }
                                    // Para cualquier otra URL, deja que el WebView la maneje normalmente.
                                    return super.shouldOverrideUrlLoading(view, request)
                                }
                            }
                            // --- -------------------------------------------- ---

                            loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // El panel inferior no necesita cambios, el código anterior era correcto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Mis Direcciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                if (uiState.isLoadingDirecciones) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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
                                }
                            )
                        }
                    }
                } else {
                    Text("No tienes direcciones guardadas.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Enviar a:", style = MaterialTheme.typography.titleSmall, color = Color.Gray)

                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.height(48.dp)) {
                    when {
                        mapTouched && uiState.isLoadingAddressFromMap -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Buscando dirección...", color = Color.Gray)
                            }
                        }
                        viewModel.isAddressValid -> {
                            Text(
                                text = uiState.direccion,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        else -> {
                            Text(
                                text = "Toca el mapa para seleccionar una dirección",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                if (uiState.error != null) {
                    Text(text = uiState.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }

                Button(
                    onClick = {
                        viewModel.createPendingOrder(idCarrito, usarDireccionExistenteId = selectedDireccionId)
                        navController.navigate("detalles_pago/$idCarrito")
                    },
                    enabled = viewModel.isAddressValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONTINUAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// El Card no necesita cambios
@Composable
private fun DireccionGuardadaCard(
    direccion: DireccionResponse,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) OrangeAccent else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(direccion.alias, fontWeight = FontWeight.Bold, color = PurpleDark)
            Text(direccion.calle, maxLines = 2, fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
            Text("${direccion.ciudad}, ${direccion.departamento}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
