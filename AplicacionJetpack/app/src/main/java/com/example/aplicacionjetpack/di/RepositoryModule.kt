package com.example.aplicacionjetpack.di

import com.example.aplicacionjetpack.data.repository.AuthRepository
import com.example.aplicacionjetpack.data.repository.AuthRepositoryImpl
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

    // TODO: Añadir @Binds para ProductRepository, CarritoRepository, etc. aquí
}