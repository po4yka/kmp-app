# Industrial Design System — Compose Multiplatform Mapping

How tokens land in this project's Compose theme. The wiring is already in place — this document explains where things live and how to extend them.

## 1. FONT BUNDLING

Fonts live at `composeApp/src/commonMain/composeResources/font/`:

```
space_grotesk.ttf         variable wght axis (Light 300 → Bold 700)
space_mono_regular.ttf    static 400
space_mono_bold.ttf       static 700
doto.ttf                  variable ROND + wght axes
OFL.txt                   SIL Open Font License attribution
```

Sourced from `google/fonts` on GitHub (all OFL-1.1 licensed). If any file is missing, refetch:

```bash
curl -sSfL -o composeApp/src/commonMain/composeResources/font/space_grotesk.ttf \
  'https://raw.githubusercontent.com/google/fonts/main/ofl/spacegrotesk/SpaceGrotesk%5Bwght%5D.ttf'
```

Resource package is set to `com.po4yka.app.resources` via `compose.resources { packageOfResClass = "com.po4yka.app.resources" }` in `composeApp/build.gradle.kts`. That gives:

```kotlin
import com.po4yka.app.resources.Res
import com.po4yka.app.resources.space_grotesk
import com.po4yka.app.resources.space_mono_regular
import com.po4yka.app.resources.space_mono_bold
import com.po4yka.app.resources.doto
import org.jetbrains.compose.resources.Font
```

---

## 2. TYPOGRAPHY

`Font()` is a composable in CMP (unlike Android), so `Typography` construction must also be composable. See `Type.kt`:

```kotlin
@Composable
fun IndustrialTypography(): Typography {
    val body = FontFamily(
        Font(Res.font.space_grotesk, FontWeight.Light),
        Font(Res.font.space_grotesk, FontWeight.Normal),
        Font(Res.font.space_grotesk, FontWeight.Medium),
    )
    // …
}
```

Space Grotesk and Doto are variable fonts — listing the same resource with different `FontWeight`s lets Compose pick the axis value per call site.

`Theme.kt` wires it into `MaterialTheme`:

```kotlin
MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    typography = IndustrialTypography(),
    content = content,
)
```

---

## 3. MATERIAL 3 COLORSCHEME SLOT MAPPING

Design tokens → M3 slots (see `Color.kt`):

| Design token | M3 slot (dark) | M3 slot (light) |
|--------------|----------------|-----------------|
| `TextDisplay` / `#FFFFFF` / `#000000` | `primary` | `primary` |
| `TextPrimary` / `#E8E8E8` / `#1A1A1A` | `onSurface`, `secondary` | `onSurface`, `secondary` |
| `TextSecondary` / `#999999` / `#666666` | `onSurfaceVariant` | `onSurfaceVariant` |
| `Surface` / `#111111` / `#FFFFFF` | `surface` | `surface` |
| `SurfaceRaised` / `#1A1A1A` / `#F0F0F0` | `surfaceVariant` | `surfaceVariant` |
| `Border` / `#222222` / `#E8E8E8` | `outlineVariant` | `outlineVariant` |
| `BorderVisible` / `#333333` / `#CCCCCC` | `outline` | `outline` |
| `Black` / `#000000` / `LightBackground` / `#F5F5F5` | `background` | `background` |
| `Accent.Signal` / `#D71921` | `tertiary`, `error` | `tertiary`, `error` |

**Disabled text** has no direct M3 slot. Use `onSurface.copy(alpha = 0.4f)` or `onSurfaceVariant.copy(alpha = 0.6f)` at the call site.

---

## 4. NON-M3 TOKENS VIA `IndustrialTokens`

Spacing, motion, status colors, and shape radii aren't M3 concepts. They live in `Tokens.kt`:

```kotlin
import com.po4yka.app.ui.theme.IndustrialTokens

Modifier.padding(IndustrialTokens.Spacing.md)                     // 16.dp
Modifier.padding(horizontal = IndustrialTokens.Spacing.xl)        // 32.dp
Color(IndustrialTokens.Accent.Success)                             // good-state green
RoundedCornerShape(IndustrialTokens.Radius.Pill)                   // 999.dp
tween(IndustrialTokens.Motion.MicroDurationMillis, easing = IndustrialTokens.Motion.Easing)
```

Always prefer `IndustrialTokens.Spacing.*` over raw `.dp` literals — swapping to a denser layout becomes a single-point change.

---

## 5. DARK / LIGHT SWITCHING

`Theme.kt` picks `DarkColorScheme` or `LightColorScheme` based on `isSystemInDarkTheme()`. To force a mode, wrap `AppTheme(darkTheme = true)` at a higher level. Both schemes are first-class — design and verify each independently.

**Avoid:** `dynamicDarkColorScheme` / `dynamicLightColorScheme` (Android 12+ wallpaper color). The monochrome palette is intentional — wallpaper-derived colors break the aesthetic.

---

## 6. COMPOSITIONAL PATTERN

Screen-level template:

```kotlin
@Composable
fun ExampleScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(IndustrialTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(IndustrialTokens.Spacing.xl),
    ) {
        // Tertiary: ALL CAPS Space Mono label, top-left
        Text(
            text = "SESSION STATUS".uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Primary: hero number in Doto
        Text(
            text = "36",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        // Secondary: supporting context
        Text(
            text = "active",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
```

This composition honors the three-layer rule (tertiary label → primary number → secondary descriptor), uses `IndustrialTokens.Spacing` for rhythm, and leans on M3 `colorScheme` for palette.

---

## 7. VERIFYING THE THEME CHANGES

After editing theme files, run the standard verification pipeline:

```
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```

Stop on the first failure. The `Res` class regenerates when you add/remove fonts — a clean rebuild may be needed if imports can't resolve: `./gradlew composeApp:clean composeApp:generateComposeResClass`.
