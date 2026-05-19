---
name: kmp-strings
description: Extract hardcoded user-facing strings in a KMP module to its composeResources/values/strings.xml using Res.string.* keys. Use when audit finds raw strings in code or when adding i18n.
argument-hint: [modulePath]
allowed-tools: Bash(rg *), Bash(grep *), Bash(./gradlew *)
---

# KMP Strings Extraction

Move hardcoded user-facing strings from `$ARGUMENTS` (e.g., `feature/home/impl`) into the module's per-module Compose Resources, keyed via `Res.string.*`.

AGENTS.md is the source of truth: **no hardcoded user-facing strings in code**, every string goes through a `Res.string.*` key, and each module owns its own resources under `<module>/src/commonMain/composeResources/`. Never import another module's `Res`.

## 1. Audit the module

Find candidates — quoted literals passed to user-visible composables:

```bash
rg -n --type kotlin --no-heading \
  -e 'Text\(\s*"' \
  -e 'TopAppBar\(.*title\s*=\s*\{\s*Text\(\s*"' \
  -e 'contentDescription\s*=\s*"' \
  -e 'placeholder\s*=\s*\{\s*Text\(\s*"' \
  -e 'label\s*=\s*\{\s*Text\(\s*"' \
  $ARGUMENTS/src/commonMain/kotlin
```

Filter out:

- Test-only / preview-only files (`@Preview` blocks may use literal strings).
- Logging strings passed to Kermit — those are developer-facing, not user-facing.
- Format strings already wrapped in `Res.string.*`.

## 2. Create the resource directory if missing

```
$ARGUMENTS/
└─ src/commonMain/composeResources/
   ├─ values/strings.xml            ← English (default)
   ├─ values-ar/strings.xml         ← RTL — required if shipping Arabic
   ├─ values-fr/strings.xml         ← additional locales
   └─ drawable/                     ← module-local drawables
```

Use hyphenated qualifier folders (`values-ar`, `values-fr`, `drawable-dark`, `drawable-xxhdpi`, `font-sw600dp`) — not the legacy Android `values-rAR` notation.

## 3. Author `strings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="home_title">Home</string>
    <string name="home_empty_label">Nothing here yet</string>
    <string name="home_add_button">Add item</string>

    <!-- Plurals: use Res.plurals.* not Res.string.* -->
    <plurals name="item_count">
        <item quantity="one">%d item</item>
        <item quantity="other">%d items</item>
    </plurals>
</resources>
```

Key naming: `<screen>_<role>` (e.g., `home_title`, `detail_save_button`). Avoid encoding the literal value into the key (`home_save_button` not `home_save_text`).

## 4. Wire keys into Kotlin

The generated `Res` class lives in each module's package (per `shared/build.gradle.kts` and feature build files: `packageOfResClass = "com.po4yka.app.resources"` for `:shared`, similar for features). Imports:

```kotlin
import <module-package>.generated.resources.Res
import <module-package>.generated.resources.home_title

Text(text = stringResource(Res.string.home_title))
```

For plurals:

```kotlin
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.pluralStringResource

Text(pluralStringResource(Res.plurals.item_count, count, count))
```

## 5. Cross-module rule

If two modules need the same string, **copy it** or **promote it to `:shared/src/commonMain/composeResources/`**. Never `import othermod.generated.resources.Res` — Compose Resources are per-module by design.

App-global strings (legal copy, app name, brand strings) belong in `:shared`. Feature-specific strings belong in the feature module.

## 6. Verify

```bash
./gradlew detekt
./gradlew $ARGUMENTS:compileDebugKotlinAndroid   # catches missing Res references
./gradlew androidApp:assembleDebug
```

If you added a new locale (`values-ar`), spot-check RTL by previewing the screen with `LocalLayoutDirection.current = LayoutDirection.Rtl` — directional UI (icons, swipes) must mirror correctly per AGENTS.md.

## Reporting

After extraction, report:
- Count of strings extracted
- New `strings.xml` files
- Files modified
- Any literals deliberately left in (test/preview/log)
