package com.mehmettekin.gunkurasiapp.presentation.screens.cark


import android.os.Build
import android.util.Log
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


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
    private var finalResults: MutableList<DrawResult> = mutableListOf()

    init {
        loadDrawSettings()
    }

    fun onEvent(event: CarkEvent) {
        when (event) {
            is CarkEvent.OnStartDrawClick -> handleStartDraw()
            is CarkEvent.OnWheelRotationComplete -> handleWheelRotationComplete(event.finalAngle)
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

                    // Generate draw results template (sadece aylar ve para miktarları)
                    allDrawResults = generateDrawResultsUseCase(settings)
                    finalResults = mutableListOf()

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

        // Eğer son katılımcıya geldiyse, otomatik olarak ata (çark çevirme işlemine gerek olmadan)
        if (_state.value.remainingParticipants.size == 1 && _state.value.settings != null) {
            val lastParticipant = _state.value.remainingParticipants.first()

            // Son katılımcıyı doğrudan seç
            _state.update { it.copy(
                currentParticipant = lastParticipant
            ) }

            // Doğrudan animasyonu tamamlandı olarak işaretle
            handleAnimationComplete()
            return
        }

        // Normal durumlarda çekiliş yapılacak - sadece çarkı döndürmeye başla
        _state.update { it.copy(
            isSpinning = true,
            rotationAngle = 0f
        ) }
    }

    private fun handleWheelRotationComplete(finalAngle: Float) {
        val participants = _state.value.remainingParticipants
        if (participants.isEmpty()) return

        // Açıyı 0-360 arasına normalize et
        val normalizedAngle = (finalAngle % 360 + 360) % 360

        // Dilim açısını hesapla
        val sliceAngle = 360f / participants.size

        // Log: Temel bilgiler
        Log.d("CarkViewModel", "Normalize Açı: $normalizedAngle°, Dilim Açısı: $sliceAngle°, Katılımcı Sayısı: ${participants.size}")

        // KRİTİK DEĞİŞİKLİK: Açının hangi dilime denk geldiğini doğru hesapla
        // Her bir dilimin açı aralığını kontrol ederek kazananı bul
        var foundWinnerIndex = -1
        for (i in participants.indices) {
            val startAngle = i * sliceAngle
            val endAngle = (i + 1) * sliceAngle

            // Log: Her dilimin açı aralığı
            Log.d("CarkViewModel", "Kontrol: Dilim $i (${participants[i].name}): $startAngle° - $endAngle°")

            // Açının bu dilime denk gelip gelmediğini kontrol et
            if (normalizedAngle >= startAngle && normalizedAngle < endAngle) {
                foundWinnerIndex = i
                Log.d("CarkViewModel", "BULUNAN KAZANAN: Açı $normalizedAngle, Dilim $i (${participants[i].name})")
                break
            }
        }

        // Hiçbir dilim bulunamadıysa (360 derece tam sınırda), son dilime düşmüş sayalım
        if (foundWinnerIndex == -1) {
            foundWinnerIndex = participants.size - 1
            Log.d("CarkViewModel", "Dilim bulunamadı, son dilim kullanılıyor: $foundWinnerIndex")
        }

        // Güvenlik kontrolü
        val safeIndex = foundWinnerIndex.coerceIn(0, participants.size - 1)

        // Kazanan katılımcıyı belirle
        val winningParticipant = participants[safeIndex]
        Log.d("CarkViewModel", "Kesin Kazanan: ${winningParticipant.name}")

        // Kazanan katılımcıyı state'e kaydet
        _state.update { it.copy(
            currentParticipant = winningParticipant,
            finalRotationAngle = normalizedAngle
        ) }
    }

    private fun handleAnimationComplete() {
        val currentParticipant = _state.value.currentParticipant ?: return

        // Get the current draw result index
        val currentResultIndex = _state.value.currentDrawResults.size
        if (currentResultIndex < allDrawResults.size) {
            // Şablondan mevcut ay için bir sonuç al
            val nextResultTemplate = allDrawResults[currentResultIndex]

            // Yeni bir DrawResult oluştur, seçilen katılımcı bilgileriyle güncelle
            val updatedResult = DrawResult(
                participantId = currentParticipant.id,
                participantName = currentParticipant.name,
                month = nextResultTemplate.month,
                amount = nextResultTemplate.amount,
                date = System.currentTimeMillis()
            )

            // Add to current results
            val updatedResults = _state.value.currentDrawResults.toMutableList()
            updatedResults.add(updatedResult)
            finalResults.add(updatedResult)

            // Remove participant from remaining list
            val updatedParticipants = _state.value.remainingParticipants.toMutableList()
            updatedParticipants.removeIf { it.id == currentParticipant.id }

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

            // Çarkın seçtiği katılımcılarla oluşturulan nihai sonuçları kaydet
            when (val result = saveDrawResultsUseCase(finalResults)) {
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