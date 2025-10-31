package com.example.adminappnova.di

import com.example.adminappnova.data.api.*
import com.example.adminappnova.data.dto.PedidoResponse // <-- ¡IMPORTA EL DTO!
import com.example.adminappnova.data.remote.adapter.PedidoResponseDeserializer // <-- ¡IMPORTA EL DESERIALIZADOR!
import com.example.adminappnova.data.remote.interceptor.AuthInterceptor
import com.google.gson.GsonBuilder // <-- ¡IMPORTA GSON BUILDER!
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://figurately-sinuous-isla.ngrok-free.dev/"

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // --- 👇 ¡LA LÓGICA DE LA VICTORIA! 👇 ---
        // 1. Crea un constructor de Gson.
        val gson = GsonBuilder()
            // 2. Registra nuestro deserializador personalizado para la clase PedidoResponse.
            .registerTypeAdapter(PedidoResponse::class.java, PedidoResponseDeserializer())
            // 3. Construye la instancia de Gson.
            .create()
        // --- --------------------------------- ---

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            // 4. ¡Usa nuestra instancia de Gson personalizada en Retrofit!
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- El resto de los proveedores no cambian ---
    @Provides @Singleton fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)
    @Provides @Singleton fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService = retrofit.create(CategoryApiService::class.java)
    @Provides @Singleton fun provideProductApiService(retrofit: Retrofit): ProductApiService = retrofit.create(ProductApiService::class.java)
    @Provides @Singleton fun providePedidoApiService(retrofit: Retrofit): PedidoApiService = retrofit.create(PedidoApiService::class.java)
    @Provides @Singleton fun provideUserApiService(retrofit: Retrofit): UserApiService = retrofit.create(UserApiService::class.java)
}
