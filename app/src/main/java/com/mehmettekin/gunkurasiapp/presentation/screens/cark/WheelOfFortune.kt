package com.mehmettekin.gunkurasiapp.presentation.screens.cark



import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import android.util.Log
import kotlin.random.Random

@Composable
fun WheelOfFortune(
    participants: List<Participant>,
    isSpinning: Boolean,
    onRotationComplete: (Float) -> Unit,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) return

    // Dönüş açısı ve hedef açı
    var currentRotation by remember { mutableFloatStateOf(0f) }
    var targetRotation by remember { mutableFloatStateOf(0f) }

    // Renkler listesi
    val segmentColors = remember {
        listOf(
            Color(0xFFE57373), // Kırmızı
            Color(0xFF81C784), // Yeşil
            Color(0xFF64B5F6), // Mavi
            Color(0xFFFFD54F), // Sarı
            Color(0xFFBA68C8), // Mor
            Color(0xFF4FC3F7), // Açık Mavi
            Color(0xFFFFB74D)  // Turuncu
        )
    }

    // Dilim açısı - 360 dereceyi katılımcı sayısına bölelim
    val sliceAngle = 360f / participants.size

    // Çarkı çevirme animasyonu
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Rastgele dönüş için değerler
            val extraRotations = 5 + Random.nextInt(3) // 5-7 tur ekstra
            val randomAngle = Random.nextFloat() * 360f // Rastgele bir son açı

            // Hedef açıyı belirle - mevcut açı + tam turlar + rastgele son açı
            targetRotation = currentRotation + 360f * extraRotations + randomAngle

            // DEBUG için loglama
            Log.d("WheelOfFortune", "Mevcut: $currentRotation, Hedef: $targetRotation, Ekstra turlar: $extraRotations, Rastgele açı: $randomAngle")
        }
    }

    // Dönüş animasyonu
    val rotation by animateFloatAsState(
        targetValue = if (isSpinning) targetRotation else currentRotation,
        animationSpec = tween(
            durationMillis = if (isSpinning) 5000 else 0,
            easing = LinearOutSlowInEasing
        ),
        finishedListener = { finalRotation ->
            if (isSpinning) {
                // Son dönüş açısını güncelle
                currentRotation = finalRotation % 360
                targetRotation = currentRotation

                // Normalize edilmiş açıyı hesapla ve ilet (0-360 arası)
                val normalizedAngle = (currentRotation % 360 + 360) % 360

                // Kazanan dilimi belirlemek için tüm dilimleri logla
                Log.d("WheelOfFortune", "Animasyon bitti. Son açı: $normalizedAngle°")

                // Her dilimin açı aralığını logla
                for (i in participants.indices) {
                    val startAngle = i * sliceAngle
                    val endAngle = (i + 1) * sliceAngle
                    Log.d("WheelOfFortune", "Dilim $i (${participants[i].name}): $startAngle° - $endAngle°")
                }

                // Son dönüş açısını ve animasyon tamamlandı olayını ilet
                onRotationComplete(normalizedAngle)
                onAnimationComplete()
            }
        }
    )

    // Metin ölçümü için
    val textMeasurer = rememberTextMeasurer()

    // Çark çizimi
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2

            // 1. Dış çerçeve çiz
            drawCircle(
                color = Color.DarkGray,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // 2. Çarkı döndür
            rotate(degrees = rotation, pivot = center) {
                // 3. Her katılımcı için bir dilim çiz
                participants.forEachIndexed { index, participant ->
                    // Başlangıç açısı
                    val startAngle = index * sliceAngle
                    val segmentColor = segmentColors[index % segmentColors.size]

                    // 3.1. Dolu dilimi çiz
                    drawArc(
                        color = segmentColor,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Fill
                    )

                    // 3.2. Dilim kenarlarını çiz
                    drawArc(
                        color = Color.White,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 3.3. Dilim merkezindeki metin pozisyonunu hesapla
                    val textAngle = (startAngle + sliceAngle / 2f) * PI.toFloat() / 180f
                    val textRadius = radius * 0.65f // Metni yarıçapın %65'ine yerleştir
                    val textX = center.x + textRadius * cos(textAngle)
                    val textY = center.y + textRadius * sin(textAngle)

                    // 3.4. Metni hazırla - uzunsa kısalt
                    val displayText = if (participant.name.length > 8) {
                        participant.name.take(8) + "..."
                    } else {
                        participant.name
                    }

                    // 3.5. Metin stilini ve ölçülerini belirle - Shadow olmadan
                    val textLayout = textMeasurer.measure(
                        text = displayText,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // Siyah metin rengi kullanılıyor
                        )
                    )

                    // 3.6. Metni çiz - Shadow olmadan sadece tek bir metin çizimi
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(
                            x = textX - textLayout.size.width / 2,
                            y = textY - textLayout.size.height / 2
                        )
                    )
                }
            }

            // 4. Çark merkezi (sabit)
            drawCircle(
                color = Color.White,
                radius = 15.dp.toPx(),
                center = center
            )

            // 5. İşaretçi (sabit - yukarıda 0 derecede)
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