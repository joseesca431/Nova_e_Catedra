package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.UserViewModel

// Color solicitado por ti: negro puro para texto/placeholder fuerte
private val BrandBlack = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProfileScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    // Dialog state para mostrar errores de validación
    var showValidationDialog by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }

    // Cuando la actualización sea exitosa, retrocedemos
    LaunchedEffect(key1 = uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Mi Perfil", color = PurpleDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = PurpleDark)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // --- TARJETA DE INFORMACIÓN PERSONAL ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            color = PurpleDark,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ProfileTextField(
                            label = "Nombre de usuario",
                            value = uiState.username,
                            onValueChange = viewModel::onUsernameChanged,
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                        ProfileTextField(
                            label = "Correo electrónico",
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            keyboardType = KeyboardType.Email,
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                        ProfileTextField(
                            label = "Teléfono",
                            value = uiState.telefono,
                            onValueChange = viewModel::onTelefonoChanged,
                            keyboardType = KeyboardType.Phone,
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                    }
                }

                // --- TARJETA DE SEGURIDAD (CAMBIO DE CONTRASEÑA) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Seguridad",
                            style = MaterialTheme.typography.titleMedium,
                            color = PurpleDark,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ProfileTextField(
                            label = "Nueva Contraseña (Opcional)",
                            value = uiState.newPassword,
                            onValueChange = viewModel::onNewPasswordChanged,
                            isPassword = true,
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                        ProfileTextField(
                            label = "Confirmar Nueva Contraseña",
                            value = uiState.confirmNewPassword,
                            onValueChange = viewModel::onConfirmNewPasswordChanged,
                            isPassword = true,
                            isVisible = uiState.newPassword.isNotEmpty(),
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                    }
                }

                // --- TARJETA DE CONFIRMACIÓN FINAL ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.5f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Confirmar Cambios",
                            style = MaterialTheme.typography.titleMedium,
                            color = OrangeAccent, // solicitado: este título en naranja
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Para guardar cualquier cambio, introduce tu contraseña actual.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = BrandBlack.copy(alpha = 0.9f)
                        )
                        ProfileTextField(
                            label = "Contraseña Actual (*)",
                            value = uiState.currentPassword,
                            onValueChange = viewModel::onCurrentPasswordChanged,
                            isPassword = true,
                            labelColor = BrandBlack,
                            textColor = BrandBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- MENSAJE DE ERROR (desde ViewModel) ---
                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                // --- BOTÓN DE GUARDAR: valida campos antes de llamar a viewModel.updateProfile() ---
                Button(
                    onClick = {
                        // VALIDACIÓN: construye lista de campos faltantes
                        val missing = mutableListOf<String>()
                        if (uiState.username.isBlank()) missing.add("Nombre de usuario")
                        if (uiState.email.isBlank()) missing.add("Correo electrónico")
                        if (uiState.telefono.isBlank()) missing.add("Teléfono")
                        // Si el usuario está intentando cambiar contraseña, exige confirmación
                        if (uiState.newPassword.isNotEmpty()) {
                            if (uiState.confirmNewPassword.isBlank()) missing.add("Confirmar nueva contraseña")
                            else if (uiState.newPassword != uiState.confirmNewPassword) {
                                validationMessage = "Las contraseñas nuevas no coinciden. Por favor verifica."
                                showValidationDialog = true
                                return@Button
                            }
                            // optional: could add password strength validation here
                        }
                        // Contraseña actual es obligatoria para guardar cambios
                        if (uiState.currentPassword.isBlank()) missing.add("Contraseña actual")

                        if (missing.isNotEmpty()) {
                            // Construir mensaje amable y claro
                            val listText = missing.joinToString(separator = "\n") { "• $it" }
                            validationMessage = "Para continuar falta completar lo siguiente:\n\n$listText\n\nPor favor complétalo para poder guardar los cambios."
                            showValidationDialog = true
                            return@Button
                        }

                        // Si todo OK, llama al ViewModel
                        viewModel.updateProfile()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isUpdating,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("GUARDAR CAMBIOS", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIALOGO DE VALIDACIÓN AMABLE ---
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("OK", color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Faltan datos", color = BrandBlack, fontWeight = FontWeight.SemiBold) },
            text = {
                Text(
                    validationMessage,
                    color = BrandBlack.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            },
            containerColor = Color.White
        )
    }
}

// Composable reutilizable para los campos de texto del perfil (ahora con control de colores)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isVisible: Boolean = true,
    labelColor: Color = BrandBlack,
    textColor: Color = BrandBlack
) {
    if (!isVisible) return

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = labelColor) },
        placeholder = { Text(text = label, color = labelColor.copy(alpha = 0.5f)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = OrangeAccent,
            unfocusedBorderColor = Color(0xFFDDDDDD),
            containerColor = Color.White,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor.copy(alpha = 0.95f),
            cursorColor = OrangeAccent,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor.copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
