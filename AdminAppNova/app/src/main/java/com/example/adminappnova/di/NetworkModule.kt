package com.example.adminappnova.di

// --- Importa TODOS tus ApiService ---
import com.example.adminappnova.data.api.AuthApiService
import com.example.adminappnova.data.api.CategoryApiService
import com.example.adminappnova.data.api.PedidoApiService
import com.example.adminappnova.data.api.ProductApiService
import com.example.adminappnova.data.api.UserApiService
// --- Importa tu AuthInterceptor ---
import com.example.adminappnova.data.remote.interceptor.AuthInterceptor // 👈 ¡ASEGÚRATE DE IMPORTAR ESTO!
// --- Fin Importaciones ---
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

    // Tu URL de ngrok (o la IP local si cambiaste de red)
    private const val BASE_URL = "https://figurately-sinuous-isla.ngrok-free.dev/"

    @Provides
    @Singleton
    // --- 👇 CAMBIO: Pide AuthInterceptor como parámetro ---
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        // --- -------------------------------------------- ---
        return OkHttpClient.Builder()
            // Interceptor para ver logs (útil para debug)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            // --- 👇 CAMBIO: Añade el interceptor al cliente OkHttp ---
            .addInterceptor(authInterceptor) // <--- ¡ESTA LÍNEA ES CRUCIAL!
            // ----------------------------------------------------
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // OkHttpClient ya viene con el AuthInterceptor configurado
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create()) // Primero Scalars (para String)
            .addConverterFactory(GsonConverterFactory.create()) // Luego Gson (para JSON)
            .build()
    }

    // --- Proveedores para cada API Service (Estos no cambian) ---

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService {
        return retrofit.create(CategoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePedidoApiService(retrofit: Retrofit): PedidoApiService {
        return retrofit.create(PedidoApiService::class.java)
    }

    // Asegúrate de tener el @Provides para UserApiService si lo necesitas
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

}