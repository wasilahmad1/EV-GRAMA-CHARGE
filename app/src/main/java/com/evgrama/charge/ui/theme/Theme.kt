package com.evgrama.charge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = ElectricGreen,
    background = DeepDarkGrey,
    surface = SurfaceGrey,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    error = ErrorRed
)

@Composable
fun EVGramaChargeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
