package com.mehmettekin.gunkurasiapp.presentation.screens.cark

import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.util.UiText

data class CarkState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val settings: DrawSettings? = null,
    val remainingParticipants: List<Participant> = emptyList(),
    val currentDrawResults: List<DrawResult> = emptyList(),
    val currentParticipant: Participant? = null,
    val currentMonth: String = "",
    val isSpinning: Boolean = false,
    val rotationAngle: Float = 0f,
    val drawCompleted: Boolean = false,
    val animationCompleted: Boolean = false
)

