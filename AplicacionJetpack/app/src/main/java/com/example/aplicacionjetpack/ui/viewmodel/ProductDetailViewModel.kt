package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.data.dto.ResenaResponse
import com.example.aplicacionjetpack.data.repository.ProductRepository
import com.example.aplicacionjetpack.data.repository.ResenaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val product: ProductResponse? = null,
    val reviews: List<ResenaResponse> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val error: String? = null
) {
    val canLoadMore: Boolean
        get() = currentPage < (totalPages - 1)
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val resenaRepository: ResenaRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    private val TAG = "ProductDetailVM"

    // Carga producto + primera página de reseñas
    fun loadProductAndReviews(productId: Long, reviewsPageSize: Int = 10) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d(TAG, "Cargando producto y reseñas paginadas para id=$productId")

            val productResult: Result<ProductResponse> = productRepository.getProductById(productId)
            productResult.onSuccess { product ->
                // Cargar primera página de reseñas (page 0)
                val reviewsResult = resenaRepository.getReviewsByProductPaginated(productId, 0, reviewsPageSize)
                reviewsResult.onSuccess { paged ->
                    uiState = uiState.copy(
                        isLoading = false,
                        product = product,
                        reviews = paged.content,
                        currentPage = paged.number,
                        totalPages = paged.totalPages
                    )
                    Log.d(TAG, "Producto cargado y reseñas página ${paged.number} (${paged.content.size})")
                }.onFailure { e ->
                    Log.e(TAG, "Producto cargado, fallo al obtener reseñas paginadas", e)
                    uiState = uiState.copy(
                        isLoading = false,
                        product = product,
                        reviews = emptyList(),
                        error = "No se pudieron cargar reseñas."
                    )
                }
            }.onFailure { e ->
                Log.e(TAG, "Fallo al obtener producto", e)
                uiState = uiState.copy(isLoading = false, error = "No se pudo cargar el producto.")
            }
        }
    }

    // Carga la siguiente página de reseñas y las anexa
    fun loadMoreReviews(productId: Long, pageSize: Int = 10) {
        // Evita llamadas simultáneas o pedir si no hay más
        if (uiState.isLoadingMore || !uiState.canLoadMore) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true, error = null)
            val nextPage = uiState.currentPage + 1
            Log.d(TAG, "Cargando página $nextPage de reseñas para producto $productId")

            val result = resenaRepository.getReviewsByProductPaginated(productId, nextPage, pageSize)
            result.onSuccess { paged ->
                val combined = uiState.reviews + paged.content
                uiState = uiState.copy(
                    isLoadingMore = false,
                    reviews = combined,
                    currentPage = paged.number,
                    totalPages = paged.totalPages
                )
                Log.d(TAG, "Página ${paged.number} cargada. Total reseñas ahora: ${uiState.reviews.size}")
            }.onFailure { e ->
                Log.e(TAG, "Fallo cargando página $nextPage de reseñas", e)
                uiState = uiState.copy(isLoadingMore = false, error = "No se pudieron cargar más reseñas.")
            }
        }
    }

    fun refresh(productId: Long, reviewsPageSize: Int = 10) {
        if (!uiState.isLoading) {
            loadProductAndReviews(productId, reviewsPageSize)
        }
    }

    // Mapea nombre del enum según el valor double seleccionado (ej. 4.5 -> "FOUR_HALF")
    private fun ratingValueToEnumName(value: Double): String {
        return when (value) {
            0.0 -> "ZERO"
            0.5 -> "HALF"
            1.0 -> "ONE"
            1.5 -> "ONE_HALF"
            2.0 -> "TWO"
            2.5 -> "TWO_HALF"
            3.0 -> "THREE"
            3.5 -> "THREE_HALF"
            4.0 -> "FOUR"
            4.5 -> "FOUR_HALF"
            5.0 -> "FIVE"
            else -> "FIVE"
        }
    }

    /** Publicar reseña y actualizar lista (inserta al inicio y/o recarga la página 0) */
    fun addReview(productId: Long, userId: Long, ratingValue: Double, comentario: String?) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val ratingEnumName = ratingValueToEnumName(ratingValue)
            val request = com.example.aplicacionjetpack.data.dto.ResenaRequest(
                idUser = userId,
                idProducto = productId,
                comentario = comentario,
                rating = ratingEnumName
            )

            val result = resenaRepository.postReview(request)
            result.onSuccess { resena ->
                // opcional: insertar la reseña al inicio y mantener paginación
                val newList = listOf(resena) + uiState.reviews
                uiState = uiState.copy(
                    isLoading = false,
                    reviews = newList
                )
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "No se pudo publicar la reseña.")
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}
