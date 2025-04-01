package com.mehmettekin.gunkurasiapp.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mehmettekin.gunkurasiapp.ui.theme.OnPrimary
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import com.mehmettekin.gunkurasiapp.util.UiText

@Composable
fun ErrorView(
    message: UiText,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false
) {
    val contentModifier = if (fullScreen) {
        Modifier.fillMaxSize()
    } else {
        modifier
    }

    Column(
        modifier = contentModifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message.asString(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = OnPrimary
            )
        ) {
            Text(text = "Tekrar Dene")
        }
    }
}