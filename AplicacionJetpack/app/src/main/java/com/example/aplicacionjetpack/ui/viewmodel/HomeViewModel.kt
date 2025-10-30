package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null
    // TODO: Añadir lógica de paginación (isLoadingMore, currentPage, canLoadMore)
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    private val TAG = "HomeVM"

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d(TAG, "Cargando productos (página 0)...")

            // Llama al repo para la primera página (ej: 20 productos)
            val result = productRepository.getAllProducts(page = 0, size = 20)

            result.onSuccess { pagedResponse ->
                Log.d(TAG, "Productos cargados: ${pagedResponse.content.size}")
                uiState = uiState.copy(
                    isLoading = false,
                    products = pagedResponse.content // Muestra solo la primera página
                )
            }.onFailure { e ->
                Log.e(TAG, "Error cargando productos", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los productos.")
            }
        }
    }

    // Función para ser llamada por SwipeRefresh
    fun refreshProducts() {
        if (!uiState.isLoading) {
            loadProducts()
        }
    }
}