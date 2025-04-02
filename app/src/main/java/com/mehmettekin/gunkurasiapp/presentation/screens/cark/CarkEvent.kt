package com.mehmettekin.gunkurasiapp.presentation.screens.cark

import com.mehmettekin.gunkurasiapp.domain.model.Participant

sealed class CarkEvent {
    data object OnStartDrawClick : CarkEvent()
    data object OnAnimationComplete : CarkEvent()
    data class OnParticipantSelected(val participant: Participant) : CarkEvent()
    data object OnDrawComplete : CarkEvent()
    data object OnErrorDismiss : CarkEvent()
}