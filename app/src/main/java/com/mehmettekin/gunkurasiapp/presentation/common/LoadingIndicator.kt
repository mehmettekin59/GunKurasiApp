package com.mehmettekin.gunkurasiapp.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mehmettekin.gunkurasiapp.ui.theme.Secondary

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false
) {
    if (fullScreen) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Secondary
            )
        }
    } else {
        CircularProgressIndicator(
            modifier = modifier.size(48.dp),
            color = Secondary
        )
    }
}