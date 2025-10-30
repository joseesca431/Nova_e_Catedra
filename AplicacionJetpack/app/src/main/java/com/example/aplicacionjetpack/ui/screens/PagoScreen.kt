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
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    navController: NavController,
    idCarrito: Long,
    viewModel: CheckoutViewModel,
    // Inyectamos el CarritoViewModel para poder mostrar el total
    carritoViewModel: CarritoViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val carritoUiState = carritoViewModel.uiState

    // --- 👇👇👇 ¡LÓGICA SIMPLIFICADA Y CORRECTA! 👇👇👇 ---
    // Ya no se crea ningún pedido aquí.
    // Solo cargamos los datos del carrito para mostrar el total.
    LaunchedEffect(key1 = Unit) {
        carritoViewModel.loadCarrito()
    }

    // Observa si el pago fue exitoso para navegar
    LaunchedEffect(key1 = uiState.checkoutSuccess) {
        if (uiState.checkoutSuccess) {
            navController.navigate("pago_finalizado") {
                popUpTo("home") { inclusive = false }
                launchSingleTop = true
            }
        }
    }
    // --- -------------------------------------------- ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago", fontSize = 18.sp, color = Color(0xFF2D1B4E), fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Atrás", tint = Color(0xFF2D1B4E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            // --- CAMPOS DE TEXTO (Sin cambios en su estructura) ---
            // Número de tarjeta
            FormTextField(
                label = "Número de tarjeta",
                value = uiState.numeroTarjeta,
                onValueChange = viewModel::onNumeroTarjetaChange,
                placeholder = "1234 5678 9012 3456",
                keyboardType = KeyboardType.Number,
                isError = uiState.error != null
            )

            // Fecha de vencimiento y CVV
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(
                        label = "Fecha de vencimiento",
                        value = uiState.fechaVencimiento,
                        onValueChange = viewModel::onFechaVencimientoChange,
                        placeholder = "MM/AA",
                        isError = uiState.error != null
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(
                        label = "CVV",
                        value = uiState.cvv,
                        onValueChange = viewModel::onCvvChange,
                        placeholder = "123",
                        keyboardType = KeyboardType.Number,
                        isError = uiState.error != null
                    )
                }
            }

            // Titular de la tarjeta
            FormTextField(
                label = "Titular de la tarjeta",
                value = uiState.titular,
                onValueChange = viewModel::onTitularChange,
                placeholder = "Nombre como aparece en la tarjeta",
                isError = uiState.error != null
            )
            // --- ------------------------------------------------ ---

            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el resto hacia abajo

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // --- 👇👇👇 ¡TOTAL REAL! 👇👇👇 ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TOTAL A PAGAR:", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
                val totalFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(carritoUiState.total)
                Text(totalFormatted, fontSize = 24.sp, color = Color(0xFF2D1B4E), fontWeight = FontWeight.Bold)
            }
            // --- --------------------------- ---

            Spacer(modifier = Modifier.height(16.dp))

            // --- 👇👇👇 ¡¡¡EL BOTÓN DE LA VICTORIA!!! 👇👇👇 ---
            Button(
                onClick = {
                    // Llama a la nueva función que lo hace todo
                    viewModel.processFinalCheckout(idCarrito)
                },
                // Se habilita si los datos del formulario son válidos
                enabled = viewModel.isPaymentValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF801F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                } else {
                    Text("PAGAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            // --- ------------------------------------------ ---
        }
    }
}

// Componente auxiliar para no repetir código en los TextFields
@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, fontSize = 14.sp, color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D1B4E),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = true
        )
    }
}
