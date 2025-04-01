package com.mehmettekin.gunkurasiapp.domain.usecase

import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import javax.inject.Inject

class ValidateDrawSettingsUseCase @Inject constructor() {
    operator fun invoke(settings: DrawSettings): ResultState<DrawSettings> {
        // Validate participant count
        if (settings.participantCount <= 0) {
            return ResultState.Error(UiText.stringResource(R.string.error_invalid_participant_count))
        }

        // Validate participants
        if (settings.participants.size != settings.participantCount) {
            return ResultState.Error(UiText.stringResource(R.string.error_participant_count_mismatch))
        }

        // Validate monthly amount
        if (settings.monthlyAmount <= 0) {
            return ResultState.Error(UiText.stringResource(R.string.error_invalid_monthly_amount))
        }

        // Validate duration
        if (settings.durationMonths <= 0) {
            return ResultState.Error(UiText.stringResource(R.string.error_invalid_duration))
        }

        // Validate item type and specific item
        when (settings.itemType) {
            ItemType.CURRENCY, ItemType.GOLD -> {
                if (settings.specificItem.isBlank()) {
                    return ResultState.Error(UiText.stringResource(R.string.error_missing_specific_item))
                }
            }
            else -> { /* TL doesn't need a specific item */ }
        }

        // Validate start month
        if (settings.startMonth < 1 || settings.startMonth > 12) {
            return ResultState.Error(UiText.stringResource(R.string.error_invalid_start_month))
        }

        return ResultState.Success(settings)
    }
}