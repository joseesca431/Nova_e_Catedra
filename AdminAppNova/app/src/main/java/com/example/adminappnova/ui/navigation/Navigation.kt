package com.example.adminappnova.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.adminappnova.ui.viewmodel.OrderDetailViewModel
import com.example.adminappnova.ui.viewmodel.PedidosViewModel
import com.example.adminappnova.ui.viewmodel.ProductDetailViewModel
import com.example.adminappnova.ui.viewmodel.ProductListViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
                uiState = uiState
            )
        }

        // --- Pantalla Lista de Pedidos (Corregida) ---
        composable("pedidos") {
            val viewModel: PedidosViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            PedidosScreen(
                navController = navController,
                uiState = uiState,
                onLoadNextPage = viewModel::loadNextPage,
                onRefresh = viewModel::refreshPedidos,
                onChangeFilter = viewModel::changeFilter
            )
        }

        // --- Pantalla Detalle de Categoría (Lista de Productos) (Corregida) ---
        composable(
            route = "categories_detail/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: ProductListViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            CategoriesDetailScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState,
                onProductClick = { product ->
                    navController.navigate("product_categories/$categoryName?productId=${product.idProducto}")
                },
                onAddProductClick = {
                    navController.navigate("product_categories/$categoryName")
                },
                onRefresh = viewModel::refreshProducts
            )
        }

        // --- Pantalla Detalle/Crear Producto (Corregida) ---
        composable(
            route = "product_categories/{categoryName}?productId={productId}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val viewModel: ProductDetailViewModel = hiltViewModel()
            val uiState = viewModel.uiState
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"

            LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
                if (uiState.saveSuccess || uiState.deleteSuccess) {
                    navController.popBackStack()
                }
            }

            ProductCategoriesScreen(
                navController = navController,
                categoryName = categoryName,
                uiState = uiState,
                onNombreChange = viewModel::onNombreChange,
                onDescripcionChange = viewModel::onDescripcionChange,
                onCantidadChange = viewModel::onCantidadChange,
                onPrecioChange = viewModel::onPrecioChange,
                onCostoChange = viewModel::onCostoChange,
                onCantidadPuntosChange = viewModel::onCantidadPuntosChange,
                onDeleteClick = viewModel::onDeleteClicked,
                onSaveClick = viewModel::onSaveClicked
            )
        }

        // --- 👇👇👇 ¡¡¡LA SECCIÓN CORREGIDA DE LA VICTORIA!!! 👇👇👇 ---
        composable(
            // La ruta ahora acepta DOS argumentos, como lo definimos antes
            route = "detalles_pago/{pedidoId}/{userId}",
            arguments = listOf(
                navArgument("pedidoId") { type = NavType.LongType },
                navArgument("userId") { type = NavType.LongType } // ¡Definimos el nuevo argumento!
            )
        ) { backStackEntry ->
            // Hilt se encarga de obtener los argumentos y pasarlos al ViewModel.
            // No necesitamos hacer nada con backStackEntry aquí.
            val viewModel: OrderDetailViewModel = hiltViewModel()

            // ¡Llamamos a la pantalla con los parámetros CORRECTOS!
            // Ya no existe 'uiState' ni 'viewModel' como parámetros separados.
            // La pantalla DetallesPagoScreen obtiene el viewModel con hiltViewModel() directamente.
            DetallesPagoScreen(
                navController = navController,
                uiState = viewModel.uiState,
                viewModel = viewModel
            )
        }
        // --- -------------------------------------------------------- ---
    }
}
