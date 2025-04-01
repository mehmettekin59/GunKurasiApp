package com.mehmettekin.gunkurasiapp.di

import com.mehmettekin.gunkurasiapp.data.repository.DrawRepositoryImpl
import com.mehmettekin.gunkurasiapp.data.repository.KapalicarsiRepositoryImpl
import com.mehmettekin.gunkurasiapp.data.repository.UserPreferencesRepositoryImpl
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.domain.repository.UserPreferencesRepository
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
    abstract fun bindKapalicarsiRepository(
        impl: KapalicarsiRepositoryImpl
    ): KapalicarsiRepository

    @Binds
    @Singleton
    abstract fun bindDrawRepository(
        impl: DrawRepositoryImpl
    ): DrawRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}