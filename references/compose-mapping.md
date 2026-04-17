# Industrial Design System — Compose Multiplatform Mapping

How the `industrial-design-cmp` library integrates into a consumer Compose Multiplatform project.

## 1. DEPENDENCY

Add JitPack to your repositories and depend on the library in `commonMain`:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

// composeApp/build.gradle.kts
commonMain.dependencies {
    implementation("com.github.po4yka.industrial-design-cmp:library:0.1.0")
}
```

That single dependency pulls in:
- `IndustrialTheme` composable
- `IndustrialDarkColorScheme`, `IndustrialLightColorScheme`
- `IndustrialTypography()` (composable, returns `Typography`)
- `IndustrialTokens` (spacing, accent, motion, radius)
- Bundled fonts: Space Grotesk, Space Mono, Doto (as Compose resources)

---

## 2. WRAPPING YOUR UI

Call `IndustrialTheme` at the root of your Compose hierarchy. It sets the `MaterialTheme` with the industrial `ColorScheme` + `Typography`, and picks dark/light automatically via `isSystemInDarkTheme()`.

```kotlin
import com.po4yka.industrialdesign.IndustrialTheme

@Composable
fun App() {
    IndustrialTheme {
        // Your screens here
    }
}
```

To force a specific mode:

```kotlin
IndustrialTheme(darkTheme = true) { /* … */ }
```

---

## 3. MATERIAL 3 COLORSCHEME SLOT MAPPING

Design tokens → M3 slots (for reference when you want to customize or extend):

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

Spacing, motion, status colors, and shape radii aren't M3 concepts. They live on `IndustrialTokens`:

```kotlin
import com.po4yka.industrialdesign.IndustrialTokens

Modifier.padding(IndustrialTokens.Spacing.md)                     // 16.dp
Modifier.padding(horizontal = IndustrialTokens.Spacing.xl)        // 32.dp
val successColor = IndustrialTokens.Accent.Success                 // good-state green
RoundedCornerShape(IndustrialTokens.Radius.Pill)                   // 999.dp
tween(IndustrialTokens.Motion.MicroDurationMillis, easing = IndustrialTokens.Motion.Easing)
```

Always prefer `IndustrialTokens.Spacing.*` over raw `.dp` literals — swapping to a denser layout becomes a single-point change.

---

## 5. DARK / LIGHT SWITCHING

`IndustrialTheme` picks the scheme based on `isSystemInDarkTheme()`. To override, pass `darkTheme = true/false`. Both schemes are first-class — design and verify each independently.

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

## 7. ADVANCED: CUSTOM FONT FAMILIES

If you need to compose your own `Typography` variant (say, adjust `displayLarge` to not use Doto), reach into the library's `Res.font.*`:

```kotlin
import com.po4yka.industrialdesign.resources.Res
import com.po4yka.industrialdesign.resources.space_grotesk

@Composable
fun CustomTypography(): Typography {
    val body = FontFamily(Font(Res.font.space_grotesk, FontWeight.Normal))
    // Build your own Typography with the library's fonts…
}
```

The library sets `publicResClass = true` so consumers can access bundled font resources directly.

---

## 8. VERIFYING

After adding the dependency, run your project's verification pipeline. For a KMP project:

```
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```

Stop on the first failure.
