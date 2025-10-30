package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.remote.AuthApi
import com.example.aplicacionjetpack.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory // <-- Importar Scalars
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule { // Lo llamamos NetworkModule como tu archivo

    // --- ¡¡¡IMPORTANTE!!! ---
    // Usa la URL base de tu API (la misma que usaste en la app de admin)
    // EJEMPLO: "https://tu-url-de-ngrok.ngrok-free.dev/"
    private const val BASE_URL = "https://figurately-sinuous-isla.ngrok-free.dev" // <-- ¡¡CAMBIA ESTO!!

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor // Pide el interceptor de Auth
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor) // <-- AÑADE EL INTERCEPTOR DE AUTH
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create()) // <-- Añadir Scalars (para String)
            .addConverterFactory(GsonConverterFactory.create()) // <-- Añadir Gson (para JSON)
            .build()
    }

    // --- Proveedores de API ---

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    // TODO: Añadir @Provides para ProductApi, CarritoApi, etc. aquí
    // @Provides
    // @Singleton
    // fun provideProductApi(retrofit: Retrofit): ProductApi { ... }
}