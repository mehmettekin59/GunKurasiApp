package com.mehmettekin.gunkurasiapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getLanguage(): Flow<String>
    suspend fun setLanguage(languageCode: String)
}