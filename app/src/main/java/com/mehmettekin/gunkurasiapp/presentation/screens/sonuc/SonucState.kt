package com.mehmettekin.gunkurasiapp.presentation.screens.sonuc

import android.net.Uri
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.util.UiText

data class SonucState(
    val isLoading: Boolean = false,
    val drawResults: List<DrawResult> = emptyList(),
    val error: UiText? = null,
    val pdfUri: Uri? = null,
    val isPdfGenerated: Boolean = false
)