// Ruta: app/src/main/java/com/example/aplicacionjetpack/di/RepositoryModule.kt
package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.repository.* // Importa todo del paquete
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        // --- ¡¡¡LA CORRECCIÓN DEFINITIVA!!! ---
        // Ahora Hilt sabe que para un ProductRepository, debe usar un ProductRepositoryImpl.
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindResenaRepository(
        resenaRepositoryImpl: ResenaRepositoryImpl
    ): ResenaRepository

    @Binds
    @Singleton
    abstract fun bindCarritoRepository(
        carritoRepositoryImpl: CarritoRepositoryImpl
    ): CarritoRepository

    @Binds
    @Singleton
    abstract fun bindDireccionRepository(
        direccionRepositoryImpl: DireccionRepositoryImpl
    ): DireccionRepository

    @Binds
    @Singleton
    abstract fun bindPedidoRepository(
        pedidoRepositoryImpl: PedidoRepositoryImpl
    ): PedidoRepository
}
