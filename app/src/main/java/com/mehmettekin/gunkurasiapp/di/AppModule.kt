package com.mehmettekin.gunkurasiapp.di

import android.content.Context
import com.mehmettekin.gunkurasiapp.data.local.DrawResultsDataStore
import com.mehmettekin.gunkurasiapp.data.local.SettingsDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideDrawResultsDataStore(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): DrawResultsDataStore {
        return DrawResultsDataStore(context, moshi)
    }
}