---
name: industrial-design
description: Industrial-monochrome design system for Compose Multiplatform — Swiss typography, OLED blacks, segmented progress bars, instrument-style widgets. Use when the user asks for "industrial design", "monochrome UI", "instrument style", "/industrial-design", or wants to apply this library's design language. Do NOT auto-trigger for generic UI tasks.
version: 1.0.0
user-invocable: true
allowed-tools: [Read, Write, Edit, Glob, Grep]
---

# Industrial-Monochrome Design System

A senior product designer's toolkit trained in Swiss typography, industrial design (Braun, Teenage Engineering), and modern interface craft. Monochromatic, typographically driven, information-dense without clutter. Dark and light mode with equal rigor.

This skill ships alongside the `industrial-design-cmp` Compose Multiplatform library. Consumers depend on the library for the theme code; this skill teaches the craft rules and component patterns. When a consumer repo has the library on its classpath, `IndustrialTheme`, `IndustrialDarkColorScheme`, `IndustrialLightColorScheme`, `IndustrialTypography()`, and `IndustrialTokens` are already wired and importable from `com.po4yka.industrialdesign`.

---

## 1. DESIGN PHILOSOPHY

- **Subtract, don't add.** Every element must earn its pixel. Default to removal.
- **Structure is ornament.** Expose the grid, the data, the hierarchy itself.
- **Monochrome is the canvas.** Color is an event, not a default — except when encoding data status (see Section 3).
- **Type does the heavy lifting.** Scale, weight, and spacing create hierarchy — not color, not icons, not borders.
- **Both modes are first-class.** Dark mode: OLED black. Light mode: warm off-white. Neither is "derived" — both get full design attention. Ask the user which mode to start with.
- **Industrial warmth.** Technical and precise, but never cold. A human hand should be felt.

---

## 2. CRAFT RULES — HOW TO COMPOSE

### 2.1 Visual Hierarchy: The Three-Layer Rule

Every screen has exactly **three layers of importance.** Not two, not five. Three.

| Layer | What | How |
|-------|------|-----|
| **Primary** | The ONE thing the user sees first. A number, a headline, a state. | Doto or Space Grotesk at display size. `displayLarge`/`displayMedium`. 48–96dp breathing room. |
| **Secondary** | Supporting context. Labels, descriptions, related data. | Space Grotesk at body/subheading. `onSurface`. Grouped tight (8–16dp) to the primary. |
| **Tertiary** | Metadata, navigation, system info. Visible but never competing. | Space Mono at caption/label. `onSurfaceVariant` or disabled alpha. ALL CAPS. Pushed to edges or bottom. |

**The test:** Squint at the screen. Can you still tell what's most important? If two things compete, one needs to shrink, fade, or move.

**Common mistake:** Making everything "secondary." Evenly-sized elements with even spacing = visual flatness. Be brave — make the primary absurdly large and the tertiary absurdly small. The contrast IS the hierarchy.

### 2.2 Font Discipline

Per screen, use maximum:
- **2 font families** (Space Grotesk + Space Mono. Doto only for hero moments.)
- **3 font sizes** (one large, one medium, one small)
- **2 font weights** (Regular + one other — usually Light or Medium, rarely Bold)

Think of it as a budget. Every additional size/weight costs visual coherence. Before adding a new size, ask: can I create this distinction with spacing or color instead?

| Decision | Size | Weight | Color |
|----------|:---:|:---:|:---:|
| Heading vs. body | Yes | No | No |
| Label vs. value | No | No | Yes |
| Active vs. inactive nav | No | No | Yes |
| Hero number vs. unit | Yes | No | No |
| Section title vs. content | Yes | Optional | No |

**Rule of thumb:** If reaching for a new font-size, it's probably a spacing problem. Add distance instead.

### 2.3 Spacing as Meaning

Spacing is the primary tool for communicating relationships. Use `IndustrialTokens.Spacing` from the library.

```
Tight (xs/sm, 4–8dp)   = "These belong together" (icon + label, number + unit)
Medium (md, 16dp)       = "Same group, different items" (list items, form fields)
Wide (lg/xl, 24–32dp)   = "New group starts here" (section breaks)
Vast (xl3/xl4, 64–96dp) = "This is a new context" (hero to content, major divisions)
```

**If a divider line is needed, the spacing is probably wrong.** Dividers are a symptom of insufficient spacing contrast. Use them only in data-dense lists where items are structurally identical.

### 2.4 Container Strategy (prefer top)

1. **Spacing alone** (proximity groups items)
2. A single divider line (`HorizontalDivider`)
3. A subtle border outline (`Modifier.border(1.dp, outlineVariant)`)
4. A surface card with background change (`surfaceVariant`)

Each step down adds visual weight. Use the lightest tool that works. Never box the most important element — let it float on the background.

### 2.5 Color as Hierarchy

In a monochrome system, the gray scale IS the hierarchy. Max 4 levels per screen. Material 3 slots map as follows:

```
primary         → Hero numbers, primary content (100% white / 100% black)
onSurface       → Body text (~90%)
onSurfaceVariant → Labels, captions, metadata (~60%)
onSurface.copy(alpha = 0.4f) → Disabled, timestamps, hints
```

**The signal-light red (`IndustrialTokens.Accent.Signal`, `#D71921`) is not part of the hierarchy.** It's an interrupt — "look HERE, NOW." If nothing is urgent, no red on the screen.

**Data status colors** (`Accent.Success`, `Accent.Warning`, `Accent.Signal`) are exempt from the "one accent" rule when encoding data values. Apply color to the **value itself**, not labels or row backgrounds. See `references/tokens.md` for the full color system.

### 2.6 Consistency vs. Variance

**Be consistent in:** Font families, label treatment (always Space Mono ALL CAPS), spacing rhythm, color roles, component shapes, alignment.

**Break the pattern in exactly ONE place per screen:** An oversized number, a circular widget among rectangles, a red accent among grays, a Doto headline, a vast gap where everything else is tight.

This single break IS the design. Without it: sterile grid. With more than one: visual chaos.

### 2.7 Compositional Balance

**Asymmetry > symmetry.** Centered layouts feel generic. Favor deliberately unbalanced composition:
- **Large left, small right:** Hero metric + metadata stack.
- **Top-heavy:** Big headline near top, sparse content below.
- **Edge-anchored:** Important elements pinned to screen edges, negative space in center.

Balance heavy elements with more empty space, not with more heavy elements.

### 2.8 The Industrial Vibe

1. **Confidence through emptiness.** Large uninterrupted background areas. Resist filling space.
2. **Precision in the small things.** Letter-spacing, exact gray values, 4dp gaps. Micro-decisions compound into craft.
3. **Data as beauty.** `36GB/s` in Space Mono at 48sp IS the visual. No illustrations needed.
4. **Mechanical honesty.** Controls look like controls. A toggle = physical switch. A gauge = instrument.
5. **One moment of surprise.** A dot-matrix headline. A circular widget. A red dot. Restraint makes the one expressive moment powerful.
6. **Percussive, not fluid.** Imagine UI sounds: click not swoosh, tick not chime. Design transitions that feel mechanical and precise. Use `IndustrialTokens.Motion.Easing` (subtle ease-out).

### 2.9 Visual Variety in Data-Dense Screens

When 3+ data sections appear on one screen, vary the visual form:

| Form | Best for | Weight |
|------|----------|--------|
| Hero number (large Doto/Space Mono) | Single key metric | Heavy — use once |
| Segmented progress bar | Progress toward goal | Medium |
| Concentric rings / arcs | Multiple related percentages | Medium |
| Inline compact bar | Secondary metrics in rows | Light |
| Number-only with status color | Values without proportion | Lightest |
| Sparkline | Trends over time | Medium |
| Stat row (label + value) | Simple data points | Light |

Lead section → heaviest treatment. Secondary → different form. Tertiary → lightest. The FORM varies, the VOICE stays the same.

---

## 3. ANTI-PATTERNS — WHAT TO NEVER DO

- No gradients in UI chrome
- No shadows. No blur. Flat surfaces, border separation.
- No skeleton loading screens. Use `[LOADING...]` text or segmented spinner.
- No toast popups. Use inline status text: `[SAVED]`, `[ERROR: ...]`
- No sad-face illustrations, cute mascots, or multi-paragraph empty states
- No zebra striping in tables
- No filled icons, multi-color icons, or emoji as UI
- No parallax, scroll-jacking, or gratuitous animation
- No spring/bounce easing. Use `IndustrialTokens.Motion.Easing` only.
- No `RoundedCornerShape` > 16dp on cards. Buttons are pill (`IndustrialTokens.Radius.Pill`) or technical (`Radius.Technical`, 4dp).
- Data visualization: differentiate with **opacity** (1.0f/0.6f/0.3f) or **pattern** (solid/striped/dotted) before introducing color.

---

## 4. WORKFLOW

1. **Confirm the library is on classpath** — verify `com.github.po4yka.industrial-design-cmp:library:*` is declared in `commonMain.dependencies` and that `IndustrialTheme` imports from `com.po4yka.industrialdesign`.
2. **Ask mode** — dark or light? Neither is default.
3. **Sketch hierarchy** — identify the 3 layers before writing any code.
4. **Compose** — apply craft rules (Sections 2.1–2.9).
5. **Check tokens** — consult `references/tokens.md` for exact values.
6. **Build components** — consult `references/components.md` for Compose patterns.
7. **Integrate with the theme** — consult `references/compose-mapping.md` for Material 3 binding and `IndustrialTokens` usage.

---

## 5. REFERENCE FILES

- **`references/tokens.md`** — Fonts, type scale, color system (dark + light), spacing scale, grid, motion, iconography, dot-matrix motif. All values in Compose primitives (`Color`, `Dp`, `sp`, `em`).
- **`references/components.md`** — Cards, buttons, inputs, lists, tables, nav, tags, segmented controls, progress bars, charts, widgets, overlays, state patterns. Each with a Compose snippet.
- **`references/compose-mapping.md`** — Compose Multiplatform integration: library dependency, Material 3 `ColorScheme` slot mapping, `IndustrialTokens` usage for values outside M3, dark/light switching.
