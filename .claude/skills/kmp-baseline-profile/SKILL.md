---
name: kmp-baseline-profile
description: Generate Baseline Profile + Startup Profile via Macrobenchmark for the 3 user journeys in docs/performance.md, then commit baseline-prof.txt. Use when adding a perf-critical screen or before a release.
allowed-tools: Bash(./gradlew *), Bash(adb *), Bash(rg *)
---

# Baseline Profile Generation

Generate Baseline + Startup Profiles for the 3 user journeys called out in `docs/performance.md`:

1. Cold start → Home
2. Home → Detail
3. Add item

The profiles ship inside the AAB; AOT compilation at install-time uses them to skip JIT warm-up for hot paths. Generated artifacts land at `androidApp/src/main/baseline-prof.txt` and `androidApp/src/main/startup-prof.txt`.

## Prerequisites

This skill assumes a `:benchmarks` module exists. If not, scaffold it first (one-time):

### `:benchmarks` module (one-time setup)

```
benchmarks/
├─ build.gradle.kts
└─ src/main/
   ├─ AndroidManifest.xml
   └─ kotlin/com/po4yka/app/benchmarks/
      ├─ BaselineProfileGenerator.kt
      └─ JourneyBenchmark.kt
```

`benchmarks/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    id("androidx.baselineprofile")
}

android {
    namespace = "com.po4yka.app.benchmarks"
    compileSdk = 36
    defaultConfig {
        minSdk = 28
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    targetProjectPath = ":androidApp"
}

dependencies {
    implementation("androidx.benchmark:benchmark-macro-junit4:1.3.4")
    implementation("androidx.test.ext:junit:1.2.1")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")
}
```

Add the new module to `settings.gradle.kts`:

```kotlin
include(":benchmarks")
```

Apply the Baseline Profile consumer side to `:androidApp/build.gradle.kts`:

```kotlin
plugins {
    id("androidx.baselineprofile")
}

dependencies {
    "baselineProfile"(project(":benchmarks"))
}
```

Add the plugin to `gradle/libs.versions.toml`:

```toml
[plugins]
baselineprofile = { id = "androidx.baselineprofile", version = "1.3.4" }
```

## Authoring the generator

`benchmarks/src/main/kotlin/com/po4yka/app/benchmarks/BaselineProfileGenerator.kt`:

```kotlin
@OptIn(ExperimentalBaselineProfilesApi::class)
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = "com.po4yka.app",
            includeInStartupProfile = true,
        ) {
            // Journey 1: cold start
            startActivityAndWait()

            // Journey 2: Home → Detail
            device.findObject(By.text("Sample Item")).click()
            device.wait(Until.hasObject(By.text("Detail")), 3_000)
            device.pressBack()

            // Journey 3: Add item (placeholder — wire to actual UI once the add flow exists)
            // device.findObject(By.desc("Add")).click()
        }
    }
}
```

Keep the journeys aligned with `docs/performance.md`. If the UI text changes, this skill needs an update — the test relies on accessible text/descriptors.

## Generation

```bash
# Boot a managed device matching the AGP-generated config, or have one connected
adb devices

# Generate profiles (run on a release-like build for accuracy)
./gradlew :androidApp:generateBaselineProfile
```

Outputs:

- `androidApp/src/main/baseline-prof.txt` (AOT-compile hints)
- `androidApp/src/main/startup-prof.txt` (cold-start hints)

## Verify the profile is actually used

```bash
./gradlew :androidApp:assembleRelease
unzip -p androidApp/build/outputs/bundle/release/androidApp-release.aab \
  BUNDLE-METADATA/com.android.tools.build.profiles/baseline.prof | head -c 256 | xxd
```

The file must be non-empty.

## Commit

```bash
git add androidApp/src/main/baseline-prof.txt androidApp/src/main/startup-prof.txt
git commit -m "perf: regenerate baseline + startup profiles for v<new>"
```

Profiles get stale as the app evolves — regenerate at every release (the `kmp-release` skill calls this skill as step 6.5 once the `:benchmarks` module exists).

## Reporting

After generation, report:
- Path to the new `baseline-prof.txt` and its line count (a sane baseline has >100 entries; <30 usually means the journey didn't actually exercise the app)
- Path to `startup-prof.txt`
- Device/emulator the profile was generated on (model + API level)
- Any journey steps that were skipped (e.g., the "Add item" flow if not yet implemented)
