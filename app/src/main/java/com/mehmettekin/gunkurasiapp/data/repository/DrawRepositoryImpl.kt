package com.mehmettekin.gunkurasiapp.data.repository

import com.mehmettekin.gunkurasiapp.data.local.DrawResultsDataStore
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawRepositoryImpl @Inject constructor(
    private val dataStore: DrawResultsDataStore
) : DrawRepository {

    override suspend fun saveParticipants(participants: List<Participant>): ResultState<Unit> {
        return try {
            dataStore.saveParticipants(participants)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Katılımcılar kaydedilemedi"))
        }
    }

    override suspend fun getParticipants(): ResultState<List<Participant>> {
        return try {
            val participants = dataStore.getParticipants()
            ResultState.Success(participants)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Katılımcılar alınamadı"))
        }
    }

    override suspend fun saveDrawSettings(settings: DrawSettings): ResultState<Unit> {
        return try {
            dataStore.saveDrawSettings(settings)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Kura ayarları kaydedilemedi"))
        }
    }

    override suspend fun getDrawSettings(): ResultState<DrawSettings?> {
        return try {
            val settings = dataStore.getDrawSettings()
            ResultState.Success(settings)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Kura ayarları alınamadı"))
        }
    }

    override suspend fun saveDrawResults(results: List<DrawResult>): ResultState<Unit> {
        return try {
            dataStore.saveDrawResults(results)
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Kura sonuçları kaydedilemedi"))
        }
    }

    override fun getDrawResults(): Flow<ResultState<List<DrawResult>>> {
        return dataStore.getDrawResults()
            .map<List<DrawResult>, ResultState<List<DrawResult>>> { ResultState.Success(it) }
            .catch { emit(ResultState.Error(UiText.dynamicString(it.message ?: "Kura sonuçları alınamadı"))) }
    }

    override suspend fun clearDrawResults(): ResultState<Unit> {
        return try {
            dataStore.clearDrawResults()
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString(e.message ?: "Kura sonuçları temizlenemedi"))
        }
    }
}