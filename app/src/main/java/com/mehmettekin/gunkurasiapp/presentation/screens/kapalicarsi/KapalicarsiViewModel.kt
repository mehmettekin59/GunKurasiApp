package com.mehmettekin.gunkurasiapp.presentation.screens.kapalicarsi


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.util.ResultState
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
            kapalicarsiRepository.refreshData()
            collectCurrencies()
            collectGold()
        }
    }

    private fun collectCurrencies() {
        viewModelScope.launch {
            kapalicarsiRepository.getCurrencies().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        _state.update { it.copy(
                            currencies = result.data,
                            currenciesLoading = false,
                            lastUpdated = System.currentTimeMillis()
                        ) }
                    }
                    is ResultState.Error -> {
                        _state.update { it.copy(
                            currenciesLoading = false,
                            error = result.message
                        ) }
                    }
                    is ResultState.Loading -> {
                        _state.update { it.copy(
                            currenciesLoading = true
                        ) }
                    }
                    else -> {} // Idle
                }
            }
        }
    }

    private fun collectGold() {
        viewModelScope.launch {
            kapalicarsiRepository.getGold().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        _state.update { it.copy(
                            gold = result.data,
                            goldLoading = false,
                            lastUpdated = System.currentTimeMillis()
                        ) }
                    }
                    is ResultState.Error -> {
                        _state.update { it.copy(
                            goldLoading = false,
                            error = result.message
                        ) }
                    }
                    is ResultState.Loading -> {
                        _state.update { it.copy(
                            goldLoading = true
                        ) }
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
                        fetchData()
                    }
                }

                // Update state with new interval
                _state.update { it.copy(
                    updateIntervalSeconds = intervalSeconds
                ) }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        refreshJob?.cancel()
        super.onCleared()
    }
}
