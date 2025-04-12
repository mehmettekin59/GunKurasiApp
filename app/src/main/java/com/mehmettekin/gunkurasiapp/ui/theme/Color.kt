package com.mehmettekin.gunkurasiapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color



// Temel renkler
val NavyBlue = Color(0xFF172B53) // Lacivert
val Gold = Color(0xFFDAA520) // Altın rengi
val Gold2 = Color(0xFFBFA15F) // Altın rengi
val White = Color(0xFFFFFFFF) // Beyaz
val ErrorColor = Color(0xFFFFFFFF) // Beyaz
val SilverGray = Color(0xFFE0E0E0)

val primary = NavyBlue
val onPrimary = White
val secondary = Gold
val onSecondary = NavyBlue // Kontrastı kontrol edin, White daha iyi olabilir
val error = ErrorColor
val onError = White
val background = Color(0xFFF8F8F8) // Çok açık gri arka plan
val onBackground = Color(0xFF191C1D)
val surface = White // Kartlar, Dialoglar vb. için
val onSurface = Color(0xFF191C1D)
val surfaceVariant = Color(0xFFE7E8EC) // Katılımcı listesi gibi hafif farklı yüzeyler
val onSurfaceVariant = Color(0xFF41484D) // surfaceVariant üzerindeki metin/ikonlar
val outline = Color(0xFF71787E) // Kenarlıklar
val outlineVariant = Color(0xFFC1C7CE) // Daha hafif kenarlıklar, Divider için iyi
val scrim = Color.Black.copy(alpha = 0.6f) // Loading overlay için (kullanılmıyorsa kaldırılabilir)


private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    error = error,
    onError = onError,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyBlue.lighten(0.1f), // Karanlık temada biraz daha açık
    onPrimary = White,
    secondary = Gold.lighten(0.1f), // Karanlık temada biraz daha açık
    onSecondary = Color(0xFF121212), // Koyu arka plan için daha koyu
    error = ErrorColor.lighten(0.1f), // Karanlık temada biraz daha açık
    onError = White,
    background = Color(0xFF121212), // Material dark theme standart arka plan
    onBackground = Color(0xFFE1E1E1), // Gri-beyaz
    surface = Color(0xFF1E1E1E), // Biraz daha açık
    onSurface = Color(0xFFE1E1E1), // Gri-beyaz
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF5F5F5F),
    scrim = Color.Black.copy(alpha = 0.8f) // Karanlık temada daha yoğun
)

// Renk tonu açma yardımcı fonksiyonu
fun Color.lighten(factor: Float): Color {
    return Color(
        red = red + (1 - red) * factor,
        green = green + (1 - green) * factor,
        blue = blue + (1 - blue) * factor,
        alpha = alpha
    )
}