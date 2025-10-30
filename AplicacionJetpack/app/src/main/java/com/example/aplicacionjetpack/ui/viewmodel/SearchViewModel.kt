package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
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

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    private val _allProducts: List<ProductResponse> = emptyList(), // Lista completa
    val error: String? = null
) {
    // Lista filtrada que la UI observará
    val searchResults: List<ProductResponse> by derivedStateOf {
        if (searchQuery.isBlank()) {
            emptyList() // No mostrar nada si la búsqueda está vacía
        } else {
            // Filtra por nombre, descripción o tipo
            _allProducts.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                        it.descripcion?.contains(searchQuery, ignoreCase = true) == true ||
                        it.nombreTipo?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    private val TAG = "SearchVM"

    init {
        // Carga una página grande de productos para la búsqueda en cliente
        loadAllProductsForSearch()
    }

    private fun loadAllProductsForSearch() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d(TAG, "Cargando TODOS los productos para búsqueda...")

            // Pide una página grande. Para una app real, el backend debería hacer la búsqueda.
            val result = productRepository.getAllProducts(page = 0, size = 200)

            result.onSuccess { pagedResponse ->
                Log.d(TAG, "Total productos para búsqueda: ${pagedResponse.content.size}")
                uiState = uiState.copy(
                    isLoading = false,
                    _allProducts = pagedResponse.content // Guarda la lista completa
                )
            }.onFailure { e ->
                Log.e(TAG, "Error cargando todos los productos", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar productos.")
            }
        }
    }

    // Evento llamado por el TextField
    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }
}