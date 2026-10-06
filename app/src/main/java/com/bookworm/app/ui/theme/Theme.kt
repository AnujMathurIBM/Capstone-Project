package com.bookworm.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val BookWormTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 28.sp, color = TextPrimary),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 22.sp, color = TextPrimary),
    headlineSmall  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary),
    titleMedium    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 14.sp, color = TextPrimary),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 14.sp, color = TextPrimary),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 13.sp, color = TextSecondary),
    bodySmall      = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 12.sp, color = TextMuted),
    labelLarge     = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 13.sp, color = TextPrimary),
    labelSmall     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 11.sp, color = TextMuted)
)

@Composable
fun BookWormTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BookWormDarkColorScheme,
        typography  = BookWormTypography,
        content     = content
    )
}
