package com.mehmettekin.gunkurasiapp.data.repository

import com.mehmettekin.gunkurasiapp.data.local.SettingsDataStore
import com.mehmettekin.gunkurasiapp.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : UserPreferencesRepository {

    override fun getLanguage(): Flow<String> {
        return settingsDataStore.getLanguage()
    }

    override suspend fun setLanguage(languageCode: String) {
        settingsDataStore.setLanguage(languageCode)
    }
}