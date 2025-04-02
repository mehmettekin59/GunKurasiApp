package com.mehmettekin.gunkurasiapp.presentation.screens.kapalicarsi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.domain.model.Currency
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.util.Constants
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KapalicarsiViewModel @Inject constructor(
    private val kapalicarsiRepository: KapalicarsiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(KapalicarsiState())
    val state = _state.asStateFlow()

    private var refreshJob: Job? = null

    init {
        fetchData()
        observeUpdateInterval()
    }

    fun onEvent(event: KapalicarsiEvent) {
        when (event) {
            is KapalicarsiEvent.OnRefresh -> fetchData()
            is KapalicarsiEvent.OnErrorDismiss -> dismissError()
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                Log.d("KapalicarsiViewModel", "Veri yenileme başlatıldı")

                // Yükleniyor durumunu güncelle
                _state.update { it.copy(
                    currenciesLoading = true,
                    goldLoading = true
                ) }

                // API verilerini yenile
                kapalicarsiRepository.refreshData()

                Log.d("KapalicarsiViewModel", "Veri yenileme tamamlandı")

                // Repository'den veri akışlarını izle
                collectCurrencies()
                collectGold()
            } catch (e: Exception) {
                Log.e("KapalicarsiViewModel", "Veri yenileme hatası: ${e.message}", e)

                _state.update { it.copy(
                    currenciesLoading = false,
                    goldLoading = false,
                    error = UiText.dynamicString("Veri yükleme hatası: ${e.message}")
                )}
            }
        }
    }

    private fun collectCurrencies() {
        viewModelScope.launch {
            kapalicarsiRepository.getCurrencies().collectLatest { result ->
                Log.d("KapalicarsiViewModel", "Currency sonucu alındı: $result")

                when (result) {
                    is ResultState.Success -> {
                        _state.update { it.copy(
                            currencies = result.data,
                            currenciesLoading = false,
                            lastUpdated = System.currentTimeMillis()
                        )}
                    }
                    is ResultState.Error -> {
                        Log.e("KapalicarsiViewModel", "Currency hatası: ${result.message}")

                        _state.update { it.copy(
                            currenciesLoading = false,
                            error = result.message
                        )}
                    }
                    is ResultState.Loading -> {
                        _state.update { it.copy(
                            currenciesLoading = true
                        )}
                    }
                    else -> {} // Idle
                }
            }
        }
    }

    private fun collectGold() {
        viewModelScope.launch {
            kapalicarsiRepository.getGold().collectLatest { result ->
                Log.d("KapalicarsiViewModel", "Gold sonucu alındı: $result")

                when (result) {
                    is ResultState.Success -> {
                        _state.update { it.copy(
                            gold = result.data,
                            goldLoading = false,
                            lastUpdated = System.currentTimeMillis()
                        )}
                    }
                    is ResultState.Error -> {
                        Log.e("KapalicarsiViewModel", "Gold hatası: ${result.message}")

                        _state.update { it.copy(
                            goldLoading = false,
                            error = result.message
                        )}
                    }
                    is ResultState.Loading -> {
                        _state.update { it.copy(
                            goldLoading = true
                        )}
                    }
                    else -> {} // Idle
                }
            }
        }
    }

    private fun observeUpdateInterval() {
        viewModelScope.launch {
            kapalicarsiRepository.getUpdateInterval().collectLatest { intervalSeconds ->
                // Cancel previous job if running
                refreshJob?.cancel()

                // Start new refresh job with new interval
                refreshJob = viewModelScope.launch {
                    while (true) {
                        delay(intervalSeconds * 1000L)
                        Log.d("KapalicarsiViewModel", "Otomatik veri yenileme ($intervalSeconds saniye)")
                        fetchData()
                    }
                }

                // Update state with new interval
                _state.update { it.copy(
                    updateIntervalSeconds = intervalSeconds
                )}
            }
        }
    }

    fun showMockData() {
        Log.d("KapalicarsiViewModel", "Mock veriler kullanılıyor")

        val mockCurrencies = listOf(
            Currency(Constants.CurrencyCodes.USD, "Amerikan Doları", 32.1547, 32.2354, System.currentTimeMillis()),
            Currency(Constants.CurrencyCodes.EUR, "Euro", 34.7216, 34.8054, System.currentTimeMillis()),
            Currency(Constants.CurrencyCodes.GBP, "İngiliz Sterlini", 40.5623, 40.6874, System.currentTimeMillis()),
            Currency(Constants.CurrencyCodes.JPY, "Japon Yeni", 0.2104, 0.2117, System.currentTimeMillis()),
            Currency(Constants.CurrencyCodes.CAD, "Kanada Doları", 23.6127, 23.6982, System.currentTimeMillis()),
            Currency(Constants.CurrencyCodes.SAR, "Suudi Riyali", 8.5654, 8.5981, System.currentTimeMillis())
        )

        val mockGold = listOf(
            Gold(Constants.GoldCodes.ALTIN, "Gram Altın", 2278.45, 2281.12, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.CEYREK_ESKI, "Çeyrek (Eski)", 3723.94, 3789.12, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.CEYREK_YENI, "Çeyrek (Yeni)", 3712.45, 3774.65, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.YARIM_ESKI, "Yarım (Eski)", 7447.87, 7578.45, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.YARIM_YENI, "Yarım (Yeni)", 7424.90, 7549.30, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.TEK_ESKI, "Tam (Eski)", 14895.74, 15156.90, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.TEK_YENI, "Tam (Yeni)", 14849.80, 15098.60, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.ATA_ESKI, "Ata (Eski)", 14823.15, 15087.42, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.ATA_YENI, "Ata (Yeni)", 14789.45, 15032.17, System.currentTimeMillis()),
            Gold(Constants.GoldCodes.ONS, "Ons Altın", 3116.60, 3117.00, System.currentTimeMillis())
        )

        _state.update { it.copy(
            currencies = mockCurrencies,
            gold = mockGold,
            currenciesLoading = false,
            goldLoading = false,
            lastUpdated = System.currentTimeMillis()
        )}
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        refreshJob?.cancel()
        super.onCleared()
    }
}