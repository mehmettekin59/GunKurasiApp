package com.mehmettekin.gunkurasiapp.presentation.screens.cark

sealed class CarkEvent {
    data object OnStartDrawClick : CarkEvent()
    data object OnAnimationComplete : CarkEvent()
    data object OnDrawComplete : CarkEvent()
    data object OnErrorDismiss : CarkEvent()
}