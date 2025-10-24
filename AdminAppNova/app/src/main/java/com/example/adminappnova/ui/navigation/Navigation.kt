package com.example.adminappnova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adminappnova.ui.screens.CategoriesDetailScreen
import com.example.adminappnova.ui.screens.CategoriesScreen
import com.example.adminappnova.ui.screens.DetallesPagoScreen
import com.example.adminappnova.ui.screens.HomeScreen
import com.example.adminappnova.ui.screens.LoginScreen
import com.example.adminappnova.ui.screens.PedidosScreen
import com.example.adminappnova.ui.screens.ProductCategoriesScreen
import com.example.adminappnova.ui.screens.SplashScreen
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash"

    ){
        composable("splash"){
            SplashScreen(navController)
        }
        composable("start"){
            HomeScreen(navController)
        }
        composable("login"){
            LoginScreen(navController)
        }
        composable("categories") {
            CategoriesScreen(navController)
        }
        composable("pedidos") {
            PedidosScreen(navController)
        }
        composable("categories_detail/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"
            CategoriesDetailScreen(navController, categoryName)
        }

        composable("product_categories/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Categoría"
            ProductCategoriesScreen(navController, categoryName)
        }
        composable("detalles_pago") {
            DetallesPagoScreen(navController)
        }
    }
}