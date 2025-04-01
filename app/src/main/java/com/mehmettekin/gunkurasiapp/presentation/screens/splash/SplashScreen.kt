package com.mehmettekin.gunkurasiapp.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mehmettekin.gunkurasiapp.presentation.common.RotatingLogo
import com.mehmettekin.gunkurasiapp.presentation.navigation.Screen
import com.mehmettekin.gunkurasiapp.ui.theme.Background

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isNavigateNext by viewModel.isNavigateNext.collectAsState()

    // Navigate when animation is complete
    LaunchedEffect(isNavigateNext) {
        if (isNavigateNext) {
            navController.navigate(Screen.Giris.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        RotatingLogo(
            onAnimationFinish = {
                viewModel.onAnimationFinish()
            }
        )
    }
}