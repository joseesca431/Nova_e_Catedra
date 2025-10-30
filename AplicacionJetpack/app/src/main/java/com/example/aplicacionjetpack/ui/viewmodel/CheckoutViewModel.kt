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
// --- 👇👇👇 ¡¡¡IMPORTS CRUCIALES PARA LA NUEVA LLAMADA DE RED!!! 👇👇👇 ---
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
// --- ----------------------------------------------------------------- ---
import javax.inject.Inject

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
    val numeroTarjeta: String = "",
    val fechaVencimiento: String = "",
    val cvv: String = "",
    val titular: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val checkoutSuccess: Boolean = false,
    val idPedidoPendiente: Long? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val direccionRepository: DireccionRepository
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    init {
        loadDireccionesGuardadas()
    }

    fun loadDireccionesGuardadas() {
        viewModelScope.launch {
            val userId = AuthManager.userId ?: return@launch
            uiState = uiState.copy(isLoadingDirecciones = true)
            val result = direccionRepository.getDireccionesByUser(userId)
            result.onSuccess { direcciones ->
                uiState = uiState.copy(isLoadingDirecciones = false, direccionesGuardadas = direcciones)
            }.onFailure {
                uiState = uiState.copy(isLoadingDirecciones = false, error = "No se pudieron cargar las direcciones.")
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
            error = null
        )
    }

    fun fetchAddressFromCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingAddressFromMap = true)
            // Llama a la nueva función de red
            val addressInfo = getAddressFromCoordinates(lat, lon)
            uiState = uiState.copy(
                direccion = addressInfo.calle,
                municipio = addressInfo.ciudad,
                departamento = addressInfo.depto,
                latitud = lat,
                longitud = lon,
                isLoadingAddressFromMap = false,
                error = null
            )
        }
    }

    // --- 👇👇👇 ¡¡¡LA FUNCIÓN DE RED FINALMENTE CORREGIDA!!! 👇👇👇 ---
    private suspend fun getAddressFromCoordinates(lat: Double, lon: Double): AddressInfo {
        return withContext(Dispatchers.IO) { // Ejecuta en un hilo de fondo
            val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon&addressdetails=1"
            try {
                val url = URL(urlString)
                // Usamos HttpURLConnection para poder añadir cabeceras
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    // ¡¡LA LÍNEA MÁS IMPORTANTE!! Nominatim requiere un User-Agent.
                    setRequestProperty("User-Agent", "NovaECatedraApp/1.0 (tu.email@ejemplo.com)")
                    connectTimeout = 10_000 // 10 segundos
                    readTimeout = 10_000 // 10 segundos
                }

                // Leemos la respuesta
                val stream = conn.inputStream
                val reader = BufferedReader(InputStreamReader(stream))
                val text = reader.readText()
                reader.close()
                conn.disconnect()

                Log.d("CheckoutVM", "Geocodificación inversa exitosa: $text")

                val jsonObj = JSONObject(text)
                val address = jsonObj.optJSONObject("address")
                // Se busca en varios campos posibles para asegurar que encontramos algo
                val calle = address?.optString("road", null) ?: address?.optString("suburb", null) ?: jsonObj.optString("display_name", "Ubicación seleccionada")
                val ciudad = address?.optString("city", null) ?: address?.optString("town", null) ?: address?.optString("village", "") ?: ""
                val depto = address?.optString("state", "") ?: ""
                AddressInfo(calle, ciudad, depto)
            } catch (e: Exception) {
                // Ahora sí veremos este error en el logcat si algo falla
                Log.e("CheckoutVM", "Error en geocodificación inversa", e)
                AddressInfo("Error al obtener dirección", "", "")
            }
        }
    }

    private data class AddressInfo(val calle: String, val ciudad: String, val depto: String)
    // --- ----------------------------------------------------------- ---


    val isAddressValid: Boolean
        get() = uiState.departamento.isNotBlank() && uiState.municipio.isNotBlank() && uiState.direccion.isNotBlank()

    val isPaymentValid: Boolean
        get() = ValidationUtils.isValidCardNumber(uiState.numeroTarjeta) &&
                ValidationUtils.isValidExpiryDate(uiState.fechaVencimiento) &&
                ValidationUtils.isValidCvv(uiState.cvv) &&
                ValidationUtils.isValidCardHolder(uiState.titular)

    fun onDepartamentoChange(value: String) { uiState = uiState.copy(departamento = value, error = null) }
    fun onMunicipioChange(value: String) { uiState = uiState.copy(municipio = value, error = null) }
    fun onDireccionChange(value: String) { uiState = uiState.copy(direccion = value, error = null) }
    fun onNumeroTarjetaChange(value: String) { uiState = uiState.copy(numeroTarjeta = value.filter { it.isDigit() }.take(16), error = null) }
    fun onFechaVencimientoChange(value: String) { uiState = uiState.copy(fechaVencimiento = value, error = null) }
    fun onCvvChange(value: String) { uiState = uiState.copy(cvv = value.filter { it.isDigit() }.take(4), error = null) }
    fun onTitularChange(value: String) { uiState = uiState.copy(titular = value, error = null) }

    fun createPendingOrder(idCarrito: Long, usarDireccionExistenteId: Long? = null) {
        if (!isAddressValid) {
            uiState = uiState.copy(error = "Completa todos los campos de dirección.")
            return
        }
        viewModelScope.launch {
            val userId = AuthManager.userId
            if (userId == null) {
                handleError("Error fatal: Usuario no autenticado.")
                return@launch
            }

            uiState = uiState.copy(isLoading = true, error = null)
            val idDireccionFinal: Long
            if (usarDireccionExistenteId != null) {
                idDireccionFinal = usarDireccionExistenteId
            } else {
                val dirRequest = DireccionRequest(
                    alias = uiState.aliasDireccion,
                    calle = uiState.direccion,
                    ciudad = uiState.municipio,
                    departamento = uiState.departamento,
                    latitud = uiState.latitud,
                    longitud = uiState.longitud
                )
                val dirResult = direccionRepository.createDireccion(userId, dirRequest)

                dirResult.onFailure {
                    handleError("Error al guardar la dirección.")
                    return@launch
                }
                val idNuevaDireccion = dirResult.getOrNull()?.idDireccion
                if (idNuevaDireccion == null) {
                    handleError("No se pudo obtener el ID de la nueva dirección.")
                    return@launch
                }
                idDireccionFinal = idNuevaDireccion
            }

            val pedidoRequest = PedidoRequest(
                idCarrito = idCarrito,
                tipoPago = "TARJETA_CREDITO",
                cuponCodigo = null,
                idDireccion = idDireccionFinal
            )
            val pedidoResult = pedidoRepository.checkout(pedidoRequest)
            pedidoResult.onSuccess { pedidoResponse ->
                uiState = uiState.copy(isLoading = false, idPedidoPendiente = pedidoResponse.idPedido)
            }.onFailure {
                handleError("Error al crear el pedido.")
            }
        }
    }

    fun processPayment() {
        val idPedido = uiState.idPedidoPendiente
        if (idPedido == null) {
            uiState = uiState.copy(error = "No hay un pedido pendiente para pagar.")
            return
        }

        if (!isPaymentValid) {
            uiState = uiState.copy(error = "Los datos de la tarjeta son inválidos.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val detallesJson = JSONObject().apply {
                put("numeroTarjeta", uiState.numeroTarjeta)
                put("fechaVencimiento", uiState.fechaVencimiento)
                put("cvv", uiState.cvv)
                put("titular", uiState.titular)
            }.toString()
            val pagoRequest = PagoRequest(detallesPago = detallesJson)
            val pagoResult = pedidoRepository.pagar(idPedido, pagoRequest)

            pagoResult.onSuccess {
                uiState = uiState.copy(isLoading = false, checkoutSuccess = true)
            }.onFailure {
                handleError("El pago falló. Por favor, verifica tus datos.")
            }
        }
    }

    private fun handleError(message: String) {
        Log.e("CheckoutVM", message)
        uiState = uiState.copy(isLoading = false, error = message)
    }
}
