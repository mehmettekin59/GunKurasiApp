package com.mehmettekin.gunkurasiapp.presentation.screens.settings

sealed class SettingsEvent {
    data class OnApiUpdateIntervalChange(val seconds: Int) : SettingsEvent()
    data class OnLanguageChange(val languageCode: String) : SettingsEvent()
    data object OnErrorDismiss : SettingsEvent()
}