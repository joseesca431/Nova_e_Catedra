package com.example.adminappnova.di

import com.example.adminappnova.data.api.*
import com.example.adminappnova.data.dto.PagedResponse
import com.example.adminappnova.data.dto.PedidoResponse
import com.example.adminappnova.data.remote.adapter.HateoasPagedResponseDeserializer
import com.example.adminappnova.data.remote.interceptor.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://figurately-sinuous-isla.ngrok-free.dev/"

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        // Construye el tipo parametrizado PagedResponse<PedidoResponse>
        val pagedPedidoType = TypeToken.getParameterized(PagedResponse::class.java, PedidoResponse::class.java).type

        return GsonBuilder()
            // Registrar el deserializador específico para PagedResponse<PedidoResponse>
            .registerTypeAdapter(pagedPedidoType, HateoasPagedResponseDeserializer<PedidoResponse>())
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // Mantengo Scalars primero (tu archivo original lo tenía) para respuestas planas si las hay
            .addConverterFactory(ScalarsConverterFactory.create())
            // Usamos el Gson personalizado que registra el deserializador
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- Proveedores de API services ---
    @Provides @Singleton fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)
    @Provides @Singleton fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService = retrofit.create(CategoryApiService::class.java)
    @Provides @Singleton fun provideProductApiService(retrofit: Retrofit): ProductApiService = retrofit.create(ProductApiService::class.java)
    @Provides @Singleton fun providePedidoApiService(retrofit: Retrofit): PedidoApiService = retrofit.create(PedidoApiService::class.java)
    @Provides @Singleton fun provideUserApiService(retrofit: Retrofit): UserApiService = retrofit.create(UserApiService::class.java)
}
