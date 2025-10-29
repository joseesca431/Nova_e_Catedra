package com.example.adminappnova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // <-- Importación necesaria
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
import com.example.adminappnova.ui.viewmodel.ProductListViewModel // Renombrado para CategoriesDetailScreen
import com.example.adminappnova.ui.viewmodel.ProductDetailViewModel // Renombrado para ProductCategoriesScreen
import com.example.adminappnova.ui.viewmodel.OrderDetailViewModel // Renombrado para DetallesPagoScreen

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

        // --- Pantalla Home (Corregida) ---
        composable("start"){
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            HomeScreen(
                navController = navController,
                uiState = uiState // 👈 Pasa el estado
            )
        }

        // --- Pantalla Login (Ya estaba correcta) ---
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

        // --- Pantalla Lista de Categorías (Corregida) ---
        composable("categories") {
            val viewModel: CategoriesViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            CategoriesScreen(
                navController = navController,
                uiState = uiState,
                onAddCategoryClick = viewModel::onAddCategoryClicked,
                onDismissAddDialog = viewModel::onDismissAddDialog,
                // Corregido: Pasa el evento que actualiza el 'tipo' (que en la UI se llama 'Name')
                onNewCategoryNameChange = viewModel::onNewCategoryTypeChange,
                onConfirmAddCategory = viewModel::onConfirmAddCategory
            )
        }

        // --- Pantalla Lista de Pedidos (Corregida) ---
        composable("pedidos") {
            val viewModel: PedidosViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            PedidosScreen(
                navController = navController,
                uiState = uiState, // 👈 Pasa el estado
                onLoadNextPage = viewModel::loadNextPage, // 👈 Pasa evento
                onRefresh = viewModel::refreshPedidos, // 👈 Pasa evento
                onChangeFilter = viewModel::changeFilter // 👈 Pasa evento
            )
        }

        // --- Pantalla Detalle de Categoría (Lista de Productos) (Corregida) ---
        composable(
            route = "categories_detail/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: ProductListViewModel = hiltViewModel() // Usa el VM de Lista de Productos
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            CategoriesDetailScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState, // 👈 Pasa el estado
                // Pasa los eventos
                onProductClick = { product ->
                    // Navega a detalle, pasa categoryName y productId
                    navController.navigate("product_categories/$categoryName?productId=${product.idProducto}")
                },
                onAddProductClick = {
                    // Navega a detalle, pasa categoryName pero NO productId (para crear)
                    navController.navigate("product_categories/$categoryName")
                },
                onRefresh = viewModel::refreshProducts
            )
        }

        // --- Pantalla Detalle/Crear Producto (Corregida) ---
        composable(
            route = "product_categories/{categoryName}?productId={productId}", // Ruta con argumento opcional
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType; nullable = true } // productId es opcional
            )
        ) { backStackEntry ->
            val viewModel: ProductDetailViewModel = hiltViewModel() // Usa el VM de Detalle de Producto
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            // Efecto para navegar atrás automáticamente al guardar o eliminar
            LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
                if (uiState.saveSuccess || uiState.deleteSuccess) {
                    navController.popBackStack() // Vuelve a la pantalla anterior
                }
            }

            ProductCategoriesScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState, // 👈 Pasa el estado
                // 👈 Pasa TODOS los eventos
                onNombreChange = viewModel::onNombreChange,
                onDescripcionChange = viewModel::onDescripcionChange,
                onCantidadChange = viewModel::onCantidadChange,
                onPrecioChange = viewModel::onPrecioChange,
                onCostoChange = viewModel::onCostoChange,
                onCantidadPuntosChange = viewModel::onCantidadPuntosChange,
                // onAddImageClick = viewModel::onAddImageClicked, // Descomentar si implementas imagen
                onDeleteClick = viewModel::onDeleteClicked,
                onSaveClick = viewModel::onSaveClicked
            )
        }

        // --- Pantalla Detalles de Pedido (Corregida) ---
        composable(
            route = "detalles_pago/{pedidoId}", // <-- RUTA CORREGIDA con argumento
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType }) // <-- DEFINE EL ARGUMENTO
        ) { backStackEntry ->
            val viewModel: OrderDetailViewModel = hiltViewModel() // Usa el VM de Detalle de Pedido
            val uiState = viewModel.uiState

            DetallesPagoScreen(
                navController = navController,
                uiState = uiState, // 👈 Pasa el estado
                // --- 👇 Pasa el ViewModel entero (como lo definimos) 👇 ---
                viewModel = viewModel
                // ---------------------------------------------------
                // Las funciones 'onConfirmar...' etc. se llamarán a través del viewModel
            )
        }
    }
}