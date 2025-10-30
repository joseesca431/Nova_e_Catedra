package com.example.aplicacionjetpack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// --- Importa TODOS tus Screens ---
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
import com.example.aplicacionjetpack.ui.screens.SplashScreen
import com.example.aplicacionjetpack.ui.screens.ProductDetailScreen
// --- Importa TODOS tus ViewModels ---
import com.example.aplicacionjetpack.ui.viewmodel.LoginViewModel
import com.example.aplicacionjetpack.ui.viewmodel.RegisterViewModel
// (Asegúrate de tener también los ViewModels para las otras pantallas: Home, Product, Cart, etc.)
// import com.example.aplicacionjetpack.ui.viewmodel.HomeViewModel
// import com.example.aplicacionjetpack.ui.viewmodel.ProductListViewModel
// import com.example.aplicacionjetpack.ui.viewmodel.ProductDetailViewModel
// import com.example.aplicacionjetpack.ui.viewmodel.OrderDetailViewModel
// import com.example.aplicacionjetpack.ui.viewmodel.CartViewModel

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

        // --- PANTALLA LOGIN ---
        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            LoginScreen(
                navController = navController,
                uiState = uiState,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClicked
            )
        }

        // --- PANTALLA REGISTRO ---
        composable("register") {
            val viewModel: RegisterViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            RegisterScreen(
                navController = navController,
                uiState = uiState,
                onPrimerNombreChange = viewModel::onPrimerNombreChange,
                onPrimerApellidoChange = viewModel::onPrimerApellidoChange,
                onFechaNacimientoClicked = viewModel::onFechaNacimientoClicked, // Para el calendario
                onCalendarDismiss = viewModel::onCalendarDismiss,
                onDateSelected = viewModel::onDateSelected,
                onEmailChange = viewModel::onEmailChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onTelefonoChange = viewModel::onTelefonoChange,
                onDuiChange = viewModel::onDuiChange,
                onDireccionChange = viewModel::onDireccionChange,
                onSegundoNombreChange = viewModel::onSegundoNombreChange,
                onSegundoApellidoChange = viewModel::onSegundoApellidoChange,
                onRegisterClick = viewModel::onRegisterClicked
            )
        }

        // --- PANTALLAS DE CLIENTE (Aún no refactorizadas, excepto DetallesPago) ---
        // (Eventualmente, todas estas deberían inyectar su propio ViewModel)

        composable("home"){
            // TODO: Refactorizar HomeScreen con HomeViewModel
            HomeScreen(navController = navController)
        }
        composable("profile") {
            // TODO: Refactorizar ProfileScreen con ProfileViewModel
            ProfileScreen(navController = navController)
        }
        composable("cart"){
            // TODO: Refactorizar CarritoScreen con CartViewModel
            CarritoScreen(navController = navController)
        }
        composable("product_detail"){
            // TODO: Refactorizar ProductDetailScreen con ProductDetailViewModel
            ProductDetailScreen(navController = navController)
        }
        composable("confirm_address") {
            // TODO: Refactorizar ConfirmAddressScreen con AddressViewModel
            ConfirmAddressScreen(navController = navController)
        }

        // --- PANTALLA DETALLES DE PAGO (CORREGIDA) ---
        composable(
            route = "detalles_pago", // O "detalles_pago/{pedidoId}" si necesitas pasar un ID
            // arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            // --- 👇 CORRECCIÓN AQUÍ 👇 ---
            // Inyecta el ViewModel
            // val viewModel: OrderDetailViewModel = hiltViewModel() // Necesitarás este VM
            // val uiState = viewModel.uiState

            // Llama a DetallesPagoScreen pasándole el viewModel
            DetallesPagoScreen(
                navController = navController
                // uiState = uiState,       // 👈 Pasa el estado
                // viewModel = viewModel    // 👈 Pasa el ViewModel completo
            )
            // --- FIN CORRECCIÓN ---
        }
        // ------------------------------------

        composable("pago") {
            PagoScreen(navController = navController)
        }
        composable("pago_finalizado"){
            PagoFinalizadoScreen(navController = navController)
        }
        composable("busqueda") {
            BusquedaScreen(navController = navController)
        }

        // --- RUTA "START" (ELIMINADA) ---
        // composable("start"){
        //     SessionStartScreen(navController = navController)
        // }
    }
}