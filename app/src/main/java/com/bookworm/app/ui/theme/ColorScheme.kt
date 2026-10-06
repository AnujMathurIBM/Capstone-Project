package com.bookworm.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val BookWormDarkColorScheme = darkColorScheme(
    primary          = AccentBlue,
    onPrimary        = Color.White,
    primaryContainer = AccentBlueDark,
    secondary        = AccentOrange,
    onSecondary      = Color.White,
    background       = BackgroundDark,
    onBackground     = TextPrimary,
    surface          = SurfaceDark,
    onSurface        = TextPrimary,
    surfaceVariant   = CardDark,
    onSurfaceVariant = TextSecondary,
    error            = Error,
    onError          = Color.White,
    outline          = TextMuted
)
