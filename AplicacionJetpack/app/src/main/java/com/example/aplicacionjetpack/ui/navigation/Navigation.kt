package com.example.aplicacionjetpack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionjetpack.ui.screens.*
import com.example.aplicacionjetpack.ui.viewmodel.*

// --- 👇👇👇 ¡GRAFO DE CHECKOUT RECONSTRUIDO! 👇👇👇 ---
fun NavGraphBuilder.checkoutGraph(navController: NavController) {
    // La ruta del grafo ahora INCLUYE el parámetro. Esto es clave.
    navigation(
        startDestination = "confirm_address/{idCarrito}",
        route = "checkout_flow/{idCarrito}",
        arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
    ) {
        composable(
            route = "confirm_address/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            // Obtenemos el ViewModel con el ámbito del grafo "checkout_flow/{idCarrito}"
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) { navController.getBackStackEntry("checkout_flow/{idCarrito}") }
            )
            ConfirmAddressScreen(
                navController = navController,
                idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L,
                viewModel = checkoutViewModel
            )
        }

        composable(
            route = "detalles_pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            // Obtenemos la MISMA instancia del ViewModel
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) { navController.getBackStackEntry("checkout_flow/{idCarrito}") }
            )
            DetallesPagoScreen(
                navController = navController,
                idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L,
                checkoutViewModel = checkoutViewModel
            )
        }

        composable(
            route = "pago/{idCarrito}",
            arguments = listOf(navArgument("idCarrito") { type = NavType.LongType })
        ) { backStackEntry ->
            // Obtenemos la MISMA instancia del ViewModel
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(
                remember(backStackEntry) { navController.getBackStackEntry("checkout_flow/{idCarrito}") }
            )
            PagoScreen(
                navController = navController,
                idCarrito = backStackEntry.arguments?.getLong("idCarrito") ?: 0L,
                viewModel = checkoutViewModel
            )
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ... (splash, login, register, etc. no cambian) ...
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
                onSegundoNombreChange = viewModel::onSegundoNombreChange,
                onPrimerApellidoChange = viewModel::onPrimerApellidoChange,
                onSegundoApellidoChange = viewModel::onSegundoApellidoChange,
                onEmailChange = viewModel::onEmailChange,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onTelefonoChange = viewModel::onTelefonoChange,
                onDuiChange = viewModel::onDuiChange,
                onDireccionChange = viewModel::onDireccionChange,
                onRegisterClick = viewModel::onRegisterClicked,
                onFechaNacimientoClicked = viewModel::onFechaNacimientoClicked,
                onCalendarDismiss = viewModel::onCalendarDismiss,
                onDateSelected = viewModel::onDateSelected,
                onDismissErrorDialog = viewModel::dismissErrorDialog
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
                viewModel = viewModel,
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
                productId = productId,
                viewModel = hiltViewModel(),
                carritoViewModel = hiltViewModel()
            )
        }

        composable("cart") {
            CarritoScreen(
                navController = navController,
                // --- 👇👇👇 ¡LA LLAMADA AHORA CONSTRUYE LA RUTA COMPLETA! 👇👇👇 ---
                onPagarClick = { idCarrito ->
                    if (idCarrito > 0) {
                        // Construimos la ruta que el grafo "checkout_flow/{idCarrito}" espera.
                        navController.navigate("checkout_flow/$idCarrito")
                    }
                }
                // --- --------------------------------------------------------- ---
            )
        }

        // --- El grafo se llama igual, pero ahora está definido para recibir un ID ---
        checkoutGraph(navController)

        composable("pago_finalizado") {
            PagoFinalizadoScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("editar_profile") {
            EditarProfileScreen(navController = navController)
        }

        composable("historial_compras") {
            HistorialComprasScreen(navController = navController)
        }

        composable("notificaciones") {
            NotificacionesScreen(navController = navController)
        }
    }
}
