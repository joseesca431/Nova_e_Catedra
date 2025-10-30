package com.example.aplicacionjetpack.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado de la UI para la pantalla de búsqueda
data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<ProductResponse> = emptyList(),
    val allProducts: List<ProductResponse> = emptyList(), // Lista completa para filtrar
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    private var searchJob: Job? = null

    init {
        // Carga todos los productos en segundo plano al iniciar
        loadAllProducts()
    }

    // Carga la lista completa de productos para poder filtrar localmente
    private fun loadAllProducts() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            // NOTA: Esto no es ideal para miles de productos.
            // La solución correcta sería un endpoint de búsqueda en el backend.
            // Por ahora, para que funcione, cargamos todo.
            val result = productRepository.getAllProducts(page = 0, size = 100) // Carga hasta 100 productos
            result.onSuccess { pagedResponse ->
                uiState = uiState.copy(
                    isLoading = false,
                    allProducts = pagedResponse.content
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo cargar la lista de productos para buscar."
                )
            }
        }
    }

    // --- 👇👇👇 ¡¡¡LA FUNCIÓN QUE FALTABA!!! 👇👇👇 ---
    // Se llama cada vez que el texto de búsqueda cambia
    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)

        // Cancela la búsqueda anterior para no sobrecargar
        searchJob?.cancel()

        // Inicia una nueva búsqueda después de un breve retraso (debounce)
        searchJob = viewModelScope.launch {
            delay(300L) // Espera 300ms antes de buscar

            if (query.isBlank()) {
                uiState = uiState.copy(searchResults = emptyList())
            } else {
                // Filtra la lista completa de productos localmente
                val results = uiState.allProducts.filter {
                    it.nombre.contains(query, ignoreCase = true)
                }
                uiState = uiState.copy(searchResults = results)
            }
        }
    }
}
