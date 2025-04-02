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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow

import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.ui.theme.Gold
import com.mehmettekin.gunkurasiapp.ui.theme.NavyBlue
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import com.mehmettekin.gunkurasiapp.ui.theme.Secondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WheelOfFortune(
    participants: List<Participant>,
    isSpinning: Boolean,
    onSpinComplete: (Participant) -> Unit,
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) return

    // Define our rotation animation
    var currentRotation by remember { mutableFloatStateOf(0f) }
    var targetRotation by remember { mutableFloatStateOf(0f) }
    var selectedParticipant by remember { mutableStateOf<Participant?>(null) }

    // Renkler listesi
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
            val additionalRotation = (1800..3600).random().toFloat()
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

                // Kazanan katılımcıyı hesapla
                val normalizedAngle = (currentRotation % 360 + 360) % 360
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
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2

            // Dış çerçeve
            drawCircle(
                color = Color.DarkGray,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Çarkı döndür
            rotate(degrees = rotation, pivot = center) {
                // Çark dilimlerini çiz
                participants.forEachIndexed { index, participant ->
                    val startAngle = index * sliceAngle
                    val segmentColor = sectionColors[index % sectionColors.size]

                    // Dilimi çiz
                    drawArc(
                        color = segmentColor,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Fill
                    )

                    // Dilim kenarlarını çiz
                    drawArc(
                        color = Color.White,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Metin açısını ve konumunu hesapla (radyanlara çevirerek)
                    val textAngle = (startAngle + sliceAngle / 2f) * PI.toFloat() / 180f
                    val textRadius = radius * 0.65f // Metni yarıçapın %65'ine yerleştir
                    val textX = center.x + textRadius * cos(textAngle)
                    val textY = center.y + textRadius * sin(textAngle)

                    // Metni ölç - uzunsa kısalt
                    val displayText = if (participant.name.length > 8) {
                        participant.name.take(8) + "..."
                    } else {
                        participant.name
                    }

                    val textLayout = textMeasurer.measure(
                        text = displayText,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(1f, 1f),
                                blurRadius = 1f
                            )
                        )
                    )

                    // Metni çiz - tam ortada olacak şekilde
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(
                            x = textX - textLayout.size.width / 2,
                            y = textY - textLayout.size.height / 2
                        )
                    )
                }
            }

            // Merkez noktası (sabit)
            drawCircle(
                color = Color.White,
                radius = 15.dp.toPx(),
                center = center
            )

            // İşaretçiyi çiz (sabit)
            val pointerPath = Path().apply {
                moveTo(center.x, 20.dp.toPx())
                lineTo(center.x - 10.dp.toPx(), 0f)
                lineTo(center.x + 10.dp.toPx(), 0f)
                close()
            }

            drawPath(
                path = pointerPath,
                color = Color.Red,
                style = Fill
            )
        }
    }
}