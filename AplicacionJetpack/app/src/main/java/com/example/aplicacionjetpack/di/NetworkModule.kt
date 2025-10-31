package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.api.*
import com.example.aplicacionjetpack.data.remote.interceptor.AuthInterceptor
import com.example.aplicacionjetpack.data.repository.ParametroRepository
import com.example.aplicacionjetpack.data.repository.ParametroRepositoryImpl
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
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // --- PROVEEDORES DE APIs ---
    @Provides @Singleton fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
    @Provides @Singleton fun provideProductApi(retrofit: Retrofit): ProductApi = retrofit.create(ProductApi::class.java)
    @Provides @Singleton fun provideResenaApi(retrofit: Retrofit): ResenaApi = retrofit.create(ResenaApi::class.java)
    @Provides @Singleton fun provideCarritoApi(retrofit: Retrofit): CarritoApi = retrofit.create(CarritoApi::class.java)
    @Provides @Singleton fun provideDireccionApi(retrofit: Retrofit): DireccionApi = retrofit.create(DireccionApi::class.java)
    @Provides @Singleton fun providePedidoApi(retrofit: Retrofit): PedidoApi = retrofit.create(PedidoApi::class.java)
    @Provides @Singleton fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
    @Provides @Singleton fun provideNotificacionApi(retrofit: Retrofit): NotificacionApi = retrofit.create(NotificacionApi::class.java)
    @Provides @Singleton fun provideHistorialPedidoApi(retrofit: Retrofit): HistorialPedidoApi = retrofit.create(HistorialPedidoApi::class.java)

    // --- Nuevo: proveedor para ParametroService / ParametroApi ---
    @Provides
    @Singleton
    fun provideParametroService(retrofit: Retrofit): ParametroService =
        retrofit.create(ParametroService::class.java)

    @Provides
    @Singleton
    fun provideParametroRepository(service: ParametroService): ParametroRepository =
        ParametroRepositoryImpl(service)

}
