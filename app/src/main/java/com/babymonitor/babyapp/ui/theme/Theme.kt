package com.babymonitor.babyapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Define the Light Color Scheme using our custom baby-friendly colors from Color.kt
private val LightColorScheme = lightColorScheme(
    primary = PrimarySoft,
    onPrimary = OnPrimary,
    secondary = SecondaryCalm,
    onSecondary = OnSecondary,
    tertiary = TertiarySubtle,
    onTertiary = OnTertiary,

    background = BackgroundLight,
    onBackground = OnBackground,
    surface = SurfaceLight,
    onSurface = OnSurface,

    error = ErrorRed,
    onError = OnError,
)

@Composable
fun BabymonitorTheme(
    // We are simplifying the theme to focus on a high-contrast Light Theme for a monitoring app.
    // The darkTheme and dynamicColor parameters are kept, but we explicitly choose the LightColorScheme.
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Explicitly use the LightColorScheme for a bright, reassuring UI
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
