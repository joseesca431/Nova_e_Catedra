package com.example.aplicacionjetpack.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO genérico para mapear PagedModel (Spring HATEOAS).
 * Incluye los posibles nombres que tu API podría devolver dentro de _embedded,
 * incluyendo "productoResponseList" y "resenaResponseList".
 */
data class PagedResponse<T>(
    @SerializedName("_embedded")
    val embedded: Embedded<T>?,
    @SerializedName("_links")
    val links: Links?,
    val page: PageInfo?
) {
    val content: List<T>
        get() = embedded?.productoResponseList ?:
        embedded?.pedidoResponseList ?:
        embedded?.userResponseList ?:
        embedded?.tipoProductoResponseList ?:
        embedded?.resenaResponseList ?: // Soporta reseñas paginadas
        emptyList()

    val totalPages: Int get() = page?.totalPages ?: 0
    val totalElements: Long get() = page?.totalElements ?: 0L
    val number: Int get() = page?.number ?: 0
    val size: Int get() = page?.size ?: 0
    val first: Boolean get() = number == 0
    val last: Boolean get() = totalPages == 0 || number >= totalPages - 1
}

data class Embedded<T>(
    @SerializedName("productoResponseList")
    val productoResponseList: List<T>?,
    @SerializedName("pedidoResponseList")
    val pedidoResponseList: List<T>?,
    @SerializedName("userResponseList")
    val userResponseList: List<T>?,
    @SerializedName("tipoProductoResponseList")
    val tipoProductoResponseList: List<T>?,
    @SerializedName("resenaResponseList")
    val resenaResponseList: List<T>? // <-- importante para reseñas paginadas
)

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
