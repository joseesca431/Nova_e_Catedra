package com.example.aplicacionjetpack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.data.dto.NotificacionResponse
import com.example.aplicacionjetpack.ui.viewmodel.NotificacionesViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import com.example.aplicacionjetpack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    navController: NavController,
    viewModel: NotificacionesViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        // Carga inicial
        viewModel.loadNotificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Notificaciones", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = if (uiState.notificaciones.isEmpty()) "Sin notificaciones" else "${uiState.notificaciones.size} nuevas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                uiState.notificaciones.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Sin Notificaciones",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No tienes notificaciones nuevas.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(uiState.notificaciones, key = { _, n -> n.id }) { index, notificacion ->
                            NotificationItem(
                                notificacion = notificacion,
                                // <-- LLAMADA CORRECTA: tu ViewModel sólo espera el id de la notificación.
                                onClick = { viewModel.marcarComoLeida(notificacion.id) }
                            )
                            if (index < uiState.notificaciones.lastIndex) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notificacion: NotificacionResponse,
    onClick: () -> Unit
) {
    // Colors según estado
    val unread = notificacion.estado.equals("ENVIADA", ignoreCase = true)
    val cardBg = if (unread) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
    val titleWeight = if (unread) FontWeight.SemiBold else FontWeight.Medium
    val titleColor = if (unread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val subtitleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de no leído
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (unread) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificacion.mensaje,
                    fontSize = 15.sp,
                    fontWeight = titleWeight,
                    color = titleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // fechaEnvio: la DTO tiene String; si quieres formateo lo hacemos aparte.
                    Text(
                        text = notificacion.fechaEnvio,
                        fontSize = 12.sp,
                        color = subtitleColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Acción visual o cheurón
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

/* ----------------- Preview con datos de ejemplo ----------------- */

@Preview(showBackground = true)
@Composable
private fun PreviewNotificacionesScreen() {
    // Dummy preview data: **orden correcto** en la data class:
    // NotificacionResponse(id: Long, mensaje: String, fechaEnvio: String, estado: String, pedidoId: Long)
    val previewNotifs = remember {
        mutableStateListOf(
            NotificacionResponse(1L, "Bienvenido a NOVA+e — tu cuenta se creó correctamente.", "2025-10-29 14:32", "ENVIADA", 101L),
            NotificacionResponse(2L, "Tu curso 'Introducción a Kotlin' tiene una nueva lección.", "2025-10-28 09:18", "LEIDA", 102L),
            NotificacionResponse(3L, "Recordatorio: participa en el foro hoy.", "2025-10-30 08:00", "ENVIADA", 103L)
        )
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(previewNotifs) { _, item ->
                NotificationItem(notificacion = item, onClick = {})
            }
        }
    }
}
