package com.example.adminappnova.data.dto

import com.google.gson.annotations.SerializedName // <-- Asegúrate de tener esta importación

// --- Estructura principal que coincide con Spring HATEOAS PagedModel ---
data class PagedResponse<T>(
    // Mapea el objeto JSON "_embedded"
    @SerializedName("_embedded")
    val embedded: Embedded<T>?, // Nullable por si la respuesta viene vacía

    // Mapea el objeto JSON "_links" (para navegación HATEOAS)
    @SerializedName("_links")
    val links: Links?, // Nullable

    // Mapea el objeto JSON "page" (información de paginación)
    val page: PageInfo? // Nullable
) {
    /**
     * Propiedad calculada para acceder fácilmente a la lista de contenido principal.
     * Busca la lista correcta dentro del objeto 'embedded' usando los nombres comunes
     * definidos en la clase Embedded, o devuelve una lista vacía si no se encuentra.
     */
    val content: List<T>
        get() = embedded?.productoResponseList ?: // Intenta obtener lista de productos
        embedded?.pedidoResponseList ?:    // Si no, intenta obtener lista de pedidos
        embedded?.userResponseList ?:      // Si no, intenta obtener lista de usuarios
        embedded?.tipoProductoResponseList ?: // Si no, intenta obtener lista de categorías
        emptyList() // Si no encuentra ninguna, devuelve lista vacía

    // --- Propiedades calculadas para facilitar el acceso a la información de paginación ---

    // Número total de páginas disponibles. Devuelve 0 si no hay información.
    val totalPages: Int
        get() = page?.totalPages ?: 0

    // Número total de elementos en todas las páginas. Devuelve 0 si no hay información.
    val totalElements: Long
        get() = page?.totalElements ?: 0L

    // Número de la página actual (base 0). Devuelve 0 si no hay información.
    val number: Int
        get() = page?.number ?: 0

    // Tamaño de la página (cuántos items por página). Devuelve 0 si no hay información.
    val size: Int
        get() = page?.size ?: 0

    // Indica si esta es la primera página. Calculado a partir del número de página.
    val first: Boolean
        get() = number == 0

    // Indica si esta es la última página. Calculado a partir del número y total de páginas.
    val last: Boolean
        // Es la última si totalPages es 0 (lista vacía) o si el número actual es el último índice.
        get() = totalPages == 0 || number >= totalPages - 1
}

// --- Clases anidadas para representar la estructura HATEOAS ---

/**
 * Representa el objeto JSON `_embedded`.
 * Contiene propiedades nullable para cada posible nombre de lista que tu API
 * pueda devolver (ej: "productoResponseList", "pedidoResponseList").
 * Gson/Moshi llenarán la propiedad correspondiente basándose en el JSON recibido.
 */
data class Embedded<T>(
    // Mapea la lista JSON "productoResponseList" a esta propiedad
    @SerializedName("productoResponseList")
    val productoResponseList: List<T>?,

    // Mapea la lista JSON "pedidoResponseList" a esta propiedad
    @SerializedName("pedidoResponseList")
    val pedidoResponseList: List<T>?,

    // Mapea la lista JSON "userResponseList" a esta propiedad
    @SerializedName("userResponseList")
    val userResponseList: List<T>?,

    // Mapea la lista JSON "tipoProductoResponseList" a esta propiedad
    @SerializedName("tipoProductoResponseList")
    val tipoProductoResponseList: List<T>?

    // --- AÑADE MÁS CAMPOS @SerializedName SI TU API DEVUELVE OTRAS LISTAS ---
    // @SerializedName("otroNombreDeLista")
    // val otraLista: List<T>?,
    // --------------------------------------------------------------------
)

/**
 * Representa el objeto JSON `_links` que contiene los enlaces de navegación HATEOAS.
 */
data class Links(
    val self: Link?,  // Enlace a la página actual
    val first: Link?, // Enlace a la primera página
    val prev: Link?,  // Enlace a la página anterior (si existe)
    val next: Link?,  // Enlace a la página siguiente (si existe)
    val last: Link?   // Enlace a la última página
)

/**
 * Representa un enlace HATEOAS individual dentro del objeto `_links`.
 */
data class Link(
    val href: String? // La URL del enlace
)

/**
 * Representa el objeto JSON `page` que contiene metadatos sobre la paginación.
 */
data class PageInfo(
    val size: Int?,          // Número de elementos por página
    val totalElements: Long?, // Número total de elementos en todas las páginas
    val totalPages: Int?,    // Número total de páginas
    val number: Int?         // Número de la página actual (base 0)
)