package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.repository.AuthRepository
import com.example.aplicacionjetpack.data.repository.AuthRepositoryImpl
import com.example.aplicacionjetpack.data.repository.ProductRepository
import com.example.aplicacionjetpack.data.repository.ProductRepositoryImpl
import com.example.aplicacionjetpack.data.repository.ResenaRepository
import com.example.aplicacionjetpack.data.repository.ResenaRepositoryImpl
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
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    // dentro del módulo abstracto
    @Binds
    @Singleton
    abstract fun bindResenaRepository(
        resenaRepositoryImpl: ResenaRepositoryImpl
    ): ResenaRepository


    // TODO: Añadir @Binds para ProductRepository, CarritoRepository, etc. aquí
}