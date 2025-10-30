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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProfileScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

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
                            onValueChange = viewModel::onUsernameChanged
                        )
                        ProfileTextField(
                            label = "Correo electrónico",
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            keyboardType = KeyboardType.Email
                        )
                        ProfileTextField(
                            label = "Teléfono",
                            value = uiState.telefono,
                            onValueChange = viewModel::onTelefonoChanged,
                            keyboardType = KeyboardType.Phone
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
                            isPassword = true
                        )
                        ProfileTextField(
                            label = "Confirmar Nueva Contraseña",
                            value = uiState.confirmNewPassword,
                            onValueChange = viewModel::onConfirmNewPasswordChanged,
                            isPassword = true,
                            isVisible = uiState.newPassword.isNotEmpty()
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
                            color = OrangeAccent,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Para guardar cualquier cambio, introduce tu contraseña actual.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ProfileTextField(
                            label = "Contraseña Actual (*)",
                            value = uiState.currentPassword,
                            onValueChange = viewModel::onCurrentPasswordChanged,
                            isPassword = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- MENSAJE DE ERROR ---
                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                // --- BOTÓN DE GUARDAR ---
                Button(
                    onClick = { viewModel.updateProfile() },
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
}

// Composable reutilizable para los campos de texto del perfil
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isVisible: Boolean = true
) {
    if (!isVisible) return

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
