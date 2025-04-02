package com.mehmettekin.gunkurasiapp.presentation.screens.cark

import com.mehmettekin.gunkurasiapp.domain.model.Participant

sealed class CarkEvent {
    data object OnStartDrawClick : CarkEvent()
    data class OnWheelRotationComplete(val finalAngle: Float) : CarkEvent() // Yeni event
    data object OnAnimationComplete : CarkEvent()
    data object OnDrawComplete : CarkEvent()
    data object OnErrorDismiss : CarkEvent()
}