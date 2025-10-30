package com.example.aplicacionjetpack.data.remote.interceptor

import android.util.Log
import com.example.aplicacionjetpack.data.AuthManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d("AuthInterceptor", "Interceptando: ${originalRequest.url}")

        val token = AuthManager.authToken

        // No añadas el token a las peticiones de login o registro
        if (token == null || originalRequest.url.encodedPath.contains("/auth/login") || originalRequest.url.encodedPath.contains("/auth/register")) {
            Log.d("AuthInterceptor", "Petición pública. No se añade token.")
            return chain.proceed(originalRequest)
        }

        Log.d("AuthInterceptor", "Añadiendo cabecera Authorization")
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}