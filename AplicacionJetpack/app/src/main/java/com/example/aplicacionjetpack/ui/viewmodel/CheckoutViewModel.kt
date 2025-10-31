package com.example.aplicacionjetpack.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
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
    private val direccionRepository: DireccionRepository
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    private val TAG = "CheckoutVM"

    init {
        loadDireccionesGuardadas()
    }

    fun loadDireccionesGuardadas() {
        val userId = AuthManager.userId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingDirecciones = true, error = null)
            val result = direccionRepository.getDireccionesByUser(userId)
            result.onSuccess { direcciones ->
                uiState = uiState.copy(isLoadingDirecciones = false, direccionesGuardadas = direcciones)
            }.onFailure { exception ->
                uiState = uiState.copy(isLoadingDirecciones = false, error = "No se pudieron cargar las direcciones.")
                Log.e(TAG, "Error al cargar las direcciones guardadas", exception)
            }
        }
    }

    fun deleteDireccion(idDireccion: Long) {
        viewModelScope.launch {
            val result = direccionRepository.deleteDireccion(idDireccion)
            if (result.isSuccess) {
                if (uiState.usarDireccionExistenteId == idDireccion) {
                    uiState = uiState.copy(
                        departamento = "",
                        municipio = "",
                        direccion = "",
                        aliasDireccion = "Casa",
                        latitud = null,
                        longitud = null,
                        usarDireccionExistenteId = null
                    )
                }
                loadDireccionesGuardadas()
            } else {
                uiState = uiState.copy(error = "No se pudo borrar la dirección.")
            }
        }
    }

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
                    isLoadingAddressFromMap = false,
                    aliasDireccion = "" // Dejamos el alias vacío para que el usuario lo llene
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoadingAddressFromMap = false, error = "No se pudo obtener la dirección.")
                Log.e(TAG, "Error al obtener dirección desde coordenadas", e)
            }
        }
    }

    // --- FUNCIÓN PARA EL ALIAS (SE MANTIENE) ---
    fun onAliasChange(nuevoAlias: String) {
        uiState = uiState.copy(aliasDireccion = nuevoAlias)
    }

    fun setUsarDireccionExistenteId(id: Long?) {
        uiState = uiState.copy(usarDireccionExistenteId = id)
    }

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
                AddressInfo(
                    calle = json.optString("road", "Calle no encontrada"),
                    ciudad = json.optString("city", json.optString("town", json.optString("village", "Municipio no encontrado"))),
                    depto = json.optString("state", "Departamento no encontrado")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Fallo en getAddressFromCoordinates", e)
                AddressInfo("Error", "No se pudo", "Obtener")
            }
        }
    }

    private data class AddressInfo(val calle: String, val ciudad: String, val depto: String)

    // --- 👇👇👇 ¡¡¡LA VALIDACIÓN SIMPLE Y FUNCIONAL HA VUELTO!!! 👇👇👇 ---
    val isAddressValid: Boolean
        get() = uiState.departamento.isNotBlank() && uiState.municipio.isNotBlank() && uiState.direccion.isNotBlank()
    // --- ---------------------------------------------------------------- ---

    fun onMetodoPagoChange(nuevoMetodo: TipoPago) { uiState = uiState.copy(metodoPagoSeleccionado = nuevoMetodo, isDropdownExpanded = false, error = null) }
    fun onDropdownDismiss() { uiState = uiState.copy(isDropdownExpanded = false) }
    fun onDropdownClicked() { uiState = uiState.copy(isDropdownExpanded = true) }
    fun onNumeroTarjetaChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(numeroTarjeta = digitsOnly.take(16)) }
    fun onFechaVencimientoChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(fechaVencimiento = digitsOnly.take(4)) }
    fun onCvvChange(value: String) { val digitsOnly = value.filter { it.isDigit() }; uiState = uiState.copy(cvv = digitsOnly.take(4)) }
    fun onTitularChange(value: String) { uiState = uiState.copy(titular = value) }
    fun onEmailPaypalChange(value: String) { uiState = uiState.copy(emailPaypal = value) }

    val isPaymentValid: Boolean by derivedStateOf {
        when (uiState.metodoPagoSeleccionado) {
            TipoPago.TARJETA_CREDITO -> {
                ValidationUtils.isValidCardNumber(uiState.numeroTarjeta) &&
                        ValidationUtils.isValidExpiryDate(uiState.fechaVencimiento) &&
                        ValidationUtils.isValidCvv(uiState.cvv) &&
                        ValidationUtils.isValidCardHolder(uiState.titular)
            }
            TipoPago.PAYPAL -> ValidationUtils.isValidEmail(uiState.emailPaypal)
            TipoPago.EFECTIVO -> true
        }
    }

    fun processFinalCheckout(idCarrito: Long) {
        // --- 👇👇👇 ¡¡¡LA VALIDACIÓN DEL ALIAS AHORA VIVE AQUÍ, DONDE DEBE!!! 👇👇👇 ---
        val isNewAddress = uiState.usarDireccionExistenteId == null
        if (!isAddressValid || (isNewAddress && uiState.aliasDireccion.isBlank())) {
            uiState = uiState.copy(error = "La dirección (y el alias si es nueva) no es válida.")
            return
        }
        // --- ----------------------------------------------------------------------- ---

        if (!isPaymentValid) {
            uiState = uiState.copy(error = "Los datos del método de pago son inválidos.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val userId = AuthManager.userId ?: run {
                handleError("Error fatal: Usuario no autenticado.")
                return@launch
            }
            val idDireccionFinal = getFinalDireccionId(userId).getOrElse {
                handleError("No se pudo procesar la dirección: ${it.message}")
                return@launch
            }
            val pedidoRequest = PedidoRequest(
                idCarrito = idCarrito,
                tipoPago = uiState.metodoPagoSeleccionado.name,
                cuponCodigo = null,
                idDireccion = idDireccionFinal
            )
            val checkoutResult = pedidoRepository.checkout(pedidoRequest)
            checkoutResult.onSuccess { pedidoCreado ->
                val pagoRequest = PagoRequest(
                    detallesPago = buildPaymentDetailsJson(),
                    usuario = UserRequest(idUser = userId)
                )
                val pagarResult = pedidoRepository.pagar(pedidoCreado.idPedido, pagoRequest)
                pagarResult.onSuccess {
                    uiState = uiState.copy(isLoading = false, checkoutSuccess = true)
                }.onFailure { e ->
                    handleError("El pago no pudo ser procesado: ${e.message}")
                }
            }.onFailure { e ->
                handleError("No se pudo iniciar el pedido: ${e.message}")
            }
        }
    }

    private fun buildPaymentDetailsJson(): String {
        val json = JSONObject()
        when (uiState.metodoPagoSeleccionado) {
            TipoPago.TARJETA_CREDITO -> {
                json.put("numeroTarjeta", uiState.numeroTarjeta)
                json.put("fechaVencimiento", uiState.fechaVencimiento)
                json.put("cvv", uiState.cvv)
                json.put("titular", uiState.titular)
            }
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

            val request = DireccionRequest(
                alias = uiState.aliasDireccion,
                calle = uiState.direccion,
                ciudad = uiState.municipio,
                departamento = uiState.departamento,
                latitud = uiState.latitud,
                longitud = uiState.longitud
            )
            val result = direccionRepository.createDireccion(userId, request)
            result.map { it.idDireccion }
        }
    }

    private fun handleError(message: String) {
        uiState = uiState.copy(isLoading = false, error = message)
    }
}
