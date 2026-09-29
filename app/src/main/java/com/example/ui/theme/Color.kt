package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Clean Theme Colors
val CleanNavy = Color(0xFF1E3A8A)
val CleanNavyDark = Color(0xFF172554)
val CleanBlue = Color(0xFF2563EB)
val CleanBackground = Color(0xFFF8FAFC)
val CleanSurface = Color(0xFFFFFFFF)
val CleanSurfaceVariant = Color(0xFFF1F5F9)
val CleanTextPrimary = Color(0xFF0F172A)
val CleanTextSecondary = Color(0xFF475569)

// Modern Indigo Theme Colors
val ModernIndigoPrimary = Color(0xFF4F46E5)
val ModernIndigoSecondary = Color(0xFF6366F1)
val ModernIndigoBackground = Color(0xFFF5F3FF)
val ModernIndigoSurface = Color(0xFFFFFFFF)
val ModernIndigoSurfaceVariant = Color(0xFFEDE9FE)
val ModernIndigoTextPrimary = Color(0xFF1E1B4B)
val ModernIndigoTextSecondary = Color(0xFF4338CA)

// Executive Slate Theme Colors
val ExecutiveSlatePrimary = Color(0xFF38BDF8)
val ExecutiveSlateSecondary = Color(0xFF818CF8)
val ExecutiveSlateBackground = Color(0xFF090D16)
val ExecutiveSlateSurface = Color(0xFF0F172A)
val ExecutiveSlateSurfaceVariant = Color(0xFF1E293B)
val ExecutiveSlateTextPrimary = Color(0xFFF8FAFC)
val ExecutiveSlateTextSecondary = Color(0xFF94A3B8)
val ExecutiveSlateBorder = Color(0xFF334155)

// Status Colors
val StatusSuccess = Color(0xFF059669)
val StatusSuccessBg = Color(0xFFECFDF5)
val StatusWarning = Color(0xFFD97706)
val StatusWarningBg = Color(0xFFFFFBEB)
val StatusError = Color(0xFFDC2626)
val StatusErrorBg = Color(0xFFFEF2F2)

// Vibrant Button & Gradient Styles
val UploadGradientStart = Color(0xFF4F46E5) // Electric Indigo
val UploadGradientMid = Color(0xFF2563EB)   // Vibrant Blue
val UploadGradientEnd = Color(0xFF06B6D4)   // Radiant Cyan
val UploadBrush = Brush.horizontalGradient(
    listOf(UploadGradientStart, UploadGradientMid, UploadGradientEnd)
)

val DownloadGradientStart = Color(0xFF047857) // Deep Emerald
val DownloadGradientMid = Color(0xFF059669)   // Vibrant Green
val DownloadGradientEnd = Color(0xFF10B981)   // Bright Mint
val DownloadBrush = Brush.horizontalGradient(
    listOf(DownloadGradientStart, DownloadGradientMid, DownloadGradientEnd)
)

val AccentGold = Color(0xFFF59E0B)
val AccentCoral = Color(0xFFF43F5E)
val AccentViolet = Color(0xFF8B5CF6)
val AccentCyan = Color(0xFF06B6D4)

val TopBarGradient = Brush.horizontalGradient(
    listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF4F46E5))
)

val RawPdfGradient = Brush.horizontalGradient(
    listOf(Color(0xFFE11D48), Color(0xFFF43F5E), Color(0xFFFB7185))
)

val HtmlExportGradient = Brush.horizontalGradient(
    listOf(Color(0xFF1D4ED8), Color(0xFF2563EB), Color(0xFF38BDF8))
)

val MarkdownGradient = Brush.horizontalGradient(
    listOf(Color(0xFF059669), Color(0xFF10B981), Color(0xFF14B8A6))
)

val Base64Gradient = Brush.horizontalGradient(
    listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6), Color(0xFFA78BFA))
)
