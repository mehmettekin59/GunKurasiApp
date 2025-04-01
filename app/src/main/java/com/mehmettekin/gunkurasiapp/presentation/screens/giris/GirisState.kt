package com.mehmettekin.gunkurasiapp.presentation.screens.giris

import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import java.time.YearMonth

data class GirisState(
    val participantCount: String = "",
    val participants: List<Participant> = emptyList(),
    val selectedItemType: ItemType = ItemType.TL,
    val selectedSpecificItem: String = "",
    val monthlyAmount: String = "",
    val durationMonths: String = "",
    val startMonth: Int = YearMonth.now().monthValue,
    val startYear: Int = YearMonth.now().year,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val validationResult: ResultState<Unit> = ResultState.Idle,
    val isShowingConfirmDialog: Boolean = false,
    val currencyOptions: List<String> = emptyList(),
    val goldOptions: List<String> = emptyList()
)