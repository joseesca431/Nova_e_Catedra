package com.example.adminappnova.data.remote.adapter

import com.example.adminappnova.data.dto.PedidoResponse
import com.google.gson.*
import java.lang.reflect.Type

class PedidoResponseDeserializer : JsonDeserializer<PedidoResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PedidoResponse {
        val jsonObject = json.asJsonObject

        // Usa Gson para deserializar la mayor parte del objeto automáticamente
        val pedido = Gson().fromJson(jsonObject, PedidoResponse::class.java)

        // Ahora, extrae manualmente el idUser de _links
        val linksObject = jsonObject.getAsJsonObject("_links")
        val pedidosUsuarioObject = linksObject?.getAsJsonObject("pedidos-usuario")
        val href = pedidosUsuarioObject?.get("href")?.asString

        // Extrae el ID del final de la URL
        pedido.idUser = href?.substringAfterLast('/')?.toLongOrNull()

        return pedido
    }
}
