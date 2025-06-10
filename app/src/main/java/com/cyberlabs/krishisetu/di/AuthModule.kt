package com.cyberlabs.krishisetu.di

import com.cyberlabs.krishisetu.authentication.AmplifyAuthRepository
import com.cyberlabs.krishisetu.authentication.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AmplifyAuthRepository
    ): AuthRepository
}
