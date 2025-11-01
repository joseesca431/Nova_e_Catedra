package com.example.aplicacionjetpack.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO genérico y universal para mapear el PagedModel de Spring HATEOAS.
 * Esta versión es la definitiva y está diseñada para entender CUALQUIER
 * lista paginada que tu API envíe, incluyendo la del historial.
 */
data class PagedResponse<T>(
    @SerializedName("_embedded")
    val embedded: Embedded<T>?,

    @SerializedName("_links")
    val links: Links?,

    val page: PageInfo?
) {
    // --- 👇👇👇 ¡EL "CONTENT" OMNISCIENTE! 👇👇👇 ---
    // Esta propiedad inteligente busca en TODAS las posibles listas nombradas
    // que tu API puede enviar, devolviendo la que encuentre o una lista vacía.
    val content: List<T>
        get() = embedded?.productoResponseList
            ?: embedded?.pedidoResponseList
            ?: embedded?.resenaResponseList
            ?: embedded?.historialPedidoResponseList // <-- ¡LA PIEZA CLAVE QUE FALTABA!
            ?: embedded?.userResponseList
            ?: embedded?.tipoProductoResponseList
            ?: emptyList()
    // --- -------------------------------------------- ---

    // Propiedades de ayuda para la paginación
    val totalPages: Int get() = page?.totalPages ?: 0
    val totalElements: Long get() = page?.totalElements ?: 0L
    val number: Int get() = page?.number ?: 0
    val size: Int get() = page?.size ?: 0
    val first: Boolean get() = number == 0
    // Lógica robusta para `last`: es la última si no hay páginas, o si el número de página actual es el último.
    val last: Boolean get() = totalPages == 0 || number >= totalPages - 1
}

/**
 * Contenedor para TODAS las posibles listas nombradas que tu API puede devolver
 * dentro del bloque "_embedded". GSON usará esta clase para mapear el JSON.
 */
data class Embedded<T>(
    @SerializedName("productoResponseList")
    val productoResponseList: List<T>?,

    @SerializedName("pedidoResponseList")
    val pedidoResponseList: List<T>?,

    @SerializedName("resenaResponseList")
    val resenaResponseList: List<T>?,

    // --- 👇👇👇 ¡LA PROPIEDAD QUE GSON NECESITABA PARA ENTENDER! 👇👇👇 ---
    @SerializedName("historialPedidoResponseList")
    val historialPedidoResponseList: List<T>?,
    // --- ----------------------------------------------------------------- ---

    @SerializedName("userResponseList")
    val userResponseList: List<T>?,

    @SerializedName("tipoProductoResponseList")
    val tipoProductoResponseList: List<T>?
)

// El resto del archivo no requiere cambios, son estructuras estándar de HATEOAS.
data class Links(
    val self: Link?,
    val first: Link?,
    val prev: Link?,
    val next: Link?,
    val last: Link?
)

data class Link(val href: String?)

data class PageInfo(
    val size: Int?,
    val totalElements: Long?,
    val totalPages: Int?,
    val number: Int?
)
