# KMP App Template

A production-ready Kotlin Multiplatform template for iOS and Android with shared Compose Multiplatform UI, a feature-first modular architecture, type-safe navigation, Koin DI, Room KMP persistence, Ktor networking, and a pre-wired monochrome design system. Ships with a full Claude Code / Codex / Cursor agent configuration: imperative rules, on-demand skills, deep-dive policy docs, and verification gates.

## What you get

- **One shared UI codebase.** Compose Multiplatform renders the same screens on Android and iOS.
- **Feature-first modular architecture.** Feature modules for product areas (`:feature:*:api` + `:feature:*:impl`), data modules for business domains (`:data:*`), small core/common modules for reusable infrastructure (`:core:*`). Shared Compose UI and ViewModels live in each module's `commonMain`.
- **Type-safe navigation.** Navigation 3 with `@Serializable` routes per feature — no string paths, no manual deep-link wiring. Routes live in each feature's `:api` module.
- **Persistence that works on both platforms.** Room KMP with `BundledSQLiteDriver` — DAOs/entities in `:data:*`, `@Database` aggregated in `:composeApp`, platform-specific DB builders in `androidMain` / `iosMain`.
- **Networking that respects platform engines.** Ktor client with OkHttp on Android and Darwin on iOS.
- **A real design system.** [`industrial-design-cmp`](https://github.com/po4yka/industrial-design-cmp) is consumed via JitPack — monochrome palette, bundled fonts, `IndustrialTokens` for spacing/motion/radius.
- **Strict public-API boundaries.** Kotlin explicit API mode is compiler-enforced on `:core:*` and `:feature:*:api` modules.
- **Agent rules on day one.** `AGENTS.md` is the canonical source; deep-dive docs live under `docs/`. On-demand skills (`kmp-feature`, `kmp-entity`, `kmp-platform-audit`, `kmp-build`, `compose-patterns`, `industrial-design`) handle repeatable workflows.
- **Verification pipeline baked in.** Fastest-first: detekt → Android build → iOS link → tests.

## Tech stack

| Category | Library | Version |
|----------|---------|--------:|
| Language | Kotlin | 2.3.20 |
| UI | [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) | 1.10.3 |
| Design system | [industrial-design-cmp](https://github.com/po4yka/industrial-design-cmp) | 0.1.0 |
| Navigation | [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) | 1.0.0-alpha05 |
| DI | [Koin](https://insert-koin.io/) + Koin Compiler Plugin | 4.2.1 |
| ViewModel | [AndroidX Lifecycle ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel) | 2.10.0-alpha05 |
| Database | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) + `BundledSQLiteDriver` | 2.8.4 |
| Network | [Ktor](https://ktor.io/) + `kotlinx.serialization` | 3.4.2 |
| Settings | [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) | 1.3.0 |
| Images | [Coil 3](https://coil-kt.github.io/coil/) + Ktor network backend | 3.4.0 |
| Logging | [Kermit](https://kermit.touchlab.co/) | 2.1.0 |
| Build config | [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) | 0.18.0 |
| Android Gradle Plugin | AGP (`com.android.kotlin.multiplatform.library`) | 9.0.1 |
| Static analysis | [detekt](https://detekt.dev/) | 1.23.8 |

## Project structure

```
kmp-app/
├── androidApp/                        # com.android.application entry point
├── composeApp/                        # app shell / composition root
│   └── src/
│       ├── commonMain/kotlin/com/po4yka/app/
│       │   ├── App.kt                 # Root composable — AppTheme + AppNavigation
│       │   ├── Platform.kt            # expect getDatabaseBuilder()
│       │   ├── data/local/            # AppDatabase (@Database + Room KSP runs here)
│       │   ├── di/AppModule.kt        # appModules() aggregator
│       │   └── navigation/AppNavigation.kt  # NavDisplay + polymorphic route registration
│       ├── androidMain/               # Android actuals (DB builder, Context binding)
│       └── iosMain/                   # iOS actuals (MainViewController.kt lives here)
├── core/
│   ├── common/                        # Kermit, coroutines, datetime
│   ├── ui/                            # AppTheme wrapping IndustrialTheme + Roborazzi hooks
│   ├── navigation/                    # Route : NavKey marker
│   ├── network/                       # HttpClientFactory + networkModule(baseUrl)
│   └── settings/                      # AppSettings + settingsModule + platformSettingsModule()
├── data/
│   └── sample/                        # SampleEntity, SampleDao (kmp-app.kmp-data)
├── feature/
│   ├── home/
│   │   ├── api/                       # HomeRoute (Serializable)
│   │   └── impl/                      # HomeScreen, HomeViewModel, homeFeatureModule, homeEntries
│   └── detail/
│       ├── api/
│       └── impl/
├── iosApp/                            # SwiftUI wrapper (imports ComposeApp.framework)
├── build-logic/                       # Composite build with precompiled convention plugins
│   └── src/main/kotlin/
│       ├── kmp-app.kmp-library.gradle.kts
│       ├── kmp-app.kmp-compose.gradle.kts
│       ├── kmp-app.kmp-public-library.gradle.kts     # + kotlin { explicitApi() }
│       ├── kmp-app.kmp-public-compose.gradle.kts     # + kotlin { explicitApi() }
│       ├── kmp-app.kmp-data.gradle.kts               # + room-runtime + koin + coroutines
│       ├── kmp-app.kmp-feature-ui.gradle.kts         # + Navigation 3 + koin-compose-viewmodel
│       ├── kmp-app.android-application.gradle.kts
│       └── Libs.kt                                   # Version-catalog helper (Project.catalog)
├── gradle/libs.versions.toml          # Single version catalog
├── config/detekt/detekt.yml           # detekt rules
├── docs/                              # Deep-dive companions to AGENTS.md
│   ├── state-management.md
│   ├── testing.md
│   ├── performance.md
│   ├── variants.md
│   ├── visibility.md
│   └── release.md
├── .claude/skills/                    # On-demand Claude Code skills
└── .agents/skills/                    # Synced mirror for Codex CLI / Cursor / Gemini / Copilot
├── AGENTS.md                          # Canonical agent rules (shared across tools)
└── CLAUDE.md                          # Thin wrapper that imports AGENTS.md
```

Module-dependency rules live in **AGENTS.md → Module Boundaries**. A quick summary: features never depend on other features' `:impl` (only `:api` if they need a typed route); `:core:*` depends only on `:core:common` + external libraries; `:composeApp` is the only composition root.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- [Xcode](https://developer.apple.com/xcode/) 15+ (for iOS)
- **JDK 17** (install via `sdk install java 17.0.11-tem` or your preferred method)
- macOS recommended (iOS targets require macOS)

## Getting started

Clone the repo and sync Gradle:

```bash
git clone https://github.com/po4yka/kmp-app.git
cd kmp-app
./gradlew projects          # lists all modules in the graph
```

### Run on Android

From Android Studio: open the project, select the `androidApp` run configuration, target device/emulator, hit Run.

From the command line:

```bash
./gradlew androidApp:installDebug
adb shell am start -n com.po4yka.app/.MainActivity
```

Or just produce the APK:

```bash
./gradlew androidApp:assembleDebug
# APK at: androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

### Run on iOS

From Xcode: open `iosApp/iosApp.xcodeproj`, pick a simulator or device, hit Run. Xcode handles the Gradle framework build via a pre-action script.

From the command line (framework only, no Xcode launch):

```bash
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
# Framework at: composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework
```

## Verification pipeline

`AGENTS.md` codifies the required stop-on-first-failure order. Run locally before pushing:

```bash
./gradlew detekt                                                # 1. Static analysis (fastest, covers all modules)
./gradlew androidApp:assembleDebug                              # 2. Android compiles through the full module graph
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64        # 3. iOS framework links
./gradlew composeApp:allTests                                   # 4. composeApp tests
./gradlew core:settings:allTests                                # 5. per-module tests (add more as modules grow)
```

Or run everything at once:

```bash
./gradlew check                                                 # detekt + every module's allTests
```

The GitHub Actions workflow runs these on every PR. **Never extend `config/detekt/baseline.xml`** to suppress violations — fix the root cause. This is a blocking rule.

## Agent configuration

This template is built for AI-assisted development. Claude Code, Codex CLI, Cursor, Gemini CLI, and Copilot read the same rules from [`AGENTS.md`](AGENTS.md) — `CLAUDE.md` imports it via `@AGENTS.md`.

### Rules (always loaded)

`AGENTS.md` covers 13 policy areas as concise imperative sections:

| Area | Summary |
|---|---|
| **Module Boundaries** | Dep-direction matrix; `:feature:*:impl` ↛ other `:impl`; `:core:*` ↛ `:data:*`/`:feature:*`; `:composeApp` is the only composition root |
| **Navigation** | `@Serializable` routes in `:api` modules; non-`sealed`; `EntryProviderScope<NavKey>.<name>Entries` extensions; state-scope rules (VM vs `rememberSaveable` vs repository) |
| **State Management** | Immutable `UiState`; UDF (state down, events up); `Channel<Effect>` for one-shot commands; one state holder per screen |
| **Platform Boundaries** | Concrete list of services that must go behind a common interface (secure storage, notifications, analytics, biometrics, deep-link, …) |
| **Data Ownership** | Repositories own data access; features never inject DAOs directly; cross-feature navigation passes IDs only |
| **Resources** | Per-module `composeResources/`; `Res.*` is module-scoped; never reach into another module's `Res` |
| **Testing** | `commonTest` matrix, Turbine + coroutines-test for `Flow`, Roborazzi for snapshots |
| **Performance** | Cold-start / scroll / memory budgets with starter targets on Pixel 6a class; Baseline + Startup Profile workflow |
| **Build Variants** | Debug/release today; rules for adding staging; per-variant BuildKonfig |
| **Visibility** | Kotlin explicit API mode compiler-enforced on `:core:*` and `:feature:*:api` |
| **Dependency Governance** | Single `libs.versions.toml`; convention plugins own recurring dep groups; Renovate auto-bumps |
| **Code Quality** | detekt config, Android Kotlin style guide, CI hard-fails |
| **KMP Rules** | `suspend`/`Flow` DAOs, Koin-not-Hilt, KMP-target check, no Android imports in `commonMain`, `expect`/`actual` discipline |

Deep-dive companion docs under [`docs/`](docs/):

- [`docs/state-management.md`](docs/state-management.md) — UDF patterns, VM lifetimes, anti-patterns
- [`docs/testing.md`](docs/testing.md) — testing matrix + Turbine + Roborazzi workflow
- [`docs/performance.md`](docs/performance.md) — budgets + Baseline/Startup Profile commands
- [`docs/variants.md`](docs/variants.md) — current debug/release reality, staging rules
- [`docs/visibility.md`](docs/visibility.md) — explicit API mode audit guide
- [`docs/release.md`](docs/release.md) — signing, CI release workflow, TestFlight

### Skills (on-demand)

Invoked via `/<skill>` in Claude Code, or via keyword match. Loaded only when needed so the always-on context stays lean.

| Skill | Use for |
|-------|---------|
| `kmp-feature` | Scaffold a new feature: `:api` (route) + `:impl` (screen, VM, Koin module, nav entries) + DI aggregation in `:composeApp` |
| `kmp-entity` | Add a Room entity + DAO to an existing `:data:<domain>` (or scaffold a new one) and register in `:composeApp`'s `AppDatabase` |
| `kmp-platform-audit` | Audit `expect`/`actual` completeness across all modules; add a new platform implementation |
| `kmp-build` | Run the verification pipeline and interpret failures (including explicit-API-mode errors) |
| `compose-patterns` | Compose Multiplatform patterns, anti-patterns, API availability matrix |
| `industrial-design` | Apply the monochrome design system to a new screen — craft rules, component patterns |

## Design system

The template consumes [`industrial-design-cmp`](https://github.com/po4yka/industrial-design-cmp) via JitPack. `AppTheme` lives in `:core:ui` (`com.po4yka.app.core.ui.theme.AppTheme`) and wraps `IndustrialTheme`:

```kotlin
@Composable
public fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    IndustrialTheme(darkTheme = darkTheme, content = content)
}
```

Use `MaterialTheme.colorScheme` / `MaterialTheme.typography` in screens — they return the industrial palette + Doto/Space Grotesk/Space Mono. For values Material 3 doesn't cover (spacing scale, motion, shape radii, status colors), import from the library:

```kotlin
import com.po4yka.industrialdesign.IndustrialTokens

Modifier.padding(IndustrialTokens.Spacing.md)
Color(IndustrialTokens.Accent.Success)
RoundedCornerShape(IndustrialTokens.Radius.Pill)
```

See the [library README](https://github.com/po4yka/industrial-design-cmp) for the full token scale and Material 3 slot mapping.

## Adding a new feature

The `kmp-feature` skill encodes the recipe. Manual path (see the skill for the full scaffolded file list):

1. **Include both modules in `settings.gradle.kts`:** `include(":feature:<name>:api", ":feature:<name>:impl")`.
2. **`:feature:<name>:api`** (applies `kmp-app.kmp-public-library`): declare `@Serializable public data object <Feature>Route : Route` (or `data class`). Explicit API mode is enforced.
3. **`:feature:<name>:impl`** (applies `kmp-app.kmp-feature-ui`): create `<Feature>Screen.kt`, `<Feature>ViewModel.kt`, `<Feature>FeatureModule.kt` (Koin), and `<Feature>NavEntries.kt` (`fun EntryProviderScope<NavKey>.<name>Entries(...)`).
4. **Wire into `:composeApp`:**
   - `composeApp/build.gradle.kts`: add `implementation(project(":feature:<name>:api"))` + `:impl`.
   - `AppModule.kt`: add `<name>FeatureModule` to the `appModules()` list.
   - `AppNavigation.kt`: register the route serializer (`subclass(<Feature>Route::class, <Feature>Route.serializer())`) and call `<name>Entries(...)` inside the `entryProvider {}` block.
5. **Verify** with the verification pipeline.

## Adding a Room entity

Use the `kmp-entity` skill. Manual summary:

1. Drop `<Entity>Entity.kt` and `<Entity>Dao.kt` into an existing `:data:<domain>` (or scaffold a new `data/<domain>` module applying `kmp-app.kmp-data`).
2. Update `:composeApp/src/commonMain/kotlin/com/po4yka/app/data/local/AppDatabase.kt`: add `<Entity>Entity::class` to `@Database(entities = [...])` and add an `abstract fun <entity>Dao(): <Entity>Dao` accessor.
3. Bind the DAO inside `AppModule.kt`'s `databaseModule`: `single<<Entity>Dao> { get<AppDatabase>().<entity>Dao() }`.
4. Verify.

All DAO functions must be `suspend` or return `Flow` — blocking DAO calls fail the iOS linker.

## Customization

Fork or clone, then adjust:

1. **Package name**: rename `com.po4yka.app` throughout (search-and-replace across all modules, package declarations, Android namespaces in each `build.gradle.kts`).
2. **Android IDs**: update `applicationId` in [`androidApp/build.gradle.kts`](androidApp/build.gradle.kts) and `namespace` fields in every module's `kotlin.androidLibrary { namespace = … }` block.
3. **iOS bundle ID**: update `binaryOption("bundleId", "…")` in [`build-logic/src/main/kotlin/kmp-app.kmp-library.gradle.kts`](build-logic/src/main/kotlin/kmp-app.kmp-library.gradle.kts) and the Xcode target in `iosApp/iosApp.xcodeproj`.
4. **App name**: change `APP_NAME` in the BuildKonfig block of `composeApp/build.gradle.kts`.
5. **Network base URL**: change `BASE_URL` in BuildKonfig. Features receive the baseline URL via `networkModule(BuildKonfig.BASE_URL)` — they never read `BuildKonfig` directly.
6. **Entities**: add Room `@Entity` classes in `data/<domain>/` (use the `kmp-entity` skill).
7. **Settings keys**: extend the `AppSettings` class in `:core:settings`.
8. **Design system**: fork `industrial-design-cmp` or swap the dependency for your own `MaterialTheme` — `AppTheme.kt` in `:core:ui` is one file.

## Troubleshooting

**"`Res.*` imports unresolved"** — the `Res` class regenerates per-module when fonts or strings change. Run `./gradlew <module>:generateComposeResClass` (e.g., `./gradlew composeApp:generateComposeResClass`) or clean + rebuild.

**"`Visibility must be specified in explicit API mode`"** — this comes from `:core:*` or `:feature:*:api` modules where Kotlin explicit API mode is enforced. Add `public` or `internal` to the declaration, and an explicit return type for functions/properties. See [`docs/visibility.md`](docs/visibility.md).

**"iOS framework won't link"** — verify `isStatic = true` in `build-logic/src/main/kotlin/kmp-app.kmp-library.gradle.kts` and that Xcode's "Link Binary with Libraries" references `ComposeApp.framework`. For simulator runs, use `iosSimulatorArm64`; for Apple silicon devices, `iosArm64`.

**"Room compiler doesn't run on iOS targets"** — ensure KSP is registered for each iOS target in `composeApp/build.gradle.kts`'s `dependencies { }` block, as the template already does:

```kotlin
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
```

**"`EntryProviderBuilder` unresolved"** — the Navigation 3 class is called `EntryProviderScope`, not `EntryProviderBuilder`. Use `fun EntryProviderScope<NavKey>.<name>Entries(...)`.

**"JitPack dependency won't resolve"** — make sure `maven("https://jitpack.io")` is in `settings.gradle.kts` under `dependencyResolutionManagement.repositories`. First-time resolution of a new version triggers a JitPack build and can take several minutes; subsequent builds use the cache.

**"detekt baseline violations after adding code"** — per project policy, never extend the baseline. Refactor to satisfy the rule (common fixes: extract helpers to split long methods, remove unused private constants, inline single-use abstractions).

## Updating the design system

The design system lives in a separate repo. To upgrade both library and skill:

```bash
# 1. Pick a new version from https://github.com/po4yka/industrial-design-cmp/tags
# 2. Bump in gradle/libs.versions.toml
sed -i '' 's/^industrial-design = ".*"/industrial-design = "0.2.0"/' gradle/libs.versions.toml

# 3. Pull the skill updates
git subtree pull --prefix=.claude/skills/industrial-design \
  https://github.com/po4yka/industrial-design-cmp.git skill-only --squash

# 4. Verify
./gradlew detekt androidApp:assembleDebug composeApp:linkDebugFrameworkIosSimulatorArm64 composeApp:allTests
```

## License

[MIT](LICENSE)
