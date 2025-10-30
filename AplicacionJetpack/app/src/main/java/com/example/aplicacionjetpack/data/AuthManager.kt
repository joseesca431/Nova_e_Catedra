package com.example.aplicacionjetpack.data

// --- 👇👇👇 ¡LA CORRECCIÓN DEFINITIVA! 👇👇👇 ---
// Cambia "data object" a un simple "object". Kapt sí entiende esto.
object AuthManager {
    var authToken: String? = null
    var userId: Long? = null
    // var userRoles: List<String>? = null // Mantenlo comentado por ahora
}
