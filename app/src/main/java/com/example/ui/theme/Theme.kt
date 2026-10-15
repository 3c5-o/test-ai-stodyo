package com.example.ui.theme

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

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianNavyBg,
    primaryContainer = ObsidianSurfaceBg,
    onPrimaryContainer = GoldLight,
    secondary = ElectricBlue,
    onSecondary = ObsidianNavyBg,
    secondaryContainer = ObsidianSurfaceBg,
    onSecondaryContainer = ElectricBlue,
    tertiary = EmeraldGreen,
    onTertiary = TextWhite,
    background = ObsidianNavyBg,
    onBackground = TextWhite,
    surface = ObsidianCardBg,
    onSurface = TextWhite,
    surfaceVariant = ObsidianSurfaceBg,
    onSurfaceVariant = TextMuted,
    outline = ObsidianBorder,
    error = CrimsonRed
)

private val LuxuryLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = TextWhite,
    primaryContainer = GoldLight,
    onPrimaryContainer = ObsidianNavyBg,
    secondary = ElectricBlue,
    onSecondary = TextWhite,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = CrimsonRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Luxury dark aesthetic default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LuxuryDarkColorScheme else LuxuryLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
