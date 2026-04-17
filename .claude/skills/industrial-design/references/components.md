# Industrial Design System — Components

Each component references Material 3 composables where possible; custom composables only when M3 doesn't fit the aesthetic (segmented progress bar, stat row, instrument gauge). All snippets assume the consumer has `IndustrialTheme { … }` wrapping the content and has imported `IndustrialTokens` from `com.po4yka.industrialdesign`.

## 1. CARDS / SURFACES

- Background: `MaterialTheme.colorScheme.surface` or `surfaceVariant`
- Border: `1.dp` border in `outlineVariant`, or none
- Radius: `IndustrialTokens.Radius.Card` (16dp), `Compact` (8dp), or `Technical` (4dp)
- Padding: 16–24dp. No shadows. Use `Card` with `elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)`.

```kotlin
Card(
    shape = RoundedCornerShape(IndustrialTokens.Radius.Card),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
) { /* … */ }
```

---

## 2. BUTTONS

| Variant | Background | Border | Text | Radius |
|---------|-----------|--------|------|--------|
| Primary | `primary` | none | `onPrimary` | `Pill` |
| Secondary | transparent | `1.dp` `outline` | `onSurface` | `Pill` |
| Ghost | transparent | none | `onSurfaceVariant` | `Technical` |
| Destructive | transparent | `1.dp` `error` | `error` | `Pill` |

All buttons: `labelLarge` style (Space Mono), ALL CAPS, letter-spacing 0.06em, `PaddingValues(horizontal = 24.dp, vertical = 12.dp)`. Min height 44dp.

```kotlin
Button(
    onClick = onClick,
    shape = RoundedCornerShape(IndustrialTokens.Radius.Pill),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
) {
    Text("SUBMIT", style = MaterialTheme.typography.labelLarge)
}
```

---

## 3. INPUTS

- Underline preferred (`OutlinedTextField` with custom colors) or full border at `Radius.Compact` (8dp)
- Label above input: `labelMedium` style, ALL CAPS, `onSurfaceVariant`
- Focus: border → `onSurface`. Error: border → `error`, message below in `error`
- Data-entry fields use Space Mono — override `textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = monoFamily)`

---

## 4. LISTS / DATA ROWS

- Dividers: `HorizontalDivider(thickness = 1.dp, color = outlineVariant)`, full-width
- Row padding: 12–16dp vertical
- Left: label (Space Mono caps, `onSurfaceVariant`). Right: value (`onSurface`)
- Never alternating row backgrounds. Use dividers.

**Stat rows:** Label left (Space Mono caps, `onSurfaceVariant`), value right (color = status color), unit adjacent in `labelMedium`. Trend arrow same color as value.

**Hierarchical rows:** Sub-items indented 16–24dp, same divider treatment. No tree lines or expand/collapse — indentation IS the hierarchy.

```kotlin
@Composable
fun StatRow(label: String, value: String, status: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = status)
    }
}
```

---

## 5. TABLES / DATA GRIDS

- Header: `labelSmall` style, bottom border `outline`
- Cell text: Space Mono for numbers, Space Grotesk for text. Cell padding 12dp × 16dp.
- Numbers right-aligned, text left. No zebra striping, no cell backgrounds.
- Active row: `surfaceVariant` background, left 2dp `tertiary` (signal) indicator

---

## 6. NAVIGATION

- Bottom bar mobile (`NavigationBar`), horizontal text bar desktop
- Labels: Space Mono, ALL CAPS. Active: `primary` + dot/underline. Inactive: disabled alpha.
- Bracket `[ HOME ]  GALLERY  INFO` or pipe `HOME | GALLERY | INFO` styles both work.
- **Back button:** Circular 40–44dp, `surface` background, thin chevron `<`, top-left 16dp from edges.

---

## 7. TAGS / CHIPS

- Border: `1.dp` `outline`, no fill. Text: Space Mono, `labelSmall`, ALL CAPS
- Shape: `Pill` or `Technical`. Padding: 4dp × 12dp. Active: `primary` border+text.

---

## 8. SEGMENTED CONTROL

- Container: `1.dp` `outline`, pill or `Compact` shape
- Active: `primary` bg, `onPrimary` text (inverted). Inactive: transparent, `onSurfaceVariant`
- Text: Space Mono, ALL CAPS, `labelMedium`. Height 36–44dp. Transition 200ms ease-out (`IndustrialTokens.Motion.Easing`).
- Max 2–4 segments.

---

## 9. DATE / PERIOD NAVIGATION

- Layout: `< LABEL >` — back arrow, label, forward arrow
- Label: Space Mono/Grotesk, ALL CAPS. Arrows: thin chevrons, `onSurfaceVariant`, 44dp touch target.
- No calendar popovers — linear stepping IS the interaction.

---

## 10. TOGGLES / SWITCHES

- Pill track, circle thumb. Off: `outline` track, disabled thumb
- On: `primary` track, `onPrimary` thumb. Min touch target 44dp.
- Use `Switch` with custom `SwitchDefaults.colors(…)`.

---

## 11. SEGMENTED PROGRESS BARS

The signature data visualization. Discrete blocks — mechanical, instrument-like.

**Anatomy:** Label + value above; full-width bar of discrete rectangular segments with 2dp gaps below.

**Segments:** Square-ended blocks, no corner radius. Filled = solid status color. Empty = `outlineVariant` (dark) / `#E0E0E0` (light).

| State | Fill | When |
|-------|------|------|
| Neutral | `primary` | Within normal range |
| Over limit | `Accent.Signal` | Exceeds target |
| Good | `Accent.Success` | Healthy range |
| Moderate | `Accent.Warning` | Caution zone |

**Overflow:** Filled segments continue past "full" mark in status color (typically signal red).

**Sizes:** Hero 16–20dp, Standard 8–12dp, Compact 4–6dp height.

Always pair with numeric readout. Bar = proportion, number = precision.

```kotlin
@Composable
fun SegmentedProgressBar(
    progress: Float,           // 0.0..1.0+
    segmentCount: Int = 20,
    height: Dp = 12.dp,
    fill: Color = MaterialTheme.colorScheme.primary,
    empty: Color = MaterialTheme.colorScheme.outlineVariant,
    modifier: Modifier = Modifier,
) {
    val filled = (segmentCount * progress).toInt().coerceIn(0, segmentCount)
    Row(modifier = modifier.height(height), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(segmentCount) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (i < filled) fill else empty),
            )
        }
    }
}
```

---

## 12. OTHER DATA VISUALIZATION

- **Bar charts:** Vertical, `primary` fill, `outlineVariant` remainder. Square ends.
- **Gauges:** Thin-stroke `Canvas` circles + tick marks, numeric readout centered/adjacent.
- **Dot grids:** Vary opacity/size for heat maps. Uniform spacing.
- **Category differentiation:** Opacity → pattern → line style → color (last resort).
- Always show numeric value alongside any visual.

**Charts:** Line 1.5–2dp `primary`, average dashed 1dp `onSurfaceVariant`. Axis labels Space Mono, `bodySmall`. Grid `outlineVariant`, horizontal only. No area fill, no legend boxes — label lines directly with `Text`.

---

## 13. WIDGETS (DASHBOARD CARDS)

- `surface` background, 16dp radius. Hero metric: large Doto/Space Mono (`displayLarge` or `displayMedium`), left-aligned.
- Unit: `labelMedium` size, adjacent. Category: ALL CAPS Space Mono (`labelSmall`) top-left.
- Instrument gauges: compass, thermometer, dial motifs (render via `Canvas`).

---

## 14. OVERLAYS & LAYERING

No shadows. Layering through background contrast and borders.

- **Modals:** Backdrop `Color(0xCC000000)` (80% black), dialog `surface` + `1.dp outline` border + 16dp radius, centered max 480dp. Close: `[ X ]` top-right ghost button.
- **Bottom sheets:** `surface`, 2dp handle bar centered, 16dp top corner radius, drag-to-dismiss. Full-page: title centered + dismiss right; sections with `onSurfaceVariant` headings.
- **Dropdowns:** `surfaceVariant`, `1.dp outline` border, `Radius.Compact` (8dp), 44dp items. Selected: left 2dp `tertiary` indicator. No shadow (`elevation = 0.dp`).
- **Toasts:** None. Use inline status text: `[SAVED]`, `[ERROR: ...]`. Space Mono, `bodySmall`, near trigger.

---

## 15. STATE PATTERNS

- **Error:** Input border → `error` + message below. Form-level: summary box `1.dp error` border. Inline: `[ERROR]` prefix. Never red backgrounds or alert banners.
- **Empty:** Centered, 96dp+ padding. Headline `onSurfaceVariant`, 1 sentence description with disabled alpha. Optional dot-matrix illustration. No mascots.
- **Loading:** Segmented spinner (hardware-style) or segmented bar + percentage. No skeletons — use `[LOADING]` bracket text.
- **Disabled:** `Modifier.alpha(0.4f)` or color `.copy(alpha = 0.4f)`. Borders fade to `outlineVariant`.
