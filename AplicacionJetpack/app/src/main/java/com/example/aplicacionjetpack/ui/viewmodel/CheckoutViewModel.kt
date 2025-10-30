package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionjetpack.data.AuthManager
import com.example.aplicacionjetpack.data.dto.*
import com.example.aplicacionjetpack.data.repository.DireccionRepository
import com.example.aplicacionjetpack.data.repository.PedidoRepository
import com.example.aplicacionjetpack.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

// --- Data Class del Estado (sin cambios) ---
data class CheckoutUiState(
    val direccionesGuardadas: List<DireccionResponse> = emptyList(),
    val isLoadingDirecciones: Boolean = true,
    val departamento: String = "",
    val municipio: String = "",
    val direccion: String = "",
    val aliasDireccion: String = "Casa",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val isLoadingAddressFromMap: Boolean = false,
    val usarDireccionExistenteId: Long? = null,
    val metodoPagoSeleccionado: TipoPago = TipoPago.TARJETA_CREDITO,
    val isDropdownExpanded: Boolean = false,
    val numeroTarjeta: String = "",
    val fechaVencimiento: String = "",
    val cvv: String = "",
    val titular: String = "",
    val emailPaypal: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val checkoutSuccess: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val direccionRepository: DireccionRepository // Hilt lo inyecta aquí
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    private val TAG = "CheckoutVM"

    // Se llama automáticamente al crear el ViewModel
    init {
        loadDireccionesGuardadas()
    }

    // --- 👇👇👇 ¡¡¡LA LÓGICA QUE FALTABA!!! 👇👇👇 ---

    /**
     * Carga las direcciones guardadas del usuario autenticado.
     */
    fun loadDireccionesGuardadas() {
        val userId = AuthManager.userId
        if (userId == null) {
            uiState = uiState.copy(isLoadingDirecciones = false, error = "Error: Usuario no autenticado.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingDirecciones = true, error = null)
            Log.d(TAG, "Cargando direcciones guardadas para el usuario ID: $userId")

            val result = direccionRepository.getDireccionesByUser(userId)

            result.onSuccess { direcciones ->
                Log.d(TAG, "Direcciones cargadas exitosamente: ${direcciones.size} encontradas.")
                uiState = uiState.copy(
                    isLoadingDirecciones = false,
                    direccionesGuardadas = direcciones
                )
            }.onFailure { exception ->
                Log.e(TAG, "Error al cargar las direcciones guardadas", exception)
                uiState = uiState.copy(
                    isLoadingDirecciones = false,
                    error = "No se pudieron cargar las direcciones."
                )
            }
        }
    }

    /**
     * Elimina una dirección y recarga la lista.
     */
    fun deleteDireccion(idDireccion: Long) {
        viewModelScope.launch {
            val result = direccionRepository.deleteDireccion(idDireccion)
            if (result.isSuccess) {
                Log.d(TAG, "Dirección $idDireccion borrada, recargando lista.")
                loadDireccionesGuardadas() // Vuelve a cargar la lista para actualizar la UI
            } else {
                uiState = uiState.copy(error = "No se pudo borrar la dirección.")
            }
        }
    }

    /**
     * Actualiza el estado cuando el usuario selecciona una dirección guardada.
     */
    fun onDireccionSeleccionada(direccion: DireccionResponse) {
        uiState = uiState.copy(
            departamento = direccion.departamento,
            municipio = direccion.ciudad,
            direccion = direccion.calle,
            aliasDireccion = direccion.alias,
            latitud = direccion.latitud,
            longitud = direccion.longitud,
            usarDireccionExistenteId = direccion.idDireccion
        )
    }

    /**
     * Obtiene la dirección a partir de coordenadas geográficas.
     */
    fun fetchAddressFromCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingAddressFromMap = true, usarDireccionExistenteId = null)
            try {
                val addressInfo = getAddressFromCoordinates(lat, lon)
                uiState = uiState.copy(
                    departamento = addressInfo.depto,
                    municipio = addressInfo.ciudad,
                    direccion = addressInfo.calle,
                    latitud = lat,
                    longitud = lon,
                    isLoadingAddressFromMap = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener dirección desde coordenadas", e)
                uiState = uiState.copy(isLoadingAddressFromMap = false, error = "No se pudo obtener la dirección.")
            }
        }
    }

    // --- -------------------------------------------- ---

    // --- LÓGICA DE PAGO (¡YA ESTÁ BIEN!) ---
    // (Aquí va todo el resto del código que ya tenías: onMetodoPagoChange, onNumeroTarjetaChange,
    // processFinalCheckout, getFinalDireccionId, buildPaymentDetailsJson, etc.
    // El código que te di en la respuesta anterior para esta parte es correcto y completo.)

    // ... (el resto del ViewModel que ya tienes) ...
    fun setUsarDireccionExistenteId(id: Long?) { uiState = uiState.copy(usarDireccionExistenteId = id) }
    private suspend fun getAddressFromCoordinates(lat: Double, lon: Double): AddressInfo {
        return withContext(Dispatchers.IO) {
            val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon"
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "NovaECatedraApp/1.0")

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                val json = JSONObject(response).getJSONObject("address")

                val calle = json.optString("road", "")
                val ciudad = json.optString("city", json.optString("town", json.optString("village", "")))
                val depto = json.optString("state", "")
                AddressInfo(calle, ciudad, depto)
            } catch (e: Exception) {
                Log.e(TAG, "Fallo en getAddressFromCoordinates", e)
                AddressInfo("Error", "No se pudo", "Obtener")
            }
        }
    }
    private data class AddressInfo(val calle: String, val ciudad: String, val depto: String)
    val isAddressValid: Boolean get() = uiState.departamento.isNotBlank() && uiState.municipio.isNotBlank() && uiState.direccion.isNotBlank()

    fun onMetodoPagoChange(nuevoMetodo: TipoPago) { uiState = uiState.copy(metodoPagoSeleccionado = nuevoMetodo, isDropdownExpanded = false, error = null) }
    fun onDropdownDismiss() { uiState = uiState.copy(isDropdownExpanded = false) }
    fun onDropdownClicked() { uiState = uiState.copy(isDropdownExpanded = true) }
    fun onNumeroTarjetaChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(numeroTarjeta = digitsOnly.take(16)) }
    fun onFechaVencimientoChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(fechaVencimiento = digitsOnly.take(4)) }
    fun onCvvChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(cvv = digitsOnly.take(4)) }
    fun onTitularChange(value: String) { uiState = uiState.copy(titular = value) }
    fun onEmailPaypalChange(value: String) { uiState = uiState.copy(emailPaypal = value) }
    val isPaymentValid: Boolean get() = when (uiState.metodoPagoSeleccionado) {
        TipoPago.TARJETA_CREDITO -> ValidationUtils.isValidCardNumber(uiState.numeroTarjeta) && ValidationUtils.isValidExpiryDate(uiState.fechaVencimiento) && ValidationUtils.isValidCvv(uiState.cvv) && ValidationUtils.isValidCardHolder(uiState.titular)
        TipoPago.PAYPAL -> ValidationUtils.isValidEmail(uiState.emailPaypal)
        TipoPago.EFECTIVO -> true
    }
    fun processFinalCheckout(idCarrito: Long) {
        if (!isAddressValid) { uiState = uiState.copy(error = "La dirección de entrega no es válida."); return }
        if (!isPaymentValid) { uiState = uiState.copy(error = "Los datos del método de pago son inválidos."); return }
        viewModelScope.launch {
            val userId = AuthManager.userId ?: run { handleError("Error fatal: Usuario no autenticado."); return@launch }
            uiState = uiState.copy(isLoading = true, error = null)
            val idDireccionResult = getFinalDireccionId(userId)
            if (idDireccionResult.isFailure) { handleError("No se pudo procesar la dirección."); return@launch }
            val idDireccionFinal = idDireccionResult.getOrThrow()
            val pedidoRequest = PedidoRequest(idCarrito = idCarrito, tipoPago = uiState.metodoPagoSeleccionado.name, cuponCodigo = null, idDireccion = idDireccionFinal)
            val detallesJson = buildPaymentDetailsJson()
            val pagoRequest = PagoRequest(detallesPago = detallesJson)
            val finalResult = pedidoRepository.createAndPayOrder(pedidoRequest, pagoRequest)
            finalResult.onSuccess { uiState = uiState.copy(isLoading = false, checkoutSuccess = true) }.onFailure { Log.e(TAG, "Error en el checkout final.", it); handleError("El pago falló: ${it.message}") }
        }
    }
    private fun buildPaymentDetailsJson(): String {
        val json = JSONObject()
        when (uiState.metodoPagoSeleccionado) {
            TipoPago.TARJETA_CREDITO -> { json.put("numeroTarjeta", uiState.numeroTarjeta); json.put("fechaVencimiento", uiState.fechaVencimiento); json.put("cvv", uiState.cvv); json.put("titular", uiState.titular) }
            TipoPago.PAYPAL -> { json.put("email", uiState.emailPaypal) }
            TipoPago.EFECTIVO -> { json.put("mensaje", "Pago se realizará contra entrega.") }
        }
        return json.toString()
    }
    private suspend fun getFinalDireccionId(userId: Long): Result<Long> {
        return withContext(Dispatchers.IO) {
            if (uiState.usarDireccionExistenteId != null) {
                return@withContext Result.success(uiState.usarDireccionExistenteId!!)
            }
            val request = DireccionRequest(alias = uiState.aliasDireccion, calle = uiState.direccion, ciudad = uiState.municipio, departamento = uiState.departamento, latitud = uiState.latitud, longitud = uiState.longitud)
            val result = direccionRepository.createDireccion(userId, request)
            result.map { it.idDireccion }
        }
    }
    private fun handleError(message: String) { uiState = uiState.copy(isLoading = false, error = message) }
}
