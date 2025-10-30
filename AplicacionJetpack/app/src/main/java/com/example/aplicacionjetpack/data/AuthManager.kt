package com.example.aplicacionjetpack.data

// Objeto simple para guardar el token en memoria mientras la app vive
// Para una app real, reemplaza esto con DataStore o SharedPreferences
object AuthManager {
    var authToken: String? = null
    // También podrías guardar datos del usuario aquí
    // var userId: Long? = null
    // var userRoles: List<String>? = null
}