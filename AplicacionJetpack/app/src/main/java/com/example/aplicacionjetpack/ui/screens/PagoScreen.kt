@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import com.example.aplicacionjetpack.data.dto.TipoPago
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.Purple40
import com.example.aplicacionjetpack.ui.theme.Purple80
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.CarritoViewModel
import com.example.aplicacionjetpack.ui.viewmodel.CheckoutViewModel
import com.example.aplicacionjetpack.utils.CardNumberVisualTransformation
import com.example.aplicacionjetpack.utils.ExpiryDateVisualTransformation
import java.text.NumberFormat
import java.time.Year
import java.util.Locale

// Colores y constantes
private val BrandBlack = Color(0xFF000000)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val SoftBg = Color(0xFFFDF8F3)

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

    // Dialog state para mensajes de validación / errores
    var showValidationDialog by remember { mutableStateOf(false) }
    var validationTitle by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf("") }

    // navegación después de checkout success
    LaunchedEffect(key1 = uiState.checkoutSuccess) {
        if (uiState.checkoutSuccess) {
            carritoViewModel.loadCarrito()
            navController.navigate("pago_finalizado") {
                popUpTo("home") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = SoftBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceWhite)
                .padding(paddingValues)
                .padding(24.dp)
        ) {

            // Selector de método de pago (etiqueta 'Selecciona un método' más visible)
            Text(
                "MÉTODO DE PAGO",
                style = MaterialTheme.typography.titleMedium,
                color = OrangeAccent,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dropdown (ExposedDropdownMenuBox)
            ExposedDropdownMenuBox(
                expanded = uiState.isDropdownExpanded,
                onExpandedChange = { viewModel.onDropdownClicked() },
                modifier = Modifier.background(SurfaceWhite)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = uiState.metodoPagoSeleccionado.displayName,
                    onValueChange = {},
                    label = { Text("Selecciona un método", color = PurpleDark, fontWeight = FontWeight.SemiBold) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Purple80
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(color = PurpleDark),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Purple40,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = PurpleDark,
                        focusedLabelColor = PurpleDark,
                        unfocusedLabelColor = Purple80.copy(alpha = 0.9f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    singleLine = true,
                )

                ExposedDropdownMenu(
                    expanded = uiState.isDropdownExpanded,
                    onDismissRequest = { viewModel.onDropdownDismiss() },
                    modifier = Modifier.background(SurfaceWhite)
                ) {
                    TipoPago.values().forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    selectionOption.displayName,
                                    color = PurpleDark
                                )
                            },
                            onClick = {
                                viewModel.onMetodoPagoChange(selectionOption)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de formulario dinámicos según método seleccionado
            when (uiState.metodoPagoSeleccionado) {
                TipoPago.TARJETA_CREDITO -> TarjetaForm(viewModel = viewModel, uiState = uiState)
                TipoPago.PAYPAL -> PaypalForm(viewModel = viewModel, uiState = uiState)
                TipoPago.EFECTIVO -> EfectivoInfo()
            }

            // Muestra de error general del UI state
            uiState.error?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Divider(color = Color(0xFFEDEDED), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

            // Total
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TOTAL A PAGAR:", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
                val totalFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(carritoUiState.total)
                Text(totalFormatted, fontSize = 24.sp, color = PurpleDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón PAGAR: Al presionar, realizamos validación según método
            Button(
                onClick = {
                    // Validación por método
                    when (uiState.metodoPagoSeleccionado) {
                        TipoPago.TARJETA_CREDITO -> {
                            val missing = mutableListOf<String>()
                            if (uiState.numeroTarjeta.isBlank()) missing.add("Número de tarjeta")
                            if (uiState.fechaVencimiento.isBlank()) missing.add("Fecha de vencimiento")
                            if (uiState.cvv.isBlank()) missing.add("CVV")
                            if (uiState.titular.isBlank()) missing.add("Titular de la tarjeta")

                            if (missing.isNotEmpty()) {
                                validationTitle = "Faltan datos de la tarjeta"
                                validationMessage = missing.joinToString(separator = "\n") { "• $it" }
                                showValidationDialog = true
                                return@Button
                            }

                            // validaciones básicas locales
                            val cardOk = isValidCardNumberLocal(uiState.numeroTarjeta)
                            val expOk = isValidExpiryLocal(uiState.fechaVencimiento)
                            val cvvOk = uiState.cvv.length in 3..4

                            val errors = mutableListOf<String>()
                            if (!cardOk) errors.add("Número de tarjeta inválido")
                            if (!expOk) errors.add("Fecha de vencimiento inválida (MM/YY)")
                            if (!cvvOk) errors.add("CVV inválido")

                            if (errors.isNotEmpty()) {
                                validationTitle = "Datos inválidos"
                                validationMessage = errors.joinToString("\n") { "• $it" }
                                showValidationDialog = true
                                return@Button
                            }

                            // si todo OK, procesar pago
                            viewModel.processFinalCheckout(idCarrito)
                        }

                        TipoPago.PAYPAL -> {
                            if (uiState.emailPaypal.isBlank()) {
                                validationTitle = "Falta correo PayPal"
                                validationMessage = "Ingresa tu correo de PayPal para continuar."
                                showValidationDialog = true
                                return@Button
                            }
                            if (!isValidEmailLocal(uiState.emailPaypal)) {
                                validationTitle = "Email inválido"
                                validationMessage = "El correo de PayPal no tiene un formato válido."
                                showValidationDialog = true
                                return@Button
                            }
                            viewModel.processFinalCheckout(idCarrito)
                        }

                        TipoPago.EFECTIVO -> {
                            // No requiere datos; procesar
                            viewModel.processFinalCheckout(idCarrito)
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                } else {
                    Text("PAGAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Dialogo de validación/errores (amable)
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("Entendido", color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = { Text(validationTitle, color = BrandBlack, fontWeight = FontWeight.SemiBold) },
            text = { Text(validationMessage, color = BrandBlack.copy(alpha = 0.95f)) },
            containerColor = SurfaceWhite
        )
    }
}

/* ---------- Composables auxiliares ---------- */

@Composable
private fun TarjetaForm(viewModel: CheckoutViewModel, uiState: com.example.aplicacionjetpack.ui.viewmodel.CheckoutUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
        PaymentTextField(
            label = "Número de tarjeta",
            value = uiState.numeroTarjeta,
            onValueChange = viewModel::onNumeroTarjetaChange,
            placeholder = "1234 5678 9012 3456",
            keyboardType = KeyboardType.Number,
            isError = uiState.error?.contains("tarjeta") == true,
            visualTransformation = CardNumberVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = PurpleDark) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
private fun PaypalForm(viewModel: CheckoutViewModel, uiState: com.example.aplicacionjetpack.ui.viewmodel.CheckoutUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        PaymentTextField(
            label = "Correo de PayPal",
            value = uiState.emailPaypal,
            onValueChange = viewModel::onEmailPaypalChange,
            placeholder = "usuario@ejemplo.com",
            keyboardType = KeyboardType.Email,
            isError = uiState.error?.contains("PayPal") == true,
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_paypal), contentDescription = "PayPal", tint = Color.Unspecified, modifier = Modifier.size(24.dp))
            }
        )
    }
}

@Composable
private fun EfectivoInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Money, contentDescription = "Efectivo", tint = PurpleDark.copy(alpha = 0.85f))
        Spacer(Modifier.width(12.dp))
        Text(
            "Pagarás el monto exacto al momento de recibir tu pedido. Ten el dinero preparado.",
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
            color = Purple80,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, fontSize = 14.sp, color = BrandBlack.copy(alpha = 0.9f)) },
            textStyle = LocalTextStyle.current.copy(color = BrandBlack),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Purple40,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PurpleDark,
                focusedLabelColor = PurpleDark,
                unfocusedLabelColor = BrandBlack.copy(alpha = 0.9f),
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

/* ---------- Validadores locales simples (evitan dependencias externas) ---------- */

private fun isValidCardNumberLocal(number: String): Boolean {
    // elimina espacios y comprueba longitud mínima (13-19) y solo dígitos
    val digits = number.filter { it.isDigit() }
    return digits.length in 13..19
}

private fun isValidExpiryLocal(mmYY: String): Boolean {
    // formato esperado "MM/YY" o "MMYY"
    val cleaned = mmYY.filter { it.isDigit() }
    if (cleaned.length != 4) return false
    val month = cleaned.substring(0, 2).toIntOrNull() ?: return false
    val yearTwo = cleaned.substring(2, 4).toIntOrNull() ?: return false
    if (month !in 1..12) return false
    val currentYearTwo = Year.now().value % 100
    return yearTwo > currentYearTwo || (yearTwo == currentYearTwo && month >= java.time.LocalDate.now().monthValue)
}

private fun isValidEmailLocal(email: String): Boolean {
    val e = email.trim()
    return e.contains("@") && e.contains(".") && e.indexOf("@") < e.lastIndexOf(".")
}
