package com.example.adminappnova.ui.viewmodel

import android.util.Log // Importar Log para depuración
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.PagedResponse // Asegúrate que es el DTO HATEOAS corregido
import com.example.adminappnova.data.dto.ProductResponse
import com.example.adminappnova.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI ---
data class ProductListUiState(
    val isLoading: Boolean = true,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null
)

// --- ViewModel ---
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository, // Inyecta el repositorio real
    savedStateHandle: SavedStateHandle // Para recibir argumentos de navegación
) : ViewModel() {

    // Estado observable por la UI
    var uiState by mutableStateOf(ProductListUiState())
        private set

    // Obtiene el nombre de la categoría desde los argumentos de navegación
    private val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Desconocida"
    // TODO: Considera obtener/pasar el ID de la categoría para un filtrado más eficiente en el backend

    // Se ejecuta al crear el ViewModel
    init {
        // Carga inicial de productos (usando filtrado temporal en cliente)
        loadAllProductsAndFilter()
    }

    // --- Carga todos los productos y filtra por nombre de categoría ---
    // NOTA: Esto sigue siendo ineficiente si hay muchísimos productos.
    // Lo ideal es filtrar en el backend por ID de categoría.
    private fun loadAllProductsAndFilter() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null) // Indicar carga
            Log.d("ProductListVM", "Cargando productos para categoría: $categoryName")

            // Llama a getAllProducts (pide una página grande para intentar obtener todos)
            val result: Result<PagedResponse<ProductResponse>> = productRepository.getAllProducts(page = 0, size = 200) // Pide un tamaño grande

            result.onSuccess { pagedResponse ->
                Log.d("ProductListVM", "Productos recibidos: ${pagedResponse.content.size}")
                // Filtra los productos por el campo 'nombreTipo' que viene en ProductResponse
                val filteredList = pagedResponse.content.filter { product ->
                    // Compara ignorando mayúsculas/minúsculas y quitando espacios extra
                    product.nombreTipo?.trim().equals(categoryName.trim(), ignoreCase = true)
                }
                Log.d("ProductListVM", "Productos filtrados: ${filteredList.size}")

                if (filteredList.isEmpty()) {
                    // Si no se encontraron productos para esta categoría
                    val errorMessage = if (pagedResponse.content.isNotEmpty()) {
                        // El backend devolvió productos, pero ninguno coincidió
                        "No se encontraron productos para la categoría '$categoryName'."
                    } else {
                        // El backend no devolvió ningún producto en absoluto
                        "No hay productos disponibles."
                    }
                    uiState = uiState.copy(isLoading = false, products = emptyList(), error = errorMessage)
                } else {
                    // Actualiza el estado con la lista filtrada
                    uiState = uiState.copy(isLoading = false, products = filteredList, error = null)
                }

            }.onFailure { e ->
                // Error al cargar la lista completa
                Log.e("ProductListVM", "Error al cargar productos", e)
                uiState = uiState.copy(isLoading = false, error = "Error al cargar productos: ${e.message}")
            }
        }
    }

    // Función para refrescar la lista
    fun refreshProducts() {
        // Vuelve a ejecutar la carga y filtrado
        loadAllProductsAndFilter()
    }
}