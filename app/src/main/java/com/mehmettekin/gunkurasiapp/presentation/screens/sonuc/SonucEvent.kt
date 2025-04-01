package com.mehmettekin.gunkurasiapp.presentation.screens.sonuc

sealed class SonucEvent {
    data object OnGeneratePdfClick : SonucEvent()
    data object OnViewPdfClick : SonucEvent()
    data object OnErrorDismiss : SonucEvent()
}