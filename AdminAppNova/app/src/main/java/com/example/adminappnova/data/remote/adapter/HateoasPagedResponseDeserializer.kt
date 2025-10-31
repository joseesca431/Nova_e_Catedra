package com.example.adminappnova.data.remote.adapter

import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PedidoResponse
import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Un Deserializador GENÉRICO para cualquier PagedResponse<T> que siga la estructura HATEOAS.
 * Es capaz de procesar la lista de items y aplicar lógica extra a cada uno.
 */
class HateoasPagedResponseDeserializer<T> : JsonDeserializer<PagedResponse<T>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PagedResponse<T> {
        val jsonObject = json.asJsonObject
        val embeddedObject = jsonObject.getAsJsonObject("_embedded")

        // Obtiene el tipo genérico real (ej: PedidoResponse, ProductResponse)
        val itemType = (typeOfT as ParameterizedType).actualTypeArguments[0]

        if (embeddedObject != null) {
            // Encuentra la primera (y única) lista dentro de _embedded
            val entry = embeddedObject.entrySet().firstOrNull()
            if (entry != null && entry.value.isJsonArray) {
                val itemsArray = entry.value.asJsonArray

                itemsArray.forEach { itemElement ->
                    val itemObject = itemElement.asJsonObject
                    // --- 👇 ¡AQUÍ ESTÁ LA MAGIA! 👇 ---
                    // Si el item que estamos procesando es un PedidoResponse, le extraemos el idUser.
                    if (itemType == PedidoResponse::class.java) {
                        try {
                            val linksObject = itemObject.getAsJsonObject("_links")
                            val userLink = linksObject?.getAsJsonObject("pedidos-usuario")
                            val href = userLink?.get("href")?.asString
                            val userId = href?.substringAfterLast('/')?.toLongOrNull()
                            // Añadimos el idUser al objeto JSON antes de que Gson lo convierta
                            if (userId != null) {
                                itemObject.addProperty("idUser", userId)
                            }
                        } catch (e: Exception) {
                            // Ignora si la estructura de links no es la esperada
                        }
                    }
                    // Podrías añadir más bloques 'if' aquí para otros tipos si lo necesitas
                }
            }
        }

        // Después de modificar el JSON en memoria, le pedimos a una nueva instancia de Gson
        // que haga el resto del trabajo. Esto evita bucles infinitos.
        return Gson().fromJson(jsonObject, typeOfT)
    }
}
