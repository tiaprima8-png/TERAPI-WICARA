package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AbiBicaraLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = AmberPrimary,
    onSecondary = Color.White,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = CoralAccent,
    onTertiary = Color.White,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = OnCoralContainer,
    background = WarmWhiteBg,
    onBackground = SlateTextPrimary,
    surface = PureWhiteSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = EmeraldSurfaceTint,
    onSurfaceVariant = SlateTextSecondary,
    outline = CardBorderSoft
)

@Composable
fun AbiBicaraProTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AbiBicaraLightColorScheme,
        typography = Typography,
        content = content
    )
}
