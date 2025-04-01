package com.mehmettekin.gunkurasiapp.presentation.screens.sonuc



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.domain.usecase.GeneratePdfUseCase
import com.mehmettekin.gunkurasiapp.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SonucViewModel @Inject constructor(
    private val drawRepository: DrawRepository,
    private val generatePdfUseCase: GeneratePdfUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SonucState())
    val state = _state.asStateFlow()

    init {
        loadDrawResults()
    }

    fun onEvent(event: SonucEvent) {
        when (event) {
            is SonucEvent.OnGeneratePdfClick -> generatePdf()
            is SonucEvent.OnViewPdfClick -> {} // PDF görüntüleme işlemi artık UI katmanında
            is SonucEvent.OnErrorDismiss -> dismissError()
        }
    }

    private fun loadDrawResults() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            drawRepository.getDrawResults().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        _state.update { it.copy(
                            isLoading = false,
                            drawResults = result.data.sortedBy { drawResult -> drawResult.date }
                        ) }
                    }
                    is ResultState.Error -> {
                        _state.update { it.copy(
                            isLoading = false,
                            error = result.message
                        ) }
                    }
                    is ResultState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    else -> {} // Idle
                }
            }
        }
    }

    private fun generatePdf() {
        if (_state.value.drawResults.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = generatePdfUseCase(_state.value.drawResults)

            when (result) {
                is ResultState.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        pdfUri = result.data,
                        isPdfGenerated = true
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

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}