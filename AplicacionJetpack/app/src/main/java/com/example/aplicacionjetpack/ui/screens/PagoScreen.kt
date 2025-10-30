package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    navController: NavController,
    // --- PARÁMETROS AÑADIDOS ---
    idCarrito: Long,
    viewModel: CheckoutViewModel
) {
    // Conecta el estado de la UI al ViewModel
    val uiState = viewModel.uiState

    // 1. Cuando la pantalla se carga, crea el pedido en estado PENDIENTE
    LaunchedEffect(key1 = Unit) {
        viewModel.createPendingOrder(idCarrito)
    }

    // 2. Observa si el pago fue exitoso para navegar
    LaunchedEffect(key1 = uiState.checkoutSuccess) {
        if (uiState.checkoutSuccess) {
            navController.navigate("pago_finalizado") {
                popUpTo("home") { inclusive = false } // Vuelve a Home
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pago",
                        fontSize = 18.sp,
                        color = Color(0xFF2D1B4E),
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_atras),
                            contentDescription = "Atrás",
                            tint = Color(0xFF2D1B4E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Número de tarjeta
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Número de tarjeta",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.numeroTarjeta, // <- Conectado
                    onValueChange = { viewModel.onNumeroTarjetaChange(it) }, // <- Conectado
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "1234 5678 9012 3456",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1B4E),
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.error != null
                )
            }

            // Fila para Fecha de vencimiento y CVV
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fecha de vencimiento
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Fecha de vencimiento",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = uiState.fechaVencimiento, // <- Conectado
                        onValueChange = { viewModel.onFechaVencimientoChange(it) }, // <- Conectado
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "MM/AA",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2D1B4E),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        isError = uiState.error != null
                    )
                }

                // CVV
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "CVV",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = uiState.cvv, // <- Conectado
                        onValueChange = { viewModel.onCvvChange(it) }, // <- Conectado
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "123",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2D1B4E),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.error != null
                    )
                }
            }

            // Titular de la tarjeta
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Titular de la tarjeta",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.titular, // <- Conectado
                    onValueChange = { viewModel.onTitularChange(it) }, // <- Conectado
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Nombre como aparece en la tarjeta",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D1B4E),
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    isError = uiState.error != null
                )
            }

            // Muestra el error
            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Línea divisoria
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Total a pagar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL A PAGAR:",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$0.00", // TODO: Obtener total real del CarritoViewModel
                    fontSize = 24.sp,
                    color = Color(0xFF2D1B4E),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón PAGAR
            Button(
                onClick = {
                    // 3. Llama al paso final de pago
                    viewModel.processPayment()
                },
                enabled = viewModel.isPaymentValid &&
                        uiState.idPedidoPendiente != null &&
                        !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF801F)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "PAGAR",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}