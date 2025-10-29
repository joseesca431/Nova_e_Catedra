package com.example.adminappnova.data.remote.interceptor

import android.util.Log // Importa Log para depuración
import com.example.adminappnova.data.AuthManager // Importa tu gestor de token
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Hilt creará una sola instancia de este interceptor
class AuthInterceptor @Inject constructor() : Interceptor { // Hilt sabe cómo crearlo

    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtiene la petición original que está a punto de salir
        val originalRequest = chain.request()
        Log.d("AuthInterceptor", "Interceptando petición a: ${originalRequest.url}") // Log útil

        // Obtiene el token JWT guardado (si existe)
        val token = AuthManager.authToken

        // Construye la nueva petición
        val newRequest = if (token != null && !originalRequest.url.encodedPath.contains("/auth/login")) {
            // --- Si hay token Y NO es la petición de login ---
            Log.d("AuthInterceptor", "Añadiendo cabecera Authorization") // Log útil
            originalRequest.newBuilder()
                // Añade la cabecera estándar para JWT
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            // --- Si NO hay token O SI ES la petición de login ---
            // Deja la petición como está (sin cabecera Authorization)
            Log.d("AuthInterceptor", "No se añade cabecera Authorization (Sin token o es login)") // Log útil
            originalRequest
        }

        // Envía la petición (modificada o no) al siguiente interceptor o a la red
        return chain.proceed(newRequest)
    }
}