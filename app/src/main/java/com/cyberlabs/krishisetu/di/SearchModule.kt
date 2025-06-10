package com.cyberlabs.krishisetu.di

import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchRepository
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
}