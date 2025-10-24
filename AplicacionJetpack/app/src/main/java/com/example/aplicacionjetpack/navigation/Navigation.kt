package com.example.aplicacionjetpack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionjetpack.ui.screens.BusquedaScreen
import com.example.aplicacionjetpack.ui.screens.CarritoScreen
import com.example.aplicacionjetpack.ui.screens.ConfirmAddressScreen
import com.example.aplicacionjetpack.ui.screens.DetallesPagoScreen
import com.example.aplicacionjetpack.ui.screens.HomeScreen
import com.example.aplicacionjetpack.ui.screens.LoginScreen
import com.example.aplicacionjetpack.ui.screens.PagoFinalizadoScreen
import com.example.aplicacionjetpack.ui.screens.PagoScreen
import com.example.aplicacionjetpack.ui.screens.ProfileScreen
import com.example.aplicacionjetpack.ui.screens.RegisterScreen
import com.example.aplicacionjetpack.ui.screens.SessionStartScreen
import com.example.aplicacionjetpack.ui.screens.SplashScreen
import com.example.aplicacionjetpack.ui.screens.ProductDetailScreen



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("start"){
            SessionStartScreen(navController = navController)
        }
        composable("home"){
            HomeScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("cart"){
            CarritoScreen(navController = navController)
        }
        composable("product_detail"){
            ProductDetailScreen(navController = navController)
        }
        // En AppNavigation.kt - agregar esta ruta:
        composable("confirm_address") {
            ConfirmAddressScreen(navController = navController)
        }
        composable("detalles_pago") {
            DetallesPagoScreen(navController = navController)
        }
        composable("pago") {
            PagoScreen(navController = navController)
        }

        composable("pago_finalizado"){
            PagoFinalizadoScreen(navController = navController)
        }
        composable("busqueda") {
            BusquedaScreen(navController = navController)
        }
    }
}