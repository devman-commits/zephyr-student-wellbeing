package com.zephyr.wellbeing.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Periwinkle,
    onPrimary = DarkBackground,
    primaryContainer = DarkCard,
    onPrimaryContainer = Periwinkle,
    secondary = PastelMint,
    onSecondary = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    outline = Color(0xFF938F99),
    error = Color(0xFFF2B8B5)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLavender,
    onPrimary = Color.White,
    primaryContainer = LavenderLight,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = PeriwinkleDeep,
    onSecondary = Color.White,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFECE6F0),
    onSurfaceVariant = LightTextSecondary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    outline = Color(0xFF79747E),
    error = Color(0xFFB3261E)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp)
)

@Composable
fun ZephyrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        shapes = AppShapes,
        content = content
    )
}
