package com.po4yka.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Design tokens that Material 3 ColorScheme / Typography don't cover.
// Accent signal, status colors, spacing scale, motion easing, dot-matrix motif.

object IndustrialTokens {
    object Spacing {
        val xs2: Dp = 2.dp
        val xs: Dp = 4.dp
        val sm: Dp = 8.dp
        val md: Dp = 16.dp
        val lg: Dp = 24.dp
        val xl: Dp = 32.dp
        val xl2: Dp = 48.dp
        val xl3: Dp = 64.dp
        val xl4: Dp = 96.dp
    }

    // Status & accent colors not in the ColorScheme slots.
    object Accent {
        val Signal: Color = Color(0xFFD71921)
        val SignalSubtle: Color = Color(0x26D71921) // alpha 0x26 ≈ 0.15
        val Success: Color = Color(0xFF4A9E5C)
        val Warning: Color = Color(0xFFD4A843)
        val Interactive: Color = Color(0xFF5B9BF6)
        val InteractiveLight: Color = Color(0xFF007AFF)
    }

    object Motion {
        const val MicroDurationMillis: Int = 200
        const val TransitionDurationMillis: Int = 300

        // Subtle ease-out. No spring, no bounce.
        val Easing: Easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
    }

    // Component shape radii.
    object Radius {
        val Technical: Dp = 4.dp
        val Compact: Dp = 8.dp
        val Card: Dp = 16.dp
        val Pill: Dp = 999.dp
    }
}
