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

// --- DATA CLASS SIMPLIFICADO ---
// ¡YA NO HAY idPedidoPendiente!
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
    val isLoading: Boolean = false, // Loading genérico para el pago final
    val error: String? = null,
    val checkoutSuccess: Boolean = false,
    val usarDireccionExistenteId: Long? = null // Se mantiene para saber qué dirección usar
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

    fun setUsarDireccionExistenteId(id: Long?) {
        uiState = uiState.copy(usarDireccionExistenteId = id)
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

    // --- 👇👇👇 ¡¡¡NUEVA FUNCIÓN PARA BORRAR DIRECCIONES!!! 👇👇👇 ---
    fun deleteDireccion(idDireccion: Long) {
        viewModelScope.launch {
            val result = direccionRepository.deleteDireccion(idDireccion)
            if (result.isSuccess) {
                // Si se borra, recargamos la lista desde cero
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
            error = null
        )
    }

    fun fetchAddressFromCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingAddressFromMap = true)
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

    private suspend fun getAddressFromCoordinates(lat: Double, lon: Double): AddressInfo {
        return withContext(Dispatchers.IO) {
            val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon&addressdetails=1"
            try {
                val url = URL(urlString)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "NovaECatedraApp/1.0 (tu.email@ejemplo.com)")
                    connectTimeout = 10_000
                    readTimeout = 10_000
                }
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val text = reader.readText()
                reader.close()
                conn.disconnect()

                val jsonObj = JSONObject(text)
                val address = jsonObj.optJSONObject("address")
                val calle = address?.optString("road", null) ?: address?.optString("suburb", null) ?: jsonObj.optString("display_name", "Ubicación seleccionada")
                val ciudad = address?.optString("city", null) ?: address?.optString("town", null) ?: address?.optString("village", "") ?: ""
                val depto = address?.optString("state", "") ?: ""
                AddressInfo(calle, ciudad, depto)
            } catch (e: Exception) {
                Log.e("CheckoutVM", "Error en geocodificación inversa", e)
                AddressInfo("Error al obtener dirección", "", "")
            }
        }
    }
    private data class AddressInfo(val calle: String, val ciudad: String, val depto: String)


    val isAddressValid: Boolean get() = uiState.departamento.isNotBlank() && uiState.municipio.isNotBlank() && uiState.direccion.isNotBlank()
    val isPaymentValid: Boolean get() = ValidationUtils.isValidCardNumber(uiState.numeroTarjeta) && ValidationUtils.isValidExpiryDate(uiState.fechaVencimiento) && ValidationUtils.isValidCvv(uiState.cvv) && ValidationUtils.isValidCardHolder(uiState.titular)

    fun onDepartamentoChange(value: String) { uiState = uiState.copy(departamento = value, error = null) }
    fun onMunicipioChange(value: String) { uiState = uiState.copy(municipio = value, error = null) }
    fun onDireccionChange(value: String) { uiState = uiState.copy(direccion = value, error = null) }
    fun onNumeroTarjetaChange(value: String) { uiState = uiState.copy(numeroTarjeta = value.filter { it.isDigit() }.take(16), error = null) }
    fun onFechaVencimientoChange(value: String) { uiState = uiState.copy(fechaVencimiento = value, error = null) }
    fun onCvvChange(value: String) { uiState = uiState.copy(cvv = value.filter { it.isDigit() }.take(4), error = null) }
    fun onTitularChange(value: String) { uiState = uiState.copy(titular = value, error = null) }


    // --- 👇👇👇 ¡¡¡LA LÓGICA DE PAGO HA SIDO COMPLETAMENTE REEMPLAZADA!!! 👇👇👇 ---
    // Esta función ahora lo hace TODO. No más 'createPendingOrder'.
    fun processFinalCheckout(idCarrito: Long) {
        if (!isAddressValid) {
            uiState = uiState.copy(error = "La dirección no es válida.")
            return
        }
        if (!isPaymentValid) {
            uiState = uiState.copy(error = "Los datos de la tarjeta son inválidos.")
            return
        }

        viewModelScope.launch {
            val userId = AuthManager.userId ?: run {
                handleError("Error fatal: Usuario no autenticado.")
                return@launch
            }

            uiState = uiState.copy(isLoading = true, error = null)

            // PASO A: Obtener el ID de la dirección (creándola si es necesario)
            val idDireccionResult = getFinalDireccionId(userId)
            if (idDireccionResult.isFailure) {
                handleError("No se pudo procesar la dirección.")
                return@launch
            }
            val idDireccionFinal = idDireccionResult.getOrThrow()

            // PASO B: Crear la petición de Pedido
            val pedidoRequest = PedidoRequest(
                idCarrito = idCarrito,
                tipoPago = "TARJETA_CREDITO",
                cuponCodigo = null,
                idDireccion = idDireccionFinal
            )

            // PASO C: Crear la petición de Pago (con los detalles de la tarjeta)
            val detallesJson = JSONObject().apply {
                put("numeroTarjeta", uiState.numeroTarjeta)
                put("fechaVencimiento", uiState.fechaVencimiento)
                put("cvv", uiState.cvv)
                put("titular", uiState.titular)
            }.toString()
            val pagoRequest = PagoRequest(detallesPago = detallesJson)

            // PASO D: Llamar al NUEVO método del repositorio que lo hace todo
            // Esta llamada al backend debería ser atómica (crear pedido y pagarlo)
            val finalResult = pedidoRepository.createAndPayOrder(pedidoRequest, pagoRequest)

            finalResult.onSuccess {
                uiState = uiState.copy(isLoading = false, checkoutSuccess = true)
            }.onFailure {
                Log.e("CheckoutVM", "Error en el checkout final.", it)
                handleError("El pago falló: ${it.message}")
            }
        }
    }

    // Función auxiliar para manejar la lógica de la dirección
    private suspend fun getFinalDireccionId(userId: Long): Result<Long> {
        return if (uiState.usarDireccionExistenteId != null) {
            Result.success(uiState.usarDireccionExistenteId!!)
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
            dirResult.map { it.idDireccion } // Transforma Result<DireccionResponse> en Result<Long>
        }
    }

    private fun handleError(message: String) {
        Log.e("CheckoutVM", message)
        uiState = uiState.copy(isLoading = false, error = message)
    }
}
