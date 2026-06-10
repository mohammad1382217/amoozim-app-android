package com.amoozim.creator.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Brand palette ported verbatim from the web app's design tokens
 * (`src/shared/styles/globals.css` `:root`). The web app is LIGHT-MODE ONLY in
 * practice (the `.dark` block exists but is never activated), so the light scheme is
 * the source of truth; [AmoozimTheme] defaults to light to match.
 */
object BrandColors {
    val Primary = Color(0xFF6461FF)
    val PrimaryHover = Color(0xFF5E5BFF)
    val Primary50 = Color(0xFFE9E3FF)
    val Primary100 = Color(0xFFD3C8FF)
    val Primary400 = Color(0xFF877CFF)
    val Primary600 = Color(0xFF534FC6)
    val Primary700 = Color(0xFF403B93)
    val Primary900 = Color(0xFF1D1833)

    val Secondary = Color(0xFFFF8C6A) // coral
    val Success = Color(0xFF17C964)
    val Warning = Color(0xFFF5A524)
    val Danger = Color(0xFFF31260)
    val Danger50 = Color(0xFFFEE7EF)
    val Info = Color(0xFF0270EF)

    val Background = Color(0xFFFFFFFF)
    val SecondaryBackground = Color(0xFFF6F8FD)
    val Foreground = Color(0xFF11181C)
    val Divider = Color(0xFFE4E4E7)

    val Content1 = Color(0xFFFFFFFF)
    val Content2 = Color(0xFFF6FAFD)
    val Content3 = Color(0xFFF5F5F6)
    val Content4 = Color(0xFFE4E4E7)
    val Content5 = Color(0xFFD4D4D8)

    val Default100 = Color(0xFFF4F4F5)
    val Default400 = Color(0xFFA1A1AA)
    val Default700 = Color(0xFF3F3F46)
    val MutedForeground = Color(0xFFA1A1A1)
    val Muted = Color(0xFFF4F4F5)
    val Border = Color(0xFFE4E4E7)
    val Ring = Color(0xFF877CFF)

    val DarkBackground = Color(0xFF18181B)
    val DarkSurface = Color(0xFF27272A)
    val DarkOnSurface = Color(0xFFFAFAFA)
}

val AmoozimLightColorScheme = lightColorScheme(
    primary = BrandColors.Primary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = BrandColors.Primary50,
    onPrimaryContainer = BrandColors.Primary,
    secondary = BrandColors.Secondary,
    onSecondary = BrandColors.Foreground,
    tertiary = BrandColors.Info,
    onTertiary = Color(0xFFFFFFFF),
    error = BrandColors.Danger,
    onError = Color(0xFFFFFFFF),
    errorContainer = BrandColors.Danger50,
    onErrorContainer = BrandColors.Danger,
    background = BrandColors.Background,
    onBackground = BrandColors.Foreground,
    surface = BrandColors.Content1,
    onSurface = BrandColors.Foreground,
    surfaceVariant = BrandColors.Content3,
    onSurfaceVariant = BrandColors.Default700,
    surfaceContainer = BrandColors.SecondaryBackground,
    outline = BrandColors.Border,
    outlineVariant = BrandColors.Default100,
)

val AmoozimDarkColorScheme = darkColorScheme(
    primary = BrandColors.Primary400,
    onPrimary = BrandColors.Primary900,
    primaryContainer = BrandColors.Primary700,
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = BrandColors.Secondary,
    error = BrandColors.Danger,
    onError = Color(0xFFFFFFFF),
    background = BrandColors.DarkBackground,
    onBackground = BrandColors.DarkOnSurface,
    surface = BrandColors.DarkSurface,
    onSurface = BrandColors.DarkOnSurface,
    surfaceVariant = BrandColors.DarkSurface,
    onSurfaceVariant = Color(0xFFD4D4D8),
    outline = Color(0xFF3F3F46),
)

/**
 * Brand tokens with no direct Material3 slot (status colors, the content/neutral
 * ramp, focus ring). Read via [LocalAmoozimColors] so feature UI never hardcodes hex.
 */
@Immutable
data class AmoozimExtraColors(
    val success: Color,
    val warning: Color,
    val info: Color,
    val secondaryBackground: Color,
    val content1: Color,
    val content2: Color,
    val content3: Color,
    val content4: Color,
    val content5: Color,
    val divider: Color,
    val default400: Color,
    val default700: Color,
    val mutedForeground: Color,
    val ring: Color,
)

val LightExtraColors = AmoozimExtraColors(
    success = BrandColors.Success,
    warning = BrandColors.Warning,
    info = BrandColors.Info,
    secondaryBackground = BrandColors.SecondaryBackground,
    content1 = BrandColors.Content1,
    content2 = BrandColors.Content2,
    content3 = BrandColors.Content3,
    content4 = BrandColors.Content4,
    content5 = BrandColors.Content5,
    divider = BrandColors.Divider,
    default400 = BrandColors.Default400,
    default700 = BrandColors.Default700,
    mutedForeground = BrandColors.MutedForeground,
    ring = BrandColors.Ring,
)

val DarkExtraColors = LightExtraColors.copy(
    secondaryBackground = BrandColors.DarkSurface,
    content1 = BrandColors.DarkSurface,
    content2 = BrandColors.DarkSurface,
    content3 = Color(0xFF3F3F46),
    content4 = Color(0xFF52525B),
    divider = Color(0xFF3F3F46),
    mutedForeground = Color(0xFFA1A1AA),
)

val LocalAmoozimColors = staticCompositionLocalOf { LightExtraColors }
