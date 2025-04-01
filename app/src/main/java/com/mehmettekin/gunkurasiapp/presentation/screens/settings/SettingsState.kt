package com.mehmettekin.gunkurasiapp.presentation.screens.settings

import com.mehmettekin.gunkurasiapp.util.Constants
import com.mehmettekin.gunkurasiapp.util.UiText

data class SettingsState(
    val isLoading: Boolean = false,
    val apiUpdateInterval: Int = Constants.DefaultSettings.DEFAULT_API_UPDATE_INTERVAL,
    val selectedLanguage: String = Constants.DefaultSettings.DEFAULT_LANGUAGE,
    val error: UiText? = null
)
