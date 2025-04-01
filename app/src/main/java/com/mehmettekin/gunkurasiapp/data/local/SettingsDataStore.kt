package com.mehmettekin.gunkurasiapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mehmettekin.gunkurasiapp.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DataStoreKeys.SETTINGS_DATASTORE
)

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val apiUpdateIntervalKey = intPreferencesKey(Constants.DataStoreKeys.API_UPDATE_INTERVAL)
    private val languageCodeKey = stringPreferencesKey(Constants.DataStoreKeys.LANGUAGE_CODE)

    // API Update Interval operations
    fun getApiUpdateInterval(): Flow<Int> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[apiUpdateIntervalKey] ?: Constants.DefaultSettings.DEFAULT_API_UPDATE_INTERVAL
        }
    }

    suspend fun setApiUpdateInterval(seconds: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[apiUpdateIntervalKey] = seconds
        }
    }

    // Language operations
    fun getLanguage(): Flow<String> {
        return context.settingsDataStore.data.map { preferences ->
            preferences[languageCodeKey] ?: Constants.DefaultSettings.DEFAULT_LANGUAGE
        }
    }

    suspend fun setLanguage(languageCode: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[languageCodeKey] = languageCode
        }
    }
}