package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode

private val LightCleanColorScheme = lightColorScheme(
    primary = CleanNavy,
    onPrimary = Color.White,
    primaryContainer = CleanSurfaceVariant,
    onPrimaryContainer = CleanNavyDark,
    secondary = CleanBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E40AF),
    background = CleanBackground,
    onBackground = CleanTextPrimary,
    surface = CleanSurface,
    onSurface = CleanTextPrimary,
    surfaceVariant = CleanSurfaceVariant,
    onSurfaceVariant = CleanTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = StatusError,
    onError = Color.White
)

private val ModernIndigoColorScheme = lightColorScheme(
    primary = ModernIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ModernIndigoSurfaceVariant,
    onPrimaryContainer = ModernIndigoTextPrimary,
    secondary = ModernIndigoSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF312E81),
    background = ModernIndigoBackground,
    onBackground = ModernIndigoTextPrimary,
    surface = ModernIndigoSurface,
    onSurface = ModernIndigoTextPrimary,
    surfaceVariant = ModernIndigoSurfaceVariant,
    onSurfaceVariant = ModernIndigoTextSecondary,
    outline = Color(0xFFC7D2FE),
    error = StatusError,
    onError = Color.White
)

private val ExecutiveSlateColorScheme = darkColorScheme(
    primary = ExecutiveSlatePrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = ExecutiveSlatePrimary,
    secondary = ExecutiveSlateSecondary,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFC7D2FE),
    background = ExecutiveSlateBackground,
    onBackground = ExecutiveSlateTextPrimary,
    surface = ExecutiveSlateSurface,
    onSurface = ExecutiveSlateTextPrimary,
    surfaceVariant = ExecutiveSlateSurfaceVariant,
    onSurfaceVariant = ExecutiveSlateTextSecondary,
    outline = ExecutiveSlateBorder,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

@Composable
fun DocuCleanTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT_CLEAN,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.LIGHT_CLEAN -> LightCleanColorScheme
        AppThemeMode.MODERN_INDIGO -> ModernIndigoColorScheme
        AppThemeMode.EXECUTIVE_SLATE -> ExecutiveSlateColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
