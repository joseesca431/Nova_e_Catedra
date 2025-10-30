package com.example.aplicacionjetpack.data.remote.interceptor

import android.util.Log
import com.example.aplicacionjetpack.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider // <-- ¡IMPORTANTE!
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    // --- 👇👇👇 ¡LA CORRECCIÓN DEFINITIVA! 👇👇👇 ---
    // No inyectamos TokenManager directamente. Inyectamos un "Proveedor" de TokenManager.
    private val tokenManagerProvider: Provider<TokenManager>
    // --- -------------------------------------------- ---
) : Interceptor {

    private val TAG = "AuthInterceptor"

    // Creamos una propiedad "lazy" que obtendrá el TokenManager solo cuando se use por primera vez.
    // Esto rompe el ciclo de dependencias que confunde a Kapt.
    private val tokenManager by lazy { tokenManagerProvider.get() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // Ahora, esta llamada no confunde a Kapt.
        val token = tokenManager.getToken()

        if (!token.isNullOrBlank()) {
            val headerValue = if (token.startsWith("Bearer ", true)) token else "Bearer $token"
            val short = if (token.length > 12) token.take(8) + "..." + token.takeLast(4) else token
            Log.d(TAG, "Añadiendo cabecera Authorization (token start=$short)")
            builder.header("Authorization", headerValue)
        } else {
            Log.d(TAG, "No hay token disponible (TokenManager), no se añade Authorization")
        }

        return chain.proceed(builder.build())
    }
}
