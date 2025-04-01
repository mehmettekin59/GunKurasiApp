package com.mehmettekin.gunkurasiapp.domain.repository

import com.mehmettekin.gunkurasiapp.domain.model.Currency
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.util.ResultState
import kotlinx.coroutines.flow.Flow

interface KapalicarsiRepository {
    fun getCurrencies(): Flow<ResultState<List<Currency>>>
    fun getGold(): Flow<ResultState<List<Gold>>>
    suspend fun refreshData()
    fun getUpdateInterval(): Flow<Int>
    suspend fun setUpdateInterval(seconds: Int)
}