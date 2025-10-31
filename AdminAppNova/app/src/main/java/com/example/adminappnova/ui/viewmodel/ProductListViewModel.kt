package com.example.adminappnova.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.ProductResponse
import com.example.adminappnova.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

data class ProductListUiState(
    val isLoading: Boolean = true,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(ProductListUiState())
        private set

    private val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Desconocida"

    init {
        loadAllProductsAndFilter()
    }

    private fun loadAllProductsAndFilter() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d("ProductListVM", "Cargando productos para categoría: $categoryName")

            val result: Result<PagedResponse<ProductResponse>> = productRepository.getAllProducts(page = 0, size = 1000)

            result.onSuccess { pagedResponse ->
                Log.d("ProductListVM", "Productos recibidos: ${pagedResponse.content.size}")

                // --- 👇👇👇 ¡¡¡LA LÓGICA SIMPLIFICADA DE LA VICTORIA!!! 👇👇👇 ---
                // pagedResponse.content ya es una List<ProductResponse> limpia.
                val filteredList = pagedResponse.content
                    .filter { product ->
                        // Filtramos directamente sobre el objeto 'product'
                        product.nombreTipo?.trim().equals(categoryName.trim(), ignoreCase = true)
                    }
                // --- ---------------------------------------------------- ---

                Log.d("ProductListVM", "Productos filtrados: ${filteredList.size}")

                if (filteredList.isEmpty()) {
                    val errorMessage = if (pagedResponse.content.isNotEmpty()) {
                        "No se encontraron productos para la categoría '$categoryName'."
                    } else {
                        "No hay productos disponibles."
                    }
                    uiState = uiState.copy(isLoading = false, products = emptyList(), error = errorMessage)
                } else {
                    uiState = uiState.copy(isLoading = false, products = filteredList, error = null)
                }

            }.onFailure { e ->
                Log.e("ProductListVM", "Error al cargar productos", e)
                uiState = uiState.copy(isLoading = false, error = "Error al cargar productos: ${e.message}")
            }
        }
    }

    fun refreshProducts() {
        loadAllProductsAndFilter()
    }
}
