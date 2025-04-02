package com.mehmettekin.gunkurasiapp.presentation.screens.cark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mehmettekin.gunkurasiapp.presentation.common.LoadingIndicator
import com.mehmettekin.gunkurasiapp.presentation.navigation.Screen
import com.mehmettekin.gunkurasiapp.ui.theme.OnPrimary
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarkScreen(
    navController: NavController,
    viewModel: CarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Navigation event handling
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            navController.navigate(Screen.Sonuc.route) {
                popUpTo(Screen.Giris.route)
            }
        }
    }

    // Error handling
    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(CarkEvent.OnErrorDismiss)
        }
    }

    // Handle animation completion
    LaunchedEffect(key1 = state.animationCompleted, key2 = state.drawCompleted, key3 = state.currentDrawResults.size) {
        if ((state.animationCompleted && state.drawCompleted) ||
            (state.remainingParticipants.size == 1 && state.currentDrawResults.size == state.settings?.participantCount?.minus(1))) {
            viewModel.onEvent(CarkEvent.OnDrawComplete)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Çekiliş", color = OnPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingIndicator(fullScreen = true)
            } else {
                CarkContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CarkContent(
    state: CarkState,
    onEvent: (CarkEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Current month display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Çekilişi Yapılan Ay",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = state.currentMonth,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimary
                )
            }
        }

        // Wheel of Fortune component
        state.remainingParticipants.takeIf { it.isNotEmpty() }?.let { participants ->
            WheelOfFortune(
                participants = participants,
                isSpinning = state.isSpinning,
                onSpinComplete = {
                    onEvent(CarkEvent.OnAnimationComplete)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } ?: run {
            // Show message if no participants left
            if (!state.isLoading && state.currentDrawResults.isNotEmpty() && !state.drawCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tüm katılımcılar için çekiliş yapıldı.\nSon katılımcı: ${state.settings?.participants?.lastOrNull()?.name ?: ""}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        // Current participant display
        AnimatedVisibility(
            visible = state.currentParticipant != null && !state.isSpinning,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            state.currentParticipant?.let { participant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bu Ay Çekiliş Kazananı",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = participant.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Start button
        if (state.remainingParticipants.isNotEmpty() && !state.isSpinning) {
            Button(
                onClick = { onEvent(CarkEvent.OnStartDrawClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                enabled = !state.isSpinning
            ) {
                Text("Çarkı Çevir")
            }
        }
    }
}