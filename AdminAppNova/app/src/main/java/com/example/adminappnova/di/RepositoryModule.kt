package com.example.adminappnova.di

import com.example.adminappnova.data.repository.* // Importa todo del paquete
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

    // --- AÑADIDOS ---
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindPedidoRepository(
        pedidoRepositoryImpl: PedidoRepositoryImpl
    ): PedidoRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    // Añade aquí los Binds para ProductRepository, UserRepository, etc. cuando los crees
}