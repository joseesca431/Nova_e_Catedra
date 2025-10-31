package com.example.adminappnova.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa la respuesta paginada completa.
 * El 'content' ahora será una lista LIMPIA del tipo <T>.
 * Hemos eliminado la complejidad de HateoasItem.
 */data class PagedResponse<T>(
    @SerializedName("_embedded")
    val embedded: Embedded<T>?,
    @SerializedName("_links")
    val links: PageLinks?,
    val page: PageInfo?
) {
    val content: List<T>
        get() = embedded?.items ?: emptyList()

    val totalPages: Int get() = page?.totalPages ?: 0
    val totalElements: Long get() = page?.totalElements ?: 0L
    val number: Int get() = page?.number ?: 0
    val size: Int get() = page?.size ?: 0
    val last: Boolean get() = totalPages == 0 || number >= (totalPages - 1)
}

/**
 * Representa el objeto `_embedded` que contiene la lista.
 * Ahora contiene una lista LIMPIA del tipo <T>.
 */
data class Embedded<T>(
    @SerializedName(value = "pedidoResponseList", alternate = ["productoResponseList", "userResponseList", "tipoProductoResponseList"])
    val items: List<T>?
)

// El resto de los DTOs de paginación no necesitan cambios
data class PageLinks(
    val self: Link?,
    val first: Link?,
    val prev: Link?,
    val next: Link?,
    val last: Link?
)

data class Link(
    val href: String?
)

data class PageInfo(
    val size: Int?,
    val totalElements: Long?,
    val totalPages: Int?,
    val number: Int?
)
