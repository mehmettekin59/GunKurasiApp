package com.mehmettekin.gunkurasiapp.presentation.screens.cark



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.R
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.domain.usecase.GenerateDrawResultsUseCase
import com.mehmettekin.gunkurasiapp.domain.usecase.SaveDrawResultsUseCase
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class CarkViewModel @Inject constructor(
    private val drawRepository: DrawRepository,
    private val generateDrawResultsUseCase: GenerateDrawResultsUseCase,
    private val saveDrawResultsUseCase: SaveDrawResultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CarkState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var allDrawResults: List<DrawResult> = emptyList()

    init {
        loadDrawSettings()
    }

    fun onEvent(event: CarkEvent) {
        when (event) {
            is CarkEvent.OnStartDrawClick -> handleStartDraw()
            is CarkEvent.OnAnimationComplete -> handleAnimationComplete()
            is CarkEvent.OnDrawComplete -> handleDrawComplete()
            is CarkEvent.OnErrorDismiss -> handleErrorDismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun loadDrawSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load draw settings
            when (val result = drawRepository.getDrawSettings()) {
                is ResultState.Success -> {
                    val settings = result.data
                    if (settings == null) {
                        _state.update { it.copy(
                            isLoading = false,
                            error = UiText.stringResource(R.string.error_settings_not_found)
                        ) }
                        return@launch
                    }

                    // Generate draw results
                    allDrawResults = generateDrawResultsUseCase(settings)

                    // Initialize state with remaining participants
                    _state.update { it.copy(
                        isLoading = false,
                        settings = settings,
                        remainingParticipants = settings.participants.toMutableList(),
                        currentMonth = getFormattedMonth(settings.startMonth, settings.startYear)
                    ) }
                }
                is ResultState.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message
                    ) }
                }
                else -> {} // Loading, Idle
            }
        }
    }

    private fun handleStartDraw() {
        if (_state.value.isSpinning || _state.value.remainingParticipants.isEmpty()) return

        // Get the next draw result
        if (allDrawResults.isNotEmpty() && _state.value.currentDrawResults.size < allDrawResults.size) {
            // Eğer son katılımcıya geldiyse, otomatik olarak ata (çark çevirme işlemine gerek olmadan)
            if (_state.value.remainingParticipants.size == 1 && _state.value.settings != null) {
                val lastParticipant = _state.value.remainingParticipants.first()
                val nextResult = allDrawResults[_state.value.currentDrawResults.size]

                // Son katılımcıyı doğrudan seç ve sonuçları güncelle
                _state.update { it.copy(
                    currentParticipant = lastParticipant
                ) }

                // Doğrudan animasyonu tamamlandı olarak işaretle
                handleAnimationComplete()

                // Son katılımcı atandığında, sonuç ekranına yönlendir
                if (_state.value.currentDrawResults.size + 1 >= allDrawResults.size) {
                    handleDrawComplete()
                }

                return
            }

            // Normal durumlarda çekiliş yapılacak
            val nextResult = allDrawResults[_state.value.currentDrawResults.size]

            // Find the participant for this result
            val participant = _state.value.remainingParticipants.find { it.id == nextResult.participantId }

            if (participant != null) {
                // Start spinning animation
                _state.update { it.copy(
                    isSpinning = true,
                    currentParticipant = participant,
                    rotationAngle = 0f
                ) }
            }
        }
    }

    private fun handleAnimationComplete() {
        if (_state.value.currentParticipant == null) return

        // Get the current draw result
        val currentResultIndex = _state.value.currentDrawResults.size
        if (currentResultIndex < allDrawResults.size) {
            val nextResult = allDrawResults[currentResultIndex]

            // Add to current results
            val updatedResults = _state.value.currentDrawResults.toMutableList()
            updatedResults.add(nextResult)

            // Remove participant from remaining list
            val updatedParticipants = _state.value.remainingParticipants.toMutableList()
            updatedParticipants.removeIf { it.id == _state.value.currentParticipant?.id }

            // Update month for next draw if needed
            val nextMonth = if (updatedResults.size < allDrawResults.size) {
                allDrawResults[updatedResults.size].month
            } else {
                _state.value.currentMonth
            }

            // Check if draw is completed
            val drawCompleted = updatedResults.size >= allDrawResults.size

            // Update state
            _state.update { it.copy(
                isSpinning = false,
                currentDrawResults = updatedResults,
                remainingParticipants = updatedParticipants,
                currentMonth = nextMonth,
                animationCompleted = true,
                drawCompleted = drawCompleted
            ) }

            // Save results if draw is completed
            if (drawCompleted) {
                saveResults()
            }
        }
    }

    private fun handleDrawComplete() {
        viewModelScope.launch {
            _navigationEvent.emit(Unit)
        }
    }

    private fun handleErrorDismiss() {
        _state.update { it.copy(error = null) }
    }

    private fun saveResults() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = saveDrawResultsUseCase(allDrawResults)) {
                is ResultState.Success -> {
                    _state.update { it.copy(
                        isLoading = false
                    ) }
                }
                is ResultState.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message
                    ) }
                }
                else -> {} // Loading, Idle
            }
        }
    }

    private fun getFormattedMonth(month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1) // Calendar months are 0-based
        calendar.set(Calendar.YEAR, year)

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}