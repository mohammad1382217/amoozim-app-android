package com.amoozim.creator.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * App theme. Defaults to LIGHT and forces RTL, matching the web app (which is
 * Persian, RTL, and light-only). Pass `darkTheme = true` to opt into the derived dark
 * scheme. Brand tokens with no Material slot are exposed via [LocalAmoozimColors].
 */
@Composable
fun AmoozimTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) AmoozimDarkColorScheme else AmoozimLightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(
        LocalAmoozimColors provides extraColors,
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AmoozimTypography,
            shapes = AmoozimShapes,
            content = content,
        )
    }
}
