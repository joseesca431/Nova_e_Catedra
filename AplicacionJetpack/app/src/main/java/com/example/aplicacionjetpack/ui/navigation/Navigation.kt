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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ... (splash, login, register no cambian) ...
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
            // obtiene la instancia correcta del ViewModel con Hilt
            val registerViewModel: RegisterViewModel = hiltViewModel()

            RegisterScreen(
                navController = navController,
                uiState = registerViewModel.uiState,
                onPrimerNombreChange = registerViewModel::onPrimerNombreChange,
                onSegundoNombreChange = registerViewModel::onSegundoNombreChange,
                onPrimerApellidoChange = registerViewModel::onPrimerApellidoChange,
                onSegundoApellidoChange = registerViewModel::onSegundoApellidoChange,
                onEmailChange = registerViewModel::onEmailChange,
                onUsernameChange = registerViewModel::onUsernameChange,
                onPasswordChange = registerViewModel::onPasswordChange,
                onConfirmPasswordChange = registerViewModel::onConfirmPasswordChange,
                onTelefonoChange = registerViewModel::onTelefonoChange,
                onDuiChange = registerViewModel::onDuiChange,
                onDireccionChange = registerViewModel::onDireccionChange,
                onRegisterClick = registerViewModel::onRegisterClicked,
                onFechaNacimientoClicked = registerViewModel::onFechaNacimientoClicked,
                onCalendarDismiss = registerViewModel::onCalendarDismiss,
                onDateSelected = registerViewModel::onDateSelected,
                onDismissErrorDialog = registerViewModel::dismissErrorDialog
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
                    navController.navigate("busqueda")
                }
            )
        }

        composable("busqueda") {
            val viewModel: SearchViewModel = hiltViewModel()
            BusquedaScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onQueryChange = viewModel::onSearchQueryChanged,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.idProducto}")
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
                onPagarClick = { idCarrito ->
                    if (idCarrito > 0) {
                        navController.navigate("confirm_address/$idCarrito")
                    }
                }
            )
        }

        composable(
            route = "confirm_address/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L
            ConfirmAddressScreen(
                navController = navController,
                idCarrito = idCarrito,
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = "detalles_pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) {
                    navController.getBackStackEntry("confirm_address/{idCarrito}")
                }
            )
            DetallesPagoScreen(
                navController = navController,
                idCarrito = idCarrito,
                checkoutViewModel = checkoutViewModel
            )
        }

        composable(
            route = "pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            val idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) {
                    navController.getBackStackEntry("confirm_address/{idCarrito}")
                }
            )
            PagoScreen(
                navController = navController,
                idCarrito = idCarrito,
                viewModel = checkoutViewModel
            )
        }

        composable("pago_finalizado") {
            PagoFinalizadoScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // --- 👇👇👇 ¡¡¡AÑADIMOS LA NUEVA RUTA!!! 👇👇👇 ---
        composable("editar_profile") {
            EditarProfileScreen(navController = navController)
        }
        // --- --------------------------------------- ---

        composable("historial_compras") {
            HistorialComprasScreen(navController = navController)
        }

        composable("notificaciones") {
            NotificacionesScreen(navController = navController)
        }
    }
}
