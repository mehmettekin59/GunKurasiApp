package com.mehmettekin.gunkurasiapp.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _isAnimationFinished = MutableStateFlow(false)
    val isAnimationFinished = _isAnimationFinished.asStateFlow()

    private val _isNavigateNext = MutableStateFlow(false)
    val isNavigateNext = _isNavigateNext.asStateFlow()

    fun onAnimationFinish() {
        viewModelScope.launch {
            _isAnimationFinished.value = true
            // Extra delay for better UX
            delay(500)
            _isNavigateNext.value = true
        }
    }
}