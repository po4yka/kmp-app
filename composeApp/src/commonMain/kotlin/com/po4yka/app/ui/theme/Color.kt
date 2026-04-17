package com.po4yka.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Industrial-monochrome palette. Token names follow the design system;
// values are bound to Material 3 ColorScheme slots below.
// Accents and status colors not covered by Material 3 live in Tokens.kt.

// Dark mode — OLED black with white data "glowing".
private val Black = Color(0xFF000000)
private val Surface = Color(0xFF111111)
private val SurfaceRaised = Color(0xFF1A1A1A)
private val Border = Color(0xFF222222)
private val BorderVisible = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextDisplay = Color(0xFFFFFFFF)

// Light mode — off-white paper with black ink.
// Disabled text has no M3 slot; callers apply `onSurface.copy(alpha = 0.4f)` at use site.
private val LightBackground = Color(0xFFF5F5F5)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceRaised = Color(0xFFF0F0F0)
private val LightBorder = Color(0xFFE8E8E8)
private val LightBorderVisible = Color(0xFFCCCCCC)
private val LightTextSecondary = Color(0xFF666666)
private val LightTextPrimary = Color(0xFF1A1A1A)
private val LightTextDisplay = Color(0xFF000000)

// Accent — signal-light red. One per screen as UI element.
private val Accent = Color(0xFFD71921)

val DarkColorScheme = darkColorScheme(
    background = Black,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextSecondary,
    primary = TextDisplay,
    onPrimary = Black,
    secondary = TextPrimary,
    onSecondary = Black,
    tertiary = Accent,
    onTertiary = TextDisplay,
    error = Accent,
    onError = TextDisplay,
    outline = BorderVisible,
    outlineVariant = Border,
    scrim = Black,
)

val LightColorScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceRaised,
    onSurfaceVariant = LightTextSecondary,
    primary = LightTextDisplay,
    onPrimary = LightSurface,
    secondary = LightTextPrimary,
    onSecondary = LightSurface,
    tertiary = Accent,
    onTertiary = LightSurface,
    error = Accent,
    onError = LightSurface,
    outline = LightBorderVisible,
    outlineVariant = LightBorder,
    scrim = LightTextDisplay,
)
