package com.example.adminappnova.ui.viewmodel

import android.util.Log // Importar Log para depuración
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.CategoryRequest
import com.example.adminappnova.data.dto.CategoryResponse
import com.example.adminappnova.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI ---
data class CategoriesUiState(
    val isLoading: Boolean = true,
    val categories: List<CategoryResponse> = emptyList(),
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val newCategoryType: String = "", // Campo para el nombre/tipo en el diálogo
    val newCategoryDescription: String = "", // Campo opcional para descripción
    val isAdding: Boolean = false, // Para mostrar carga en el diálogo
    val addError: String? = null // Error específico del diálogo
)

// --- ViewModel ---
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository // Inyecta el repositorio real
) : ViewModel() {

    // Estado observable por la UI de Compose
    var uiState by mutableStateOf(CategoriesUiState())
        private set

    // Se ejecuta al crear el ViewModel
    init {
        loadCategories()
    }

    // Carga la lista de categorías desde el repositorio
    fun loadCategories() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null) // Muestra carga, limpia error
            Log.d("CategoriesVM", "Cargando categorías...")
            val result: Result<List<CategoryResponse>> = categoryRepository.getAllCategories() // Llama al repositorio

            result.onSuccess { categoryList ->
                // Éxito: Actualiza el estado con la lista
                Log.d("CategoriesVM", "Categorías cargadas: ${categoryList.size}")
                uiState = uiState.copy(isLoading = false, categories = categoryList)
            }.onFailure { e ->
                // Error: Actualiza el estado con el mensaje de error
                Log.e("CategoriesVM", "Error al cargar categorías", e)
                uiState = uiState.copy(isLoading = false, error = "Error al cargar categorías: ${e.message}")
            }
        }
    }

    // --- Funciones para manejar el diálogo de agregar ---

    // Abre el diálogo reseteando los campos
    fun onAddCategoryClicked() {
        Log.d("CategoriesVM", "Botón '+' presionado, mostrando diálogo.")
        uiState = uiState.copy(
            showAddDialog = true,
            newCategoryType = "",
            newCategoryDescription = "",
            addError = null,
            isAdding = false // Asegura que no esté en estado de carga
        )
    }

    // Cierra el diálogo
    fun onDismissAddDialog() {
        Log.d("CategoriesVM", "Diálogo cerrado.")
        uiState = uiState.copy(showAddDialog = false)
    }

    // Actualiza el estado cuando el usuario escribe en el campo "Tipo/Nombre"
    fun onNewCategoryTypeChange(type: String) {
        // Actualiza el valor y limpia el error del diálogo si existía
        uiState = uiState.copy(newCategoryType = type, addError = null)
    }

    // Actualiza el estado cuando el usuario escribe en el campo "Descripción"
    fun onNewCategoryDescriptionChange(description: String) {
        uiState = uiState.copy(newCategoryDescription = description)
    }

    // Confirma la creación de la nueva categoría
    fun onConfirmAddCategory() {
        // Validación: el tipo no puede estar vacío
        if (uiState.newCategoryType.isBlank()) {
            uiState = uiState.copy(addError = "El tipo no puede estar vacío")
            return
        }

        Log.d("CategoriesVM", "Confirmando agregar categoría: ${uiState.newCategoryType}")
        viewModelScope.launch {
            uiState = uiState.copy(isAdding = true, addError = null) // Muestra carga en diálogo

            // Crea el objeto Request con los datos del estado
            val request = CategoryRequest(
                tipo = uiState.newCategoryType.trim(), // Usa 'tipo' y quita espacios
                descripcion = uiState.newCategoryDescription.trim().takeIf { it.isNotBlank() } // Envía null si está vacío/solo espacios
            )

            // Llama al repositorio para crear la categoría
            val result: Result<CategoryResponse> = categoryRepository.createCategory(request)

            result.onSuccess { newCategory ->
                // Éxito: Cierra el diálogo y recarga la lista completa
                Log.d("CategoriesVM", "Categoría agregada: $newCategory")
                loadCategories() // Recargar toda la lista
                // Cierra el diálogo después de recargar (o antes si prefieres optimismo)
                uiState = uiState.copy(isAdding = false, showAddDialog = false)
            }.onFailure { e ->
                // Error: Muestra el error en el diálogo
                Log.e("CategoriesVM", "Error al agregar categoría", e)
                uiState = uiState.copy(isAdding = false, addError = "Error al agregar: ${e.message}")
            }
        }
    }

    // Función para refrescar la lista (llamada desde SwipeRefresh, por ejemplo)
    fun refreshCategories() {
        if (!uiState.isLoading) { // Evita refrescar si ya está cargando
            loadCategories()
        }
    }
}