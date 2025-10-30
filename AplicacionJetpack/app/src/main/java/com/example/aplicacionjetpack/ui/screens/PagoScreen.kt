package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.dto.TipoPago
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel
import com.example.aplicacionjetpack.utils.CardNumberVisualTransformation
import com.example.aplicacionjetpack.utils.ExpiryDateVisualTransformation
import com.example.aplicacionjetpack.utils.ValidationUtils
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    navController: NavController,
    idCarrito: Long,
    viewModel: CheckoutViewModel,
    carritoViewModel: CarritoViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val carritoUiState = carritoViewModel.uiState

    // Carga el estado del carrito para mostrar el total
    LaunchedEffect(key1 = Unit) {
        carritoViewModel.loadCarrito()
    }

    // --- 👇👇👇 ¡¡¡LA LÓGICA DE LA VICTORIA ESTÁ AQUÍ!!! 👇👇👇 ---
    // Este LaunchedEffect maneja la navegación DESPUÉS de un pago exitoso.
    LaunchedEffect(key1 = uiState.checkoutSuccess) {
        if (uiState.checkoutSuccess) {
            // 1. Le decimos al CarritoViewModel que se actualice desde el backend.
            //    Como el pago fue exitoso, el backend habrá vaciado el carrito,
            //    y esta llamada sincronizará la app con esa nueva realidad.
            carritoViewModel.loadCarrito()

            // 2. Navegamos a la pantalla de éxito.
            navController.navigate("pago_finalizado") {
                popUpTo("home") { inclusive = false }
                launchSingleTop = true
            }
        }
    }
    // --- ------------------------------------------------------------- ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
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
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp)
        ) {

            // Selector de método de pago
            Text("MÉTODO DE PAGO", style = MaterialTheme.typography.titleMedium, color = OrangeAccent, modifier = Modifier.padding(bottom = 16.dp))

            ExposedDropdownMenuBox(
                expanded = uiState.isDropdownExpanded,
                onExpandedChange = { viewModel.onDropdownClicked() }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = uiState.metodoPagoSeleccionado.displayName,
                    onValueChange = {},
                    label = { Text("Selecciona un método") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleDark, unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = uiState.isDropdownExpanded,
                    onDismissRequest = { viewModel.onDropdownDismiss() },
                ) {
                    TipoPago.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.displayName) },
                            onClick = { viewModel.onMetodoPagoChange(selectionOption) },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Campos de formulario dinámicos
            when (uiState.metodoPagoSeleccionado) {
                TipoPago.TARJETA_CREDITO -> TarjetaForm(viewModel = viewModel)
                TipoPago.PAYPAL -> PaypalForm(viewModel = viewModel)
                TipoPago.EFECTIVO -> EfectivoInfo()
            }

            // Muestra de error
            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el resto hacia abajo

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Total
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TOTAL A PAGAR:", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
                val totalFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(carritoUiState.total)
                Text(totalFormatted, fontSize = 24.sp, color = PurpleDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón PAGAR
            Button(
                onClick = { viewModel.processFinalCheckout(idCarrito) },
                enabled = viewModel.isPaymentValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("PAGAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- COMPOSABLES AUXILIARES PARA CADA MÉTODO DE PAGO (SIN CAMBIOS) ---

@Composable
private fun TarjetaForm(viewModel: CheckoutViewModel) {
    val uiState = viewModel.uiState
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PaymentTextField(
            label = "Número de tarjeta",
            value = uiState.numeroTarjeta,
            onValueChange = viewModel::onNumeroTarjetaChange,
            placeholder = "1234 5678 9012 3456",
            keyboardType = KeyboardType.Number,
            isError = uiState.error?.contains("tarjeta") == true,
            visualTransformation = CardNumberVisualTransformation()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                PaymentTextField(
                    label = "Vencimiento",
                    value = uiState.fechaVencimiento,
                    onValueChange = viewModel::onFechaVencimientoChange,
                    placeholder = "MM/YY",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.error?.contains("vencimiento") == true,
                    visualTransformation = ExpiryDateVisualTransformation()
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                PaymentTextField(
                    label = "CVV",
                    value = uiState.cvv,
                    onValueChange = viewModel::onCvvChange,
                    placeholder = "123",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.error?.contains("CVV") == true
                )
            }
        }
        PaymentTextField(
            label = "Titular de la tarjeta",
            value = uiState.titular,
            onValueChange = viewModel::onTitularChange,
            placeholder = "Nombre como aparece en la tarjeta",
            isError = uiState.error?.contains("titular") == true
        )
    }
}

@Composable
private fun PaypalForm(viewModel: CheckoutViewModel) {
    val uiState = viewModel.uiState
    Column {
        PaymentTextField(
            label = "Correo de PayPal",
            value = uiState.emailPaypal,
            onValueChange = viewModel::onEmailPaypalChange,
            placeholder = "usuario@ejemplo.com",
            keyboardType = KeyboardType.Email,
            isError = uiState.error?.contains("PayPal") == true,
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_paypal), contentDescription = "PayPal", tint = Color.Unspecified, modifier = Modifier.size(24.dp)) }
        )
    }
}

@Composable
private fun EfectivoInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Money, contentDescription = "Efectivo", tint = PurpleDark.copy(alpha = 0.8f))
        Spacer(Modifier.width(16.dp))
        Text(
            "Pagarás el monto exacto al momento de recibir tu pedido. Por favor, ten el dinero preparado.",
            style = MaterialTheme.typography.bodyMedium,
            color = PurpleDark.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun PaymentTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null
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
                focusedBorderColor = PurpleDark,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = true,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon
        )
    }
}

