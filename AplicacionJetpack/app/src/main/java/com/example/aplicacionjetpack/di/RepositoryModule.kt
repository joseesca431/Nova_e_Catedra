package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
    @Binds @Singleton abstract fun bindResenaRepository(impl: ResenaRepositoryImpl): ResenaRepository
    @Binds @Singleton abstract fun bindCarritoRepository(impl: CarritoRepositoryImpl): CarritoRepository
    @Binds @Singleton abstract fun bindDireccionRepository(impl: DireccionRepositoryImpl): DireccionRepository
    @Binds @Singleton abstract fun bindPedidoRepository(impl: PedidoRepositoryImpl): PedidoRepository
    @Binds @Singleton abstract fun bindNotificacionRepository(impl: NotificacionRepositoryImpl): NotificacionRepository
    @Binds @Singleton abstract fun bindHistorialPedidoRepository(impl: HistorialPedidoRepositoryImpl): HistorialPedidoRepository

    // --- El @Binds que movimos vive aquí, donde pertenece ---
    @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
