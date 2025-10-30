package com.example.aplicacionjetpack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacionjetpack.ui.screens.*
import com.example.aplicacionjetpack.ui.viewmodel.*

// NO MÁS HELPERS COMPLICADOS NI GRAFOS ANIDADOS

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
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClicked
            )
        }

        composable("register") {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onPrimerNombreChange = viewModel::onPrimerNombreChange,
                onPrimerApellidoChange = viewModel::onPrimerApellidoChange,
                onFechaNacimientoClicked = viewModel::onFechaNacimientoClicked,
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

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onRefresh = viewModel::refreshProducts,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.idProducto}")
                },
                onSearchClick = {
                    // navController.navigate("busqueda")
                }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                navController = navController,
                productId = productId
            )
        }

        composable("cart") {
            CarritoScreen(
                navController = navController,
                // Le pasamos la lógica de navegación directamente aquí
                onPagarClick = { idCarrito ->
                    // --- LA ÚNICA LÓGICA QUE IMPORTA ---
                    // Solo navegamos si el ID es válido.
                    if (idCarrito > 0) {
                        // Navegamos directamente a la primera pantalla del flujo.
                        navController.navigate("confirm_address/$idCarrito")
                    }
                    // Si idCarrito es 0 o nulo, NO HACE NADA. No hay crash.
                }
            )
        }

        // --- EL CHECKOUT SIMPLIFICADO, DIRECTO Y FUNCIONAL ---
        composable(
            route = "confirm_address/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L
            ConfirmAddressScreen(
                navController = navController,
                idCarrito = idCarrito,
                viewModel = hiltViewModel() // Hilt crea la primera instancia aquí.
            )
        }

        composable(
            route = "detalles_pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L

            // Hilt es inteligente: busca la instancia de ViewModel creada en la pantalla anterior.
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) {
                    navController.getBackStackEntry("confirm_address/{idCarrito}")
                }
            )

            DetallesPagoScreen(
                navController = navController,
                idCarrito = idCarrito,
                checkoutViewModel = checkoutViewModel
                // El CarritoViewModel se crea dentro con hiltViewModel() por defecto.
            )
        }

        composable(
            route = "pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L

            // Reutilizamos el mismo ViewModel del flujo.
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) {
                    navController.getBackStackEntry("confirm_address/{idCarrito}")
                }
            )

            PagoScreen(
                navController = navController,
                idCarrito = idCarrito,
                viewModel = checkoutViewModel
                // El CarritoViewModel se crea dentro con hiltViewModel() por defecto.
            )
        }
        // --- -------------------------------------------- ---

        composable("pago_finalizado") {
            PagoFinalizadoScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
