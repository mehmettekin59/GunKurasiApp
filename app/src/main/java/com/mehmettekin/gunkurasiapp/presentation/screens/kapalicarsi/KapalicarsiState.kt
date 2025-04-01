package com.mehmettekin.gunkurasiapp.presentation.screens.kapalicarsi

import com.mehmettekin.gunkurasiapp.domain.model.Currency
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.util.UiText

data class KapalicarsiState(
    val currencies: List<Currency> = emptyList(),
    val gold: List<Gold> = emptyList(),
    val currenciesLoading: Boolean = false,
    val goldLoading: Boolean = false,
    val error: UiText? = null,
    val lastUpdated: Long = 0,
    val updateIntervalSeconds: Int = 30
)
