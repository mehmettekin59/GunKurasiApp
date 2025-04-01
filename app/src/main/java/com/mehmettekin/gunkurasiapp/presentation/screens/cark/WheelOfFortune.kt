package com.mehmettekin.gunkurasiapp.presentation.screens.cark


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.ui.theme.Gold
import com.mehmettekin.gunkurasiapp.ui.theme.NavyBlue
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin



@Composable
fun WheelOfFortune(
    participants: List<Participant>,
    isSpinning: Boolean,
    onSpinComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) return

    // Define our rotation animation
    var currentRotation by remember { mutableStateOf(0f) }
    var targetRotation by remember { mutableStateOf(0f) }

    // Calculate the angle for each participant
    val sliceAngle = 360f / participants.size

    // Animate the rotation when isSpinning changes
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Calculate a random rotation to ensure unpredictability
            // We add at least 5 full rotations (1800 degrees) to create a good spinning effect
            val additionalRotation = (1800..3600).random().toFloat()
            // Plus a bit more to land on a specific slice
            targetRotation = currentRotation + additionalRotation
        }
    }

    // Create the rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isSpinning) targetRotation else currentRotation,
        animationSpec = tween(
            durationMillis = if (isSpinning) 5000 else 0,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { finalRotation ->
            if (isSpinning) {
                currentRotation = finalRotation % 360
                targetRotation = currentRotation
                onSpinComplete()
            }
        }
    )

    // Text measurer to calculate text dimensions
    val textMeasurer = rememberTextMeasurer()

    // Wheel drawing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw the wheel
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2
            val sectionColors = listOf(NavyBlue, Gold)

            // Draw wheel sections
            for (i in participants.indices) {
                val startAngle = i * sliceAngle
                val sectionColor = sectionColors[i % sectionColors.size]

                // Draw section
                drawArc(
                    color = sectionColor,
                    startAngle = startAngle,
                    sweepAngle = sliceAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Draw divider lines
                drawLine(
                    color = Color.White,
                    start = center,
                    end = calculatePointOnCircle(center, radius, startAngle.toDouble()),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw participant names
                drawParticipantName(
                    textMeasurer = textMeasurer,
                    participant = participants[i],
                    angle = startAngle + (sliceAngle / 2),
                    radius = radius * 0.75f,
                    center = center
                )
            }

            // Draw outer circle
            drawCircle(
                color = Color.White,
                radius = radius,
                center = center,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.cornerPathEffect(radius * 0.1f)
                )
            )
        }

        // Draw pointer
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = min(size.width, size.height) / 2

                // Draw triangle pointer
                val pointerSize = radius * 0.15f
                val pointerPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x, center.y - radius)
                    lineTo(center.x - pointerSize, center.y - radius - pointerSize)
                    lineTo(center.x + pointerSize, center.y - radius - pointerSize)
                    close()
                }

                drawPath(
                    path = pointerPath,
                    color = Color.Red,
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
            }
        }
    }
}



private fun DrawScope.drawParticipantName(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    participant: Participant,
    angle: Float,
    radius: Float,
    center: Offset
) {
    // Calculate the position for the text
    val radians = Math.toRadians(angle.toDouble())
    val x = center.x + (radius * cos(radians)).toFloat()
    val y = center.y + (radius * sin(radians)).toFloat()

    // Make the text size appropriate for the wheel
    val fontSize = (radius * 0.1f).coerceAtLeast(12.sp.toPx()).coerceAtMost(16.sp.toPx())

    // Measure text
    val textLayoutResult = textMeasurer.measure(
        text = participant.name,
        style = TextStyle(
            fontSize = fontSize.toSp(),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )

    // Position text
    val textWidth = textLayoutResult.size.width
    val textHeight = textLayoutResult.size.height
    val textPosition = Offset(
        x - textWidth / 2,
        y - textHeight / 2
    )

    withTransform({
        // İlk rotate işlemi
        rotate(angle + 90f)
        // Sonra translate işlemi
        translate(
            textPosition.x + textWidth / 2 - (center.x),
            textPosition.y + textHeight / 2 - (center.y)
        )
    }) {
        // Draw text
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(x = -textWidth.toFloat() / 2, y = -textHeight.toFloat() / 2)
        )
    }
}




private fun calculatePointOnCircle(center: Offset, radius: Float, angleInDegrees: Double): Offset {
    val angleInRadians = Math.toRadians(angleInDegrees)
    val x = center.x + (radius * cos(angleInRadians)).toFloat()
    val y = center.y + (radius * sin(angleInRadians)).toFloat()
    return Offset(x, y)
}