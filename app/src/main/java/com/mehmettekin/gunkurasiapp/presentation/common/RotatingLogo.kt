package com.mehmettekin.gunkurasiapp.presentation.common

import androidx.compose.animation.core.Animatable

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mehmettekin.gunkurasiapp.R
import kotlinx.coroutines.delay


@Composable
fun RotatingLogo(
    modifier: Modifier = Modifier,
    onAnimationFinish: () -> Unit = {}
) {
    // Rotation animation state
    val rotation = remember { Animatable(0f) }

    // Start animation
    LaunchedEffect(key1 = true) {
        // Start slow
        rotation.animateTo(
            targetValue = 120f,
            animationSpec = tween(durationMillis = 1000, delayMillis = 500)
        )

        // Speed up
        rotation.animateTo(
            targetValue = 720f,
            animationSpec = tween(durationMillis = 1500)
        )

        // Slow down
        rotation.animateTo(
            targetValue = 1080f,
            animationSpec = tween(durationMillis = 1000)
        )

        // Complete animation
        delay(500)
        onAnimationFinish()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation.value)
        )
    }
}
