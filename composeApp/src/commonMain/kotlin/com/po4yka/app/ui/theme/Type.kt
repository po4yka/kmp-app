package com.po4yka.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.po4yka.app.resources.Res
import com.po4yka.app.resources.doto
import com.po4yka.app.resources.space_grotesk
import com.po4yka.app.resources.space_mono_bold
import com.po4yka.app.resources.space_mono_regular
import org.jetbrains.compose.resources.Font

// Industrial typography: Doto (display), Space Grotesk (body/heading), Space Mono (labels/data).
// space_grotesk.ttf and doto.ttf are variable fonts — weight axis resolves FontWeight at render time.

private fun style(
    family: FontFamily,
    size: Int,
    lineHeight: Int,
    letterSpacingEm: Double = 0.0,
    weight: FontWeight = FontWeight.Normal,
): TextStyle = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacingEm.em,
)

@Composable
fun IndustrialTypography(): Typography {
    val display = FontFamily(Font(Res.font.doto, FontWeight.Normal))
    val body = FontFamily(
        Font(Res.font.space_grotesk, FontWeight.Light),
        Font(Res.font.space_grotesk, FontWeight.Normal),
        Font(Res.font.space_grotesk, FontWeight.Medium),
    )
    val mono = FontFamily(
        Font(Res.font.space_mono_regular, FontWeight.Normal),
        Font(Res.font.space_mono_bold, FontWeight.Bold),
    )

    return Typography(
        displayLarge = style(display, 72, 72, -0.03),
        displayMedium = style(body, 48, 50, -0.02, FontWeight.Light),
        displaySmall = style(body, 36, 40, -0.02, FontWeight.Light),
        headlineLarge = style(body, 24, 29, -0.01, FontWeight.Medium),
        headlineMedium = style(body, 20, 24, weight = FontWeight.Medium),
        headlineSmall = style(body, 18, 23, weight = FontWeight.Medium),
        titleLarge = style(body, 18, 24, weight = FontWeight.Medium),
        titleMedium = style(body, 16, 24, weight = FontWeight.Medium),
        titleSmall = style(body, 14, 20, weight = FontWeight.Medium),
        bodyLarge = style(body, 16, 24),
        bodyMedium = style(body, 14, 21, 0.01),
        bodySmall = style(mono, 12, 17, 0.04),
        labelLarge = style(mono, 13, 16, 0.06),
        labelMedium = style(mono, 12, 14, 0.08),
        labelSmall = style(mono, 11, 13, 0.08),
    )
}
