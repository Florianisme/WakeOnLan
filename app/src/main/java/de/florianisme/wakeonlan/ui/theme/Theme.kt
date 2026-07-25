package de.florianisme.wakeonlan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = PrimaryTextColor,
    primaryContainer = PrimaryLightColor,
    onPrimaryContainer = PrimaryTextColor,
    secondary = SecondaryColorDark,
    onSecondary = Color.White,
    secondaryContainer = PrimaryLightColor,
    onSecondaryContainer = PrimaryTextColor,
    error = ResultError,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDarkColor,
    onPrimary = PrimaryTextColor,
    primaryContainer = SecondaryVariantDarkColor,
    onPrimaryContainer = PrimaryTextColorLight,
    secondary = PrimaryColor,
    onSecondary = PrimaryTextColor,
    secondaryContainer = SecondaryVariantDarkColor,
    onSecondaryContainer = PrimaryTextColorLight,
    error = ResultError,
    background = Color(0xFF121316),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF121316),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF2A2C30),
    surfaceContainerLowest = Color(0xFF0D0E11),
    surfaceContainerLow = Color(0xFF1A1C1F),
    surfaceContainer = Color(0xFF1E2023),
    surfaceContainerHigh = Color(0xFF282A2E),
    surfaceContainerHighest = Color(0xFF33353A),
)

@Composable
fun WakeOnLanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

