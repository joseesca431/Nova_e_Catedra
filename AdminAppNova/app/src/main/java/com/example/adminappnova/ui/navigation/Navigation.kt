package com.example.adminappnova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// --- Importa TODOS tus Screens ---
import com.example.adminappnova.ui.screens.CategoriesDetailScreen
import com.example.adminappnova.ui.screens.CategoriesScreen
import com.example.adminappnova.ui.screens.DetallesPagoScreen
import com.example.adminappnova.ui.screens.HomeScreen
import com.example.adminappnova.ui.screens.LoginScreen
import com.example.adminappnova.ui.screens.PedidosScreen
import com.example.adminappnova.ui.screens.ProductCategoriesScreen
import com.example.adminappnova.ui.screens.SplashScreen
// --- Importa TODOS tus ViewModels ---
import com.example.adminappnova.ui.viewmodel.CategoriesViewModel
import com.example.adminappnova.ui.viewmodel.HomeViewModel
import com.example.adminappnova.ui.viewmodel.LoginViewModel
import com.example.adminappnova.ui.viewmodel.PedidosViewModel
import com.example.adminappnova.ui.viewmodel.ProductListViewModel // Renombrado desde CategoriesDetailViewModel
import com.example.adminappnova.ui.viewmodel.ProductDetailViewModel // Renombrado desde ProductCategoriesViewModel
import com.example.adminappnova.ui.viewmodel.OrderDetailViewModel // Renombrado desde DetallesPagoViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash"
    ){
        // --- Pantalla Splash ---
        composable("splash"){
            SplashScreen(navController)
        }

        // --- Pantalla Home ---
        composable("start"){
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            HomeScreen(
                navController = navController,
                uiState = uiState
                // evento: onRefresh = viewModel::refreshData // Ejemplo si añades un Pull-to-refresh
            )
        }

        // --- Pantalla Login ---
        composable("login"){
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            LoginScreen(
                navController = navController,
                uiState = uiState,
                onUsuarioChange = viewModel::onUsuarioChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClicked
            )
        }

        // --- Pantalla Lista de Categorías ---
        composable("categories") {
            val viewModel: CategoriesViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            CategoriesScreen(
                navController = navController,
                uiState = uiState,
                onAddCategoryClick = viewModel::onAddCategoryClicked,
                onDismissAddDialog = viewModel::onDismissAddDialog,
                // --- CORREGIDO TYPO ---
                onNewCategoryNameChange = viewModel::onNewCategoryTypeChange, // Era onNewCategoryNameChange
                // --------------------
                onConfirmAddCategory = viewModel::onConfirmAddCategory
            )
        }

        // --- Pantalla Lista de Pedidos ---
        composable("pedidos") {
            val viewModel: PedidosViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            PedidosScreen(
                navController = navController,
                uiState = uiState,
                onLoadNextPage = viewModel::loadNextPage,
                onRefresh = viewModel::refreshPedidos,
                // --- 👇 AÑADE ESTA LÍNEA 👇 ---
                onChangeFilter = viewModel::changeFilter // Pasa la referencia a la función del VM
                // -------------------------
                // onPedidoClick = { pedidoId -> navController.navigate("detalles_pago/$pedidoId") }
            )
        }
        // --- Pantalla Detalle de Categoría (Lista de Productos) ---
        composable(
            route = "categories_detail/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            // --- CORREGIDO VIEWMODEL ---
            val viewModel: ProductListViewModel = hiltViewModel() // Usa el ViewModel correcto
            // -------------------------
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            CategoriesDetailScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState, // 👈 Pasar uiState
                // Pasar eventos necesarios desde el ViewModel
                onProductClick = { product ->
                    // Navega a detalle, pasa categoryName y productId
                    navController.navigate("product_categories/$categoryName?productId=${product.idProducto}")
                },
                onAddProductClick = {
                    // Navega a detalle, pasa categoryName pero NO productId (para crear)
                    navController.navigate("product_categories/$categoryName")
                },
                onRefresh = viewModel::refreshProducts // 👈 Pasar evento para refrescar
            )
        }

        // --- Pantalla Detalle/Crear Producto ---
        composable(
            route = "product_categories/{categoryName}?productId={productId}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            // --- CORREGIDO VIEWMODEL ---
            val viewModel: ProductDetailViewModel = hiltViewModel() // Usa el ViewModel correcto
            // -------------------------
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            // Escucha los flags de éxito para navegar atrás
            LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
                if (uiState.saveSuccess || uiState.deleteSuccess) {
                    navController.popBackStack() // Vuelve a la pantalla anterior (lista de productos)
                }
            }

            ProductCategoriesScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState, // 👈 Pasar uiState
                // Pasar TODOS los eventos necesarios desde el ViewModel
                onNombreChange = viewModel::onNombreChange,
                onDescripcionChange = viewModel::onDescripcionChange,
                onCantidadChange = viewModel::onCantidadChange, // Corregido nombre evento
                onPrecioChange = viewModel::onPrecioChange,
                onCostoChange = viewModel::onCostoChange,       // Añadido evento
                onCantidadPuntosChange = viewModel::onCantidadPuntosChange, // Añadido evento
                // onAddImageClick = viewModel::onAddImageClicked, // Descomentar si implementas imagen
                onDeleteClick = viewModel::onDeleteClicked,
                onSaveClick = viewModel::onSaveClicked
            )
        }

        // --- Pantalla Detalles de Pedido ---
        composable(
            route = "detalles_pago/{pedidoId}", // Cambiado nombre de ruta si prefieres order_detail
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            // --- CORREGIDO VIEWMODEL ---
            val viewModel: OrderDetailViewModel = hiltViewModel() // Usa el ViewModel correcto
            // -------------------------
            val uiState = viewModel.uiState

            DetallesPagoScreen(
                navController = navController,
                uiState = uiState, // 👈 Pasar uiState
                // Pasar eventos si los necesitas en la UI
                onConfirmarPedido = viewModel::confirmarPedido,
                onIniciarEnvio = viewModel::iniciarEnvio,
                onMarcarEntregado = viewModel::marcarEntregado
                // onCancelarPedido = { showCancelDialog = true } // Podrías necesitar un diálogo para el motivo
            )
        }
    }
}