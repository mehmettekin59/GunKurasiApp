package com.mehmettekin.gunkurasiapp.presentation.screens.giris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.domain.usecase.ValidateDrawSettingsUseCase
import com.mehmettekin.gunkurasiapp.domain.usecase.ValidateParticipantsUseCase
import com.mehmettekin.gunkurasiapp.util.Constants
import com.mehmettekin.gunkurasiapp.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@@HiltViewModel
class GirisViewModel @Inject constructor(
    private val drawRepository: DrawRepository,
    private val kapalicarsiRepository: KapalicarsiRepository,
    private val validateParticipantsUseCase: ValidateParticipantsUseCase,
    private val validateDrawSettingsUseCase: ValidateDrawSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GirisState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // Set current month and year as default
        val currentDate = YearMonth.now()
        _state.update { it.copy(
            startMonth = currentDate.monthValue,
            startYear = currentDate.year
        ) }

        // Set currency and gold options from Constants
        _state.update { it.copy(
            currencyOptions = Constants.CurrencyCodes.CURRENCY_LIST,
            goldOptions = Constants.GoldCodes.GOLD_LIST
        ) }
    }

    fun onEvent(event: GirisEvent) {
        when (event) {
            is GirisEvent.OnParticipantCountChange -> handleParticipantCountChange(event.count)
            is GirisEvent.OnAddParticipant -> handleAddParticipant(event.name)
            is GirisEvent.OnRemoveParticipant -> handleRemoveParticipant(event.participant)
            is GirisEvent.OnItemTypeSelect -> handleItemTypeSelect(event.type)
            is GirisEvent.OnSpecificItemSelect -> handleSpecificItemSelect(event.item)
            is GirisEvent.OnMonthlyAmountChange -> handleMonthlyAmountChange(event.amount)
            is GirisEvent.OnDurationChange -> handleDurationChange(event.duration)
            is GirisEvent.OnStartMonthSelect -> handleStartMonthSelect(event.month)
            is GirisEvent.OnStartYearSelect -> handleStartYearSelect(event.year)
            is GirisEvent.OnContinueClick -> handleContinueClick()
            is GirisEvent.OnConfirmDialogConfirm -> handleConfirmDialogConfirm()
            is GirisEvent.OnConfirmDialogDismiss -> handleConfirmDialogDismiss()
            is GirisEvent.OnErrorDismiss -> handleErrorDismiss()
        }
    }

    private fun handleParticipantCountChange(count: String) {
        if (count.isEmpty() || count.toIntOrNull() != null) {
            _state.update { it.copy(participantCount = count) }

            // No longer automatically create empty participants based on count
            // The user should explicitly add participants
        }
    }

    private fun handleAddParticipant(name: String) {
        if (name.isNotBlank()) {
            val updatedParticipants = _state.value.participants.toMutableList()
            updatedParticipants.add(Participant(name = name))

            // Update participant count to match the actual number of participants
            val countStr = (updatedParticipants.size).toString()

            _state.update { it.copy(
                participants = updatedParticipants,
                participantCount = countStr
            ) }
        }
    }

    private fun handleRemoveParticipant(participant: Participant) {
        val updatedParticipants = _state.value.participants.toMutableList()
        updatedParticipants.remove(participant)

        // Update participant count
        val countStr = (updatedParticipants.size).toString()

        _state.update { it.copy(
            participants = updatedParticipants,
            participantCount = countStr
        ) }
    }

    private fun handleItemTypeSelect(type: ItemType) {
        _state.update { it.copy(
            selectedItemType = type,
            // Reset specific item when type changes
            selectedSpecificItem = ""
        ) }
    }

    private fun handleSpecificItemSelect(item: String) {
        _state.update { it.copy(selectedSpecificItem = item) }
    }

    private fun handleMonthlyAmountChange(amount: String) {
        // Only allow valid double format
        if (amount.isEmpty() || amount.toDoubleOrNull() != null) {
            _state.update { it.copy(monthlyAmount = amount) }
        }
    }

    private fun handleDurationChange(duration: String) {
        // Only allow valid integer format
        if (duration.isEmpty() || duration.toIntOrNull() != null) {
            _state.update { it.copy(durationMonths = duration) }
        }
    }

    private fun handleStartMonthSelect(month: Int) {
        _state.update { it.copy(startMonth = month) }
    }

    private fun handleStartYearSelect(year: Int) {
        _state.update { it.copy(startYear = year) }
    }

    private fun handleContinueClick() {
        viewModelScope.launch {
            // Show loading
            _state.update { it.copy(isLoading = true) }

            // Validate participants
            val participantsResult = validateParticipantsUseCase(_state.value.participants)

            if (participantsResult is ResultState.Error) {
                _state.update { it.copy(
                    isLoading = false,
                    error = participantsResult.message
                ) }
                return@launch
            }

            // Validate settings
            val participantCount = _state.value.participantCount.toIntOrNull() ?: 0
            val monthlyAmount = _state.value.monthlyAmount.toDoubleOrNull() ?: 0.0
            val durationMonths = _state.value.durationMonths.toIntOrNull() ?: 0

            val settings = DrawSettings(
                participantCount = participantCount,
                participants = _state.value.participants,
                itemType = _state.value.selectedItemType,
                specificItem = _state.value.selectedSpecificItem,
                monthlyAmount = monthlyAmount,
                durationMonths = durationMonths,
                startMonth = _state.value.startMonth,
                startYear = _state.value.startYear
            )

            val settingsResult = validateDrawSettingsUseCase(settings)

            if (settingsResult is ResultState.Error) {
                _state.update { it.copy(
                    isLoading = false,
                    error = settingsResult.message
                ) }
                return@launch
            }

            // If validation successful, show confirmation dialog
            _state.update { it.copy(
                isLoading = false,
                isShowingConfirmDialog = true
            ) }
        }
    }

    private fun handleConfirmDialogConfirm() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Save settings
            val participantCount = _state.value.participantCount.toIntOrNull() ?: 0
            val monthlyAmount = _state.value.monthlyAmount.toDoubleOrNull() ?: 0.0
            val durationMonths = _state.value.durationMonths.toIntOrNull() ?: 0

            val settings = DrawSettings(
                participantCount = participantCount,
                participants = _state.value.participants,
                itemType = _state.value.selectedItemType,
                specificItem = _state.value.selectedSpecificItem,
                monthlyAmount = monthlyAmount,
                durationMonths = durationMonths,
                startMonth = _state.value.startMonth,
                startYear = _state.value.startYear
            )

            val saveResult = drawRepository.saveDrawSettings(settings)

            if (saveResult is ResultState.Error) {
                _state.update { it.copy(
                    isLoading = false,
                    error = saveResult.message,
                    isShowingConfirmDialog = false
                ) }
                return@launch
            }

            // Save participants
            val saveParticipantsResult = drawRepository.saveParticipants(_state.value.participants)

            if (saveParticipantsResult is ResultState.Error) {
                _state.update { it.copy(
                    isLoading = false,
                    error = saveParticipantsResult.message,
                    isShowingConfirmDialog = false
                ) }
                return@launch
            }

            // Navigate to next screen
            _state.update { it.copy(
                isLoading = false,
                isShowingConfirmDialog = false
            ) }

            _navigationEvent.emit(Unit)
        }
    }

    private fun handleConfirmDialogDismiss() {
        _state.update { it.copy(isShowingConfirmDialog = false) }
    }

    private fun handleErrorDismiss() {
        _state.update { it.copy(error = null) }
    }
}