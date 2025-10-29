package com.example.adminappnova.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminappnova.data.dto.ProductRequest
import com.example.adminappnova.data.dto.ProductResponse
import com.example.adminappnova.data.repository.CategoryRepository // Para buscar ID de categoría
import com.example.adminappnova.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Result // Asegúrate de importar kotlin.Result

// --- Data Class para el Estado de la UI ---
data class ProductDetailUiState(
    val isLoading: Boolean = false, // Para cargar datos iniciales
    val isSaving: Boolean = false, // Para indicar progreso al guardar
    val isDeleting: Boolean = false, // Para indicar progreso al eliminar
    val product: ProductResponse? = null, // Datos originales si se edita
    // Campos del formulario
    val nombre: String = "",
    val descripcion: String = "",
    val cantidad: String = "", // Usamos String para el TextField
    val precio: String = "",   // Usamos String para el TextField
    val costo: String = "",    // Usamos String para el TextField
    val cantidadPuntos: String = "", // Usamos String para el TextField
    // val imagenUrl: String? = null, // Podrías añadir lógica para imagen
    val saveSuccess: Boolean = false, // Flag para indicar éxito y navegar atrás
    val deleteSuccess: Boolean = false, // Flag para indicar éxito y navegar atrás
    val error: String? = null // Mensajes de error para el usuario
)

// --- ViewModel ---
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository, // Inyecta repo de categorías
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Estado observable
    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    // Argumentos de navegación
    private val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Desconocida"
    private val productId: String? = savedStateHandle.get<String>("productId")
    private val isEditing = productId != null

    // ID de la categoría a la que pertenece/pertenecerá el producto
    private var categoryId: Long? = null

    init {
        // Carga el ID de la categoría y luego los detalles del producto si es necesario
        findCategoryIdAndLoadData()
    }

    // Busca el ID de la categoría basado en el nombre y luego carga los datos del producto
    private fun findCategoryIdAndLoadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true) // Indicar carga general inicial

            // Busca la categoría por nombre (asume que tienes esta función en CategoryRepository)
            // Necesitarás una función en CategoryRepository como findCategoryByName
            // val categoryResult = categoryRepository.findCategoryByName(categoryName)
            // Simulación temporal
            kotlinx.coroutines.delay(100)
            val simulatedCategoryId = 1L // ID de ejemplo para la categoría

            // categoryResult.onSuccess { category ->
            // categoryId = category?.idTipoProducto // Guarda el ID encontrado (puede ser null si no se encuentra)
            categoryId = simulatedCategoryId // Usando simulación

            if (categoryId == null) {
                uiState = uiState.copy(isLoading = false, error = "Categoría '$categoryName' no encontrada.")
                return@launch
            }

            // Si estamos editando, carga los detalles del producto
            if (isEditing && productId != null) {
                loadProductDetails(productId.toLong())
            } else {
                // Si estamos creando, simplemente termina la carga inicial
                uiState = uiState.copy(isLoading = false)
            }
            // }.onFailure { e ->
            //     uiState = uiState.copy(isLoading = false, error = "Error al buscar categoría: ${e.message}")
            // }
        }
    }


    // Carga los detalles de un producto existente por ID
    private fun loadProductDetails(id: Long) {
        viewModelScope.launch {
            // isLoading ya está true desde findCategoryIdAndLoadData
            val result: Result<ProductResponse> = productRepository.getProductById(id)

            result.onSuccess { loadedProduct ->
                // Rellena el estado de la UI con los datos cargados
                uiState = uiState.copy(
                    isLoading = false,
                    product = loadedProduct,
                    nombre = loadedProduct.nombre,
                    descripcion = loadedProduct.descripcion ?: "",
                    cantidad = loadedProduct.cantidad?.toString() ?: "",
                    precio = loadedProduct.precio.toString(),
                    costo = loadedProduct.costo?.toString() ?: "",
                    cantidadPuntos = loadedProduct.cantidadPuntos?.toString() ?: "",
                    // imagenUrl = loadedProduct.imagen // Si manejas imágenes
                )
                // Asegura que el categoryId coincide (aunque ya lo cargamos antes)
                categoryId = loadedProduct.idTipoProducto

            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "Error al cargar detalles del producto: ${e.message}")
            }
        }
    }

    // --- Funciones de Evento para cambios en TextFields ---
    fun onNombreChange(value: String) { uiState = uiState.copy(nombre = value, error = null) }
    fun onDescripcionChange(value: String) { uiState = uiState.copy(descripcion = value, error = null) }
    fun onCantidadChange(value: String) {
        if (value.all { it.isDigit() } || value.isEmpty()) {
            uiState = uiState.copy(cantidad = value, error = null)
        }
    }
    fun onPrecioChange(value: String) {
        if (value.matches(Regex("^\\d*\\.?\\d{0,2}\$")) || value.isEmpty()) { // Permite hasta 2 decimales
            uiState = uiState.copy(precio = value, error = null)
        }
    }
    fun onCostoChange(value: String) {
        if (value.matches(Regex("^\\d*\\.?\\d{0,2}\$")) || value.isEmpty()) {
            uiState = uiState.copy(costo = value, error = null)
        }
    }
    fun onCantidadPuntosChange(value: String) {
        if (value.all { it.isDigit() } || value.isEmpty()) {
            uiState = uiState.copy(cantidadPuntos = value, error = null)
        }
    }
    // fun onAddImageClicked() { /* Lógica para seleccionar/tomar foto */ }

    // --- Acción de Guardar (Crear o Actualizar) ---
    fun onSaveClicked() {
        val currentCategoryId = categoryId
        if (currentCategoryId == null) {
            uiState = uiState.copy(error = "No se pudo determinar la categoría.")
            return
        }

        // Validaciones numéricas y de campos obligatorios
        val cantidadInt = uiState.cantidad.toIntOrNull()
        val precioBd = uiState.precio.toBigDecimalOrNull()
        val costoBd = uiState.costo.toBigDecimalOrNull()
        val puntosInt = uiState.cantidadPuntos.toIntOrNull()

        if (uiState.nombre.isBlank() || cantidadInt == null || precioBd == null || costoBd == null || puntosInt == null) {
            uiState = uiState.copy(error = "Nombre, Cantidad, Precio, Costo y Puntos son obligatorios y deben ser números válidos.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null) // Indicar progreso

            // Crear el objeto Request para la API
            val request = ProductRequest(
                nombre = uiState.nombre,
                descripcion = uiState.descripcion,
                precio = precioBd,
                costo = costoBd,
                cantidad = cantidadInt,
                cantidadPuntos = puntosInt,
                idTipoProducto = currentCategoryId,
                imagen = null // TODO: Añadir URL de imagen si se maneja
            )

            // Decidir si llamar a crear o actualizar
            val result: Result<ProductResponse> = if (isEditing && productId != null) {
                productRepository.updateProduct(productId.toLong(), request)
            } else {
                productRepository.createProduct(request)
            }

            // Manejar el resultado
            result.onSuccess { savedProduct ->
                uiState = uiState.copy(isSaving = false, saveSuccess = true) // Éxito -> Navegar atrás
            }.onFailure { e ->
                uiState = uiState.copy(isSaving = false, error = "Error al guardar: ${e.message}")
            }
        }
    }

    // --- Acción de Eliminar ---
    fun onDeleteClicked() {
        if (!isEditing || productId == null) return // Solo se puede eliminar si se está editando

        viewModelScope.launch {
            uiState = uiState.copy(isDeleting = true, error = null) // Indicar progreso
            val result: Result<Unit> = productRepository.deleteProduct(productId.toLong()) // Llamar al repo

            result.onSuccess {
                uiState = uiState.copy(isDeleting = false, deleteSuccess = true) // Éxito -> Navegar atrás
            }.onFailure { e ->
                uiState = uiState.copy(isDeleting = false, error = "Error al eliminar: ${e.message}")
            }
        }
    }
}