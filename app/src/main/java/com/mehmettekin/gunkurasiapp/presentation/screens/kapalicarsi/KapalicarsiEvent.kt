package com.mehmettekin.gunkurasiapp.presentation.screens.kapalicarsi

sealed class KapalicarsiEvent {
    data object OnRefresh : KapalicarsiEvent()
    data object OnErrorDismiss : KapalicarsiEvent()
}