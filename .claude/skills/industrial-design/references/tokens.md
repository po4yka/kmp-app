# Industrial Design System — Tokens

All values in Compose primitives. The `industrial-design-cmp` library already wires these into `IndustrialTheme`; this document explains the values so designers can apply them intentionally at call sites.

## 1. TYPOGRAPHY

### Font Stack

| Role | Font | Fallback | Weight |
|------|------|----------|--------|
| **Display** | `Doto` | `Space Mono`, monospace | 400–700, variable dot-size |
| **Body / UI** | `Space Grotesk` | `DM Sans`, system-ui, sans-serif | Light 300, Regular 400, Medium 500, Bold 700 |
| **Data / Labels** | `Space Mono` | `JetBrains Mono`, `SF Mono`, monospace | Regular 400, Bold 700 |

**Why these fonts:** Doto is a variable dot-matrix typeface — instrument-panel aesthetic. Space Grotesk + Space Mono share the same DNA (geometric sans + monospaced companion), giving a coherent typographic voice. All three are bundled by the library — no network fetch at runtime.

### Type Scale

| Material 3 slot | Size (sp) | Line Height | Letter Spacing (em) | Use |
|-----------------|:--------:|:-----------:|:-------------------:|-----|
| `displayLarge` | 72 | 72 | -0.03 | Hero numbers, time displays (Doto) |
| `displayMedium` | 48 | 50 | -0.02 | Section heroes, percentages |
| `displaySmall` | 36 | 40 | -0.02 | Page titles |
| `headlineLarge` | 24 | 29 | -0.01 | Section headings |
| `headlineMedium` | 20 | 24 | 0 | Subsections |
| `headlineSmall` / `titleLarge` | 18 | 23 | 0 | Subsections |
| `titleMedium` | 16 | 24 | 0 | Card titles |
| `bodyLarge` | 16 | 24 | 0 | Body text |
| `bodyMedium` | 14 | 21 | 0.01 | Secondary body |
| `bodySmall` | 12 | 17 | 0.04 | Timestamps, footnotes (Space Mono) |
| `labelLarge` | 13 | 16 | 0.06 | Button labels (Space Mono) |
| `labelMedium` | 12 | 14 | 0.08 | ALL CAPS labels (Space Mono) |
| `labelSmall` | 11 | 13 | 0.08 | Instrument-panel labels (Space Mono) |

### Typographic Rules

- **Doto:** 36sp+ only, tight tracking, never for body text
- **Labels:** Always Space Mono, ALL CAPS (`.uppercase()` at call site), 0.06–0.1em spacing, 11–13sp
- **Data/Numbers:** Always Space Mono. Units as `labelMedium` size, slightly raised, adjacent
- **Hierarchy:** display (Doto) > heading (Space Grotesk) > label (Space Mono caps) > body (Space Grotesk). Four levels max.

---

## 2. COLOR SYSTEM

### Primary Palette (Dark Mode)

| Token | Hex | Contrast on #000 | Role | M3 slot |
|-------|-----|:----------------:|------|---------|
| `Black` | `#000000` | — | Primary background (OLED) | `background` |
| `Surface` | `#111111` | 1.3:1 | Elevated surfaces, cards | `surface` |
| `SurfaceRaised` | `#1A1A1A` | 1.5:1 | Secondary elevation | `surfaceVariant` |
| `Border` | `#222222` | — | Subtle dividers (decorative only) | `outlineVariant` |
| `BorderVisible` | `#333333` | — | Intentional borders, wireframe lines | `outline` |
| `TextDisabled` | `#666666` | 4.0:1 | Disabled text, decorative elements | use `onSurface.copy(alpha = 0.4f)` |
| `TextSecondary` | `#999999` | 6.3:1 | Labels, captions, metadata | `onSurfaceVariant` |
| `TextPrimary` | `#E8E8E8` | 16.5:1 | Body text | `onSurface` |
| `TextDisplay` | `#FFFFFF` | 21:1 | Headlines, hero numbers | `primary` |

### Accent & Status Colors (from `IndustrialTokens.Accent`)

| Token | Hex | Usage |
|-------|-----|-------|
| `Signal` | `#D71921` | Signal-light red: active states, destructive, urgent. One per screen as UI element. Never decorative. Also bound to Material 3 `error` and `tertiary`. |
| `SignalSubtle` | `#D71921` @ 0.15 alpha | Tint backgrounds |
| `Success` | `#4A9E5C` | Confirmed, completed, connected |
| `Warning` | `#D4A843` | Caution, pending, degraded |
| `Interactive` | `#5B9BF6` (dark) / `#007AFF` (light) | Tappable text: links, picker values. Not for buttons. |

**Data status colors:** `Success` = good/in range, `Warning` = moderate/attention, `Signal` = bad/over limit, `onSurface` = neutral. Apply color to **value**, not label or background. Labels stay `onSurfaceVariant`. Trend arrows inherit value color.

### Dark / Light Mode

| Token | Dark | Light |
|-------|------|-------|
| `background` | `#000000` | `#F5F5F5` |
| `surface` | `#111111` | `#FFFFFF` |
| `surfaceVariant` | `#1A1A1A` | `#F0F0F0` |
| `outlineVariant` | `#222222` | `#E8E8E8` |
| `outline` | `#333333` | `#CCCCCC` |
| `onSurfaceVariant` | `#999999` | `#666666` |
| `onSurface` | `#E8E8E8` | `#1A1A1A` |
| `primary` | `#FFFFFF` | `#000000` |
| `Accent.Interactive` | `#5B9BF6` | `#007AFF` |

**Identical across modes:** Signal red (`#D71921`), status colors, ALL CAPS labels, fonts, type scale, spacing, component shapes.

**Dark feel:** Instrument panel in a dark room. OLED black, white data glowing.
**Light feel:** Printed technical manual. Off-white paper (#F5F5F5), black ink. Cards = `#FFFFFF` on off-white page = subtle elevation without shadows.

---

## 3. SPACING

From `IndustrialTokens.Spacing`:

| Token | Value (Dp) | Use |
|-------|:----------:|-----|
| `xs2` | 2 | Optical adjustments only |
| `xs` | 4 | Icon-to-label gaps, tight padding |
| `sm` | 8 | Component internal spacing |
| `md` | 16 | Standard padding, element gaps |
| `lg` | 24 | Group separation |
| `xl` | 32 | Section margins |
| `xl2` | 48 | Major section breaks |
| `xl3` | 64 | Page-level vertical rhythm |
| `xl4` | 96 | Hero breathing room |

---

## 4. MOTION & INTERACTION

From `IndustrialTokens.Motion`:

- **Duration:** `MicroDurationMillis` (200ms) for micro, `TransitionDurationMillis` (300ms) for transitions
- **Easing:** `IndustrialTokens.Motion.Easing` — subtle ease-out (`cubic-bezier(0.25, 0.1, 0.25, 1)`). No spring/bounce.
- Prefer opacity over position. Elements fade, don't slide.
- Hover: border/text brightens. No scale, no shadows.
- No parallax, scroll-jacking, gratuitous animation.

---

## 5. SHAPE / RADIUS

From `IndustrialTokens.Radius`:

| Token | Value (Dp) | Use |
|-------|:----------:|-----|
| `Technical` | 4 | Technical/data components |
| `Compact` | 8 | Inputs, compact cards |
| `Card` | 16 | Standard cards, widgets |
| `Pill` | 999 | Primary/secondary buttons, toggles |

---

## 6. ICONOGRAPHY

- Monoline, 1.5dp stroke, no fill. 24x24 base, 20x20 live area. Round caps/joins.
- Color inherits text color (`LocalContentColor`). Max 5–6 strokes.
- Preferred: Lucide or Phosphor (thin variants). Never filled or multi-color.
- Material Symbols "Outlined" works; avoid "Filled" and "Rounded" variants.

---

## 7. DOT-MATRIX MOTIF

**When to use:** Hero typography (Doto), decorative grid backgrounds, dot-grid data viz, loading indicators, empty state illustrations.

### Compose Implementation (background)

```kotlin
Modifier.drawBehind {
    val dotRadius = 1.dp.toPx()
    val step = 16.dp.toPx()
    val color = Color(0xFF333333) // outline-visible
    var y = 0f
    while (y < size.height) {
        var x = 0f
        while (x < size.width) {
            drawCircle(color = color, radius = dotRadius, center = Offset(x, y))
            x += step
        }
        y += step
    }
}
```

Dots 1–2dp, uniform 12–16dp grid. Alpha 0.1–0.2 for backgrounds, full opacity for data. Never as container border or button style.
