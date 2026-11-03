package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Black,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldLight,
    onSecondary = Black,
    tertiary = CreamElevated,
    onTertiary = BrownDark,
    background = BrownDark,
    onBackground = White,
    surface = BrownSurface,
    onSurface = White,
    surfaceVariant = BrownElevated,
    onSurfaceVariant = GreyLight,
    outline = BrownBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = White,
    primaryContainer = OnGoldContainer,
    onPrimaryContainer = BrownDark,
    secondary = GoldPrimary,
    onSecondary = Black,
    tertiary = BrownSurface,
    onTertiary = White,
    background = CreamBackground,
    onBackground = CreamText,
    surface = CreamSurface,
    onSurface = CreamText,
    surfaceVariant = CreamElevated,
    onSurfaceVariant = BrownDark,
    outline = GreyLight
)

@Composable
fun BiriyaniBarsaTheme(
    darkTheme: Boolean = true, // Default to rich luxury dark mode as requested for restaurant ambiance
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
