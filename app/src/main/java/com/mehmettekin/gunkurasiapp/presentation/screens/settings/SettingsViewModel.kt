package com.mehmettekin.gunkurasiapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val kapalicarsiRepository: KapalicarsiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnApiUpdateIntervalChange -> updateApiInterval(event.seconds)
            is SettingsEvent.OnLanguageChange -> updateLanguage(event.languageCode)
            is SettingsEvent.OnErrorDismiss -> dismissError()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load API update interval
            kapalicarsiRepository.getUpdateInterval().collectLatest { intervalSeconds ->
                _state.update { it.copy(
                    apiUpdateInterval = intervalSeconds,
                    isLoading = false
                ) }
            }
        }

        viewModelScope.launch {
            // Load language setting
            userPreferencesRepository.getLanguage().collectLatest { languageCode ->
                _state.update { it.copy(
                    selectedLanguage = languageCode,
                    isLoading = false
                ) }
            }
        }
    }

    private fun updateApiInterval(seconds: Int) {
        viewModelScope.launch {
            try {
                kapalicarsiRepository.setUpdateInterval(seconds)
                _state.update { it.copy(apiUpdateInterval = seconds) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = UiText.dynamicString("API güncelleme aralığı ayarlanamadı: ${e.message}")
                ) }
            }
        }
    }

    private fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setLanguage(languageCode)
                _state.update { it.copy(selectedLanguage = languageCode) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = UiText.dynamicString("Dil ayarı değiştirilemedi: ${e.message}")
                ) }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}