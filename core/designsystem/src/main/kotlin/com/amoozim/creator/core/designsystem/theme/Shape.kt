package com.amoozim.creator.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Corner radii derived from the web's `--radius: 10px` token and its Tailwind ramp. */
val AmoozimShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp), // rounded-sm
    small = RoundedCornerShape(8.dp), // rounded-md (buttons, inputs)
    medium = RoundedCornerShape(10.dp), // rounded-lg (--radius)
    large = RoundedCornerShape(14.dp), // rounded-xl (cards, sheets)
    extraLarge = RoundedCornerShape(16.dp), // rounded-2xl
)
