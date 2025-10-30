package com.example.aplicacionjetpack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// --- Importa TODOS tus Screens ---
import com.example.aplicacionjetpack.ui.screens.* // Importa todos
// --- Importa TODOS tus ViewModels ---
import com.example.aplicacionjetpack.ui.viewmodel.LoginViewModel
import com.example.aplicacionjetpack.ui.viewmodel.RegisterViewModel
import com.example.aplicacionjetpack.ui.viewmodel.HomeViewModel
import com.example.aplicacionjetpack.ui.viewmodel.SearchViewModel
// Si en algún punto necesitas referenciar ProductDetailViewModel aquí, puedes importarlo:
// import com.example.aplicacionjetpack.ui.viewmodel.ProductDetailViewModel

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
            LoginScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClicked
            )
        }

        // --- PANTALLA REGISTRO ---
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

        // --- PANTALLA HOME (CORREGIDA) ---
        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onRefresh = viewModel::refreshProducts,
                onProductClick = { product ->
                    // Navega al detalle pasando el ID real
                    navController.navigate("product_detail/${product.idProducto}")
                },
                onSearchClick = {
                    navController.navigate("busqueda")
                }
            )
        }

        // --- PANTALLA BÚSQUEDA (CORREGIDA) ---
        composable("busqueda") {
            val viewModel: SearchViewModel = hiltViewModel()
            BusquedaScreen(
                navController = navController,
                uiState = viewModel.uiState,
                onQueryChange = viewModel::onSearchQueryChange,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.idProducto}")
                }
            )
        }

        // --- PANTALLA DETALLE PRODUCTO (RUTA ACTUALIZADA, ARREGLADA) ---
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Extrae el productId de los argumentos (si no viene, queda 0L)
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

            /*
             * Nota:
             * - Tu ProductDetailScreen, tal como te entregué, tiene la firma:
             *   ProductDetailScreen(navController: NavController, productId: Long, viewModel: ProductDetailViewModel = hiltViewModel())
             *
             * - Por eso aquí basta con pasar navController y productId. El screen pedirá su ViewModel con hiltViewModel().
             *
             * Si en tu proyecto prefieres manejar el ViewModel desde aquí y pasarlo al Screen,
             * descomenta la siguiente línea y pásalo:
             *
             * val detailVm: ProductDetailViewModel = hiltViewModel()
             * ProductDetailScreen(navController = navController, productId = productId, viewModel = detailVm)
             */

            ProductDetailScreen(
                navController = navController,
                productId = productId
            )
        }

        // --- OTRAS PANTALLAS (Aún no refactorizadas) ---
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("cart") {
            CarritoScreen(navController = navController)
        }
        composable("confirm_address") {
            ConfirmAddressScreen(navController = navController)
        }
        composable("detalles_pago") {
            DetallesPagoScreen(navController = navController)
        }
        composable("pago") {
            PagoScreen(navController = navController)
        }
        composable("pago_finalizado") {
            PagoFinalizadoScreen(navController = navController)
        }
    }
}
