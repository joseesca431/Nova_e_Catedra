// Ruta: app/src/main/java/com/example/aplicacionjetpack/di/NetworkModule.kt
package com.example.aplicacionjetpack.di

// --- 👇👇👇 ¡ASEGÚRATE DE QUE ESTOS IMPORTS ESTÉN! 👇👇👇 ---
import com.example.aplicacionjetpack.data.api.* // Importa TODAS las APIs
import com.example.aplicacionjetpack.data.remote.interceptor.AuthInterceptor
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

    // Cambia esto a tu URL de ngrok si es necesario
    private const val BASE_URL = "https://figurately-sinuous-isla.ngrok-free.dev/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- APIs QUE YA TENÍAS ---
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)

    @Provides
    @Singleton
    fun provideResenaApi(retrofit: Retrofit): ResenaApi =
        retrofit.create(ResenaApi::class.java)

    // --- 👇👇👇 ¡¡¡LAS APIs QUE FALTABAN!!! 👇👇👇 ---

    @Provides
    @Singleton
    fun provideCarritoApi(retrofit: Retrofit): CarritoApi =
        retrofit.create(CarritoApi::class.java)

    @Provides
    @Singleton
    fun provideDireccionApi(retrofit: Retrofit): DireccionApi =
        retrofit.create(DireccionApi::class.java)

    @Provides
    @Singleton
    fun providePedidoApi(retrofit: Retrofit): PedidoApi =
        retrofit.create(PedidoApi::class.java)
}
