package com.mehmettekin.gunkurasiapp.presentation.screens.giris

import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.domain.model.Participant

sealed class GirisEvent {
    data class OnParticipantCountChange(val count: String) : GirisEvent()
    data class OnAddParticipant(val name: String) : GirisEvent()
    data class OnRemoveParticipant(val participant: Participant) : GirisEvent()
    data class OnItemTypeSelect(val type: ItemType) : GirisEvent()
    data class OnSpecificItemSelect(val item: String) : GirisEvent()
    data class OnMonthlyAmountChange(val amount: String) : GirisEvent()
    data class OnDurationChange(val duration: String) : GirisEvent()
    data class OnStartMonthSelect(val month: Int) : GirisEvent()
    data class OnStartYearSelect(val year: Int) : GirisEvent()
    data object OnContinueClick : GirisEvent()
    data object OnConfirmDialogConfirm : GirisEvent()
    data object OnConfirmDialogDismiss : GirisEvent()
    data object OnErrorDismiss : GirisEvent()
}