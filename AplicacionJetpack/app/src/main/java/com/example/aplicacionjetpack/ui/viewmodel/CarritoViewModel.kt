package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.CarritoItemRequest
import com.example.aplicacionjetpack.data.dto.CarritoItemResponse
import com.example.aplicacionjetpack.data.dto.CarritoResponse
import com.example.aplicacionjetpack.data.repository.CarritoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.onFailure
import kotlin.onSuccess

data class CarritoUiState(
    val items: List<CarritoItemResponse> = emptyList(),
    val carrito: CarritoResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val total: BigDecimal
        // Este 'producto' se resolverá ahora
        get() = items.sumOf { (it.producto?.precio ?: BigDecimal.ZERO) * BigDecimal(it.cantidad ?: 1) }
}

@HiltViewModel
class CarritoViewModel @Inject constructor(
    private val carritoRepository: CarritoRepository
) : ViewModel() {

    var uiState by mutableStateOf(CarritoUiState())
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadCarrito()
    }

    fun loadCarrito() {
        val userId = AuthManager.userId
        if (userId == null) {
            uiState = uiState.copy(isLoading = false, error = "Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val carritoResult = carritoRepository.getOrCreateCarrito(userId)

            carritoResult.onSuccess { carrito ->
                val itemsResult = carritoRepository.getItems(carrito.idCarrito)
                itemsResult.onSuccess { items ->
                    uiState = uiState.copy(
                        isLoading = false,
                        carrito = carrito,
                        items = items
                    )
                }.onFailure { e ->
                    uiState = uiState.copy(isLoading = false, error = "No se pudieron cargar los items: ${e.message}")
                }
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = "No se pudo obtener el carrito: ${e.message}")
            }
        }
    }

    fun addItem(productId: Long, quantity: Int) {
        val idCarrito = uiState.carrito?.idCarrito
        if (idCarrito == null) {
            Log.e("CarritoVM", "No se puede añadir item, idCarrito es nulo.")
            return
        }

        viewModelScope.launch {
            // Este 'producto' se resolverá ahora
            val existingItem = uiState.items.find { it.producto?.idProducto == productId }
            if (existingItem != null) {
                val newQuantity = existingItem.cantidad!! + quantity
                updateItem(existingItem.idCarritoItem, newQuantity, productId)
            } else {
                // Este 'idCarrito' se resolverá ahora
                val request = CarritoItemRequest(
                    idCarrito = idCarrito,
                    idProducto = productId,
                    cantidad = quantity
                )
                val result = carritoRepository.addItem(request)
                result.onSuccess {
                    _uiEvent.emit(UiEvent.ShowSnackbar("¡Producto añadido al carrito!"))
                    loadCarrito() // Recarga el carrito
                }.onFailure {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Error al añadir producto"))
                }
            }
        }
    }

    fun removeItem(itemId: Long) {
        viewModelScope.launch {
            val result = carritoRepository.removeItem(itemId)
            if (result.isSuccess) {
                loadCarrito() // Recarga
            }
        }
    }

    fun updateItem(itemId: Long, newQuantity: Int, productId: Long) {
        val idCarrito = uiState.carrito?.idCarrito ?: return

        viewModelScope.launch {
            // Este 'idCarrito' se resolverá ahora
            val request = CarritoItemRequest(
                idCarrito = idCarrito,
                idProducto = productId,
                cantidad = newQuantity
            )
            val result = carritoRepository.updateItem(itemId, request)
            if (result.isSuccess) {
                loadCarrito() // Recarga
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}