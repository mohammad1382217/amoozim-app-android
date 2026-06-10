package com.amoozim.creator.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The web app forces a single Persian font family (IRANYekanXFaNum) onto every
 * element. That font is proprietary and ships as `.woff2`, which Android font
 * resources do NOT support — so it is not bundled here.
 *
 * [AppFontFamily] defaults to the system family, which already renders Persian/Arabic
 * script correctly via the platform Noto fallback. For exact brand parity, drop
 * `.ttf`/`.otf` weights into `core/designsystem/src/main/res/font/` (e.g. Vazirmatn,
 * the closest free equivalent) and point this at a real [FontFamily]. See README → Fonts.
 */
val AppFontFamily: FontFamily = FontFamily.Default

/** Type scale mirroring the sizes that dominate the web app (Tailwind defaults; 1px ≈ 1sp). */
val AmoozimTypography: Typography = Typography().run {
    Typography(
        displayLarge = displayLarge.copy(fontFamily = AppFontFamily),
        displayMedium = displayMedium.copy(fontFamily = AppFontFamily),
        displaySmall = displaySmall.copy(fontFamily = AppFontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = AppFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = AppFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = AppFontFamily),
        titleLarge = titleLarge.copy(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = TextStyle(
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = titleSmall.copy(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
        bodyLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = AppFontFamily, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = AppFontFamily, fontSize = 12.sp, lineHeight = 16.sp),
        labelLarge = labelLarge.copy(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium),
        labelMedium = labelMedium.copy(fontFamily = AppFontFamily),
        labelSmall = TextStyle(fontFamily = AppFontFamily, fontSize = 10.sp, lineHeight = 14.sp),
    )
}
