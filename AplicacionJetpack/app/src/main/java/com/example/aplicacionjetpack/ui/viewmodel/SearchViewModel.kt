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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val allProducts: List<ProductResponse> = emptyList(),
    val searchResults: List<ProductResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // --- Estado de los Filtros ---
    val showFilterSheet: Boolean = false,
    val allCategories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..1000f,
    val currentPriceValues: ClosedFloatingPointRange<Float> = 0f..1000f,
    val sortBy: SortOption = SortOption.NONE
)

enum class SortOption(val displayName: String) {
    NONE("Relevancia"),
    PRICE_ASC("Precio: Menor a Mayor"),
    PRICE_DESC("Precio: Mayor a Menor"),
    NAME_ASC("Nombre: A-Z"),
    NAME_DESC("Nombre: Z-A")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    private var searchJob: Job? = null
    private val TAG = "SearchVM"

    init {
        loadAllProductsForSearch()
    }

    private fun loadAllProductsForSearch() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val allFetchedProducts = mutableListOf<ProductResponse>()
            var currentPage = 0
            var hasMorePages = true

            while (hasMorePages) {
                val result = productRepository.getAllProducts(page = currentPage, size = 100)
                result.onSuccess { pagedResponse ->
                    allFetchedProducts.addAll(pagedResponse.content)
                    hasMorePages = !pagedResponse.last
                    currentPage++
                }.onFailure {
                    uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los productos.")
                    hasMorePages = false
                }
            }

            val categories = allFetchedProducts.mapNotNull { it.nombreTipo }.distinct().sorted()
            val maxPrice = allFetchedProducts.maxOfOrNull { it.precio } ?: BigDecimal(1000)
            val priceRange = 0f..maxPrice.toFloat()

            uiState = uiState.copy(
                isLoading = false,
                allProducts = allFetchedProducts,
                allCategories = categories,
                priceRange = priceRange,
                currentPriceValues = priceRange
            )
            Log.d(TAG, "Carga completa. ${allFetchedProducts.size} productos, ${categories.size} categorías. Rango de precio: $priceRange")
        }
    }

    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            applyFiltersAndSearch()
        }
    }

    private fun applyFiltersAndSearch() {
        val filtered = uiState.allProducts.filter { product ->
            val matchesQuery = product.nombre.contains(uiState.searchQuery, ignoreCase = true) ||
                    product.descripcion?.contains(uiState.searchQuery, ignoreCase = true) == true
            val matchesCategory = uiState.selectedCategories.isEmpty() ||
                    uiState.selectedCategories.contains(product.nombreTipo)
            val price = product.precio.toFloat()
            val matchesPrice = price >= uiState.currentPriceValues.start && price <= uiState.currentPriceValues.endInclusive
            matchesQuery && matchesCategory && matchesPrice
        }

        val sorted = when (uiState.sortBy) {
            SortOption.PRICE_ASC -> filtered.sortedBy { it.precio }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.precio }
            SortOption.NAME_ASC -> filtered.sortedBy { it.nombre }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.nombre }
            SortOption.NONE -> filtered
        }

        uiState = uiState.copy(searchResults = sorted)
    }

    // --- EVENTOS DEL MODAL DE FILTROS ---
    fun onFilterButtonClick() { uiState = uiState.copy(showFilterSheet = true) }
    fun onFilterSheetDismiss() { uiState = uiState.copy(showFilterSheet = false) }

    fun onCategorySelected(category: String) {
        val newSelection = uiState.selectedCategories.toMutableSet()
        if (newSelection.contains(category)) {
            newSelection.remove(category)
        } else {
            newSelection.add(category)
        }
        uiState = uiState.copy(selectedCategories = newSelection)
    }

    fun onPriceRangeChanged(newRange: ClosedFloatingPointRange<Float>) {
        uiState = uiState.copy(currentPriceValues = newRange)
    }

    fun onSortOptionSelected(sortOption: SortOption) {
        uiState = uiState.copy(sortBy = sortOption)
    }

    fun applyFiltersFromSheet() {
        applyFiltersAndSearch()
        onFilterSheetDismiss()
    }

    fun clearFilters() {
        uiState = uiState.copy(
            selectedCategories = emptySet(),
            currentPriceValues = uiState.priceRange,
            sortBy = SortOption.NONE
        )
        applyFiltersAndSearch()
        onFilterSheetDismiss()
    }
}
