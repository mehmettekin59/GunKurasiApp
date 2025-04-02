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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import com.mehmettekin.gunkurasiapp.ui.theme.Secondary
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WheelOfFortune(
    participants: List<Participant>,
    isSpinning: Boolean,
    onSpinComplete: (Participant) -> Unit, // Değişiklik: Kazanan katılımcıyı döndür
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) return

    // Define our rotation animation
    var currentRotation by remember { mutableFloatStateOf(0f) }
    var targetRotation by remember { mutableFloatStateOf(0f) }
    var selectedParticipant by remember { mutableStateOf<Participant?>(null) }

    // Renkler listesi - daha fazla renk çeşitliliği için
    val sectionColors = listOf(
        NavyBlue,
        Gold,
        Primary,
        Secondary,
        Color(0xFF4CAF50), // Yeşil
        Color(0xFFF44336), // Kırmızı
        Color(0xFF9C27B0), // Mor
        Color(0xFF2196F3)  // Mavi
    )

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

                // Belirle kazananı:
                // Dönüş açısına göre kazanan katılımcıyı hesaplıyoruz
                // NOT: Kazanan, göstergenin yukarıda olduğu dilimin katılımcısıdır
                // İşaretçi yukarıda (0°) olduğu için, çarkın dönüş açısına göre dilimi belirlemeliyiz
                // Normalize edilmiş açı hesaplama (0-360 arasında)
                val normalizedAngle = (currentRotation % 360 + 360) % 360

                // Hesaplanan açıya göre kazanan katılımcının indeksini bulalım
                // Açıyı bölüştürme açısına bölerek indeks elde ediyoruz, ancak çarkın tersine dönüşü için ayarlama gerekir
                val winnerIndex = (participants.size - (normalizedAngle / sliceAngle).toInt()) % participants.size

                selectedParticipant = participants.getOrNull(winnerIndex)
                selectedParticipant?.let { onSpinComplete(it) }
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

                // Draw participant names - improved positioning
                drawParticipantName(
                    textMeasurer = textMeasurer,
                    participant = participants[i],
                    angle = startAngle + (sliceAngle / 2),
                    radius = radius * 0.7f,
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

        // Draw pointer - triangle shape at top (pointing downward)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val pointerSize = size.width * 0.08f  // Pointer size proportional to wheel

            // Create a triangle path pointing downward
            val trianglePath = Path().apply {
                moveTo(centerX, pointerSize * 2)  // Bottom point
                lineTo(centerX - pointerSize, 0f)  // Top left
                lineTo(centerX + pointerSize, 0f)  // Top right
                close()
            }

            // Draw the triangle
            drawPath(
                path = trianglePath,
                color = Color.Red,
                style = Fill
            )
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
    // Make the text size appropriate for the wheel
    val fontSize = (radius * 0.12f).coerceAtLeast(12.sp.toPx()).coerceAtMost(18.sp.toPx())

    // Measure text - enforcing white color
    val textLayoutResult = textMeasurer.measure(
        text = participant.name,
        style = TextStyle(
            fontSize = fontSize.toSp(),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    )

    // Calculate position parameters
    val textWidth = textLayoutResult.size.width
    val textHeight = textLayoutResult.size.height

    // Simpler approach: translate to center, rotate, then draw text at correct offset
    translate(center.x, center.y) {
        rotate(angle + 90) {
            // Calculate offset from center for text placement
            val xOffset = radius * 0.7f - textWidth / 2
            val yOffset = -textHeight / 2

            // Draw text with white color
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(xOffset.toFloat(), yOffset.toFloat()),
                color = Color.White
            )
        }
    }
}

private fun calculatePointOnCircle(center: Offset, radius: Float, angleInDegrees: Double): Offset {
    val angleInRadians = Math.toRadians(angleInDegrees)
    val x = center.x + (radius * cos(angleInRadians)).toFloat()
    val y = center.y + (radius * sin(angleInRadians)).toFloat()
    return Offset(x, y)
}