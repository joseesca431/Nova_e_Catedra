package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Este efecto se lanza una vez
    LaunchedEffect(key1 = true) {
        delay(3000) // Espera 3 segundos
        // Navega a 'login' y elimina 'splash' del historial
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)), // Un fondo claro
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.novaic), // Asume que tienes 'novaic' en drawable
            contentDescription = "Logo NOVA",
            modifier = Modifier.size(120.dp)
        )
    }
}