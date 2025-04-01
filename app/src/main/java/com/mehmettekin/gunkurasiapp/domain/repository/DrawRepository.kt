package com.mehmettekin.gunkurasiapp.domain.repository

import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.util.ResultState
import kotlinx.coroutines.flow.Flow

interface DrawRepository {
    suspend fun saveParticipants(participants: List<Participant>): ResultState<Unit>
    suspend fun getParticipants(): ResultState<List<Participant>>
    suspend fun saveDrawSettings(settings: DrawSettings): ResultState<Unit>
    suspend fun getDrawSettings(): ResultState<DrawSettings?>
    suspend fun saveDrawResults(results: List<DrawResult>): ResultState<Unit>
    fun getDrawResults(): Flow<ResultState<List<DrawResult>>>
    suspend fun clearDrawResults(): ResultState<Unit>
}