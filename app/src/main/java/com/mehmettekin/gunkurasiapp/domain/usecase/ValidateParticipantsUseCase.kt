package com.mehmettekin.gunkurasiapp.domain.usecase

import com.mehmettekin.gunkurasiapp.R
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import javax.inject.Inject

class ValidateParticipantsUseCase @Inject constructor() {
    operator fun invoke(participants: List<Participant>): ResultState<List<Participant>> {
        // Check if participants list is empty
        if (participants.isEmpty()) {
            return ResultState.Error(UiText.stringResource(R.string.error_no_participants))
        }

        // Check if there are at least 2 participants
        if (participants.size < 2) {
            return ResultState.Error(UiText.stringResource(R.string.error_min_participants))
        }

        // Check for duplicate names
        val duplicateNames = participants
            .groupBy { it.name.trim().lowercase() }
            .filter { it.value.size > 1 }
            .keys

        if (duplicateNames.isNotEmpty()) {
            return ResultState.Error(UiText.stringResource(R.string.error_duplicate_names))
        }

        // Check for empty names
        val hasEmptyNames = participants.any { it.name.isBlank() }
        if (hasEmptyNames) {
            return ResultState.Error(UiText.stringResource(R.string.error_empty_names))
        }

        return ResultState.Success(participants)
    }
}