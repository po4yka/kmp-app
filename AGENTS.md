# KMP App Template

Instructions for any AI agent (Claude Code, Codex CLI, Cursor, Gemini CLI, Copilot) working in this repo. This file is the canonical source of truth; `CLAUDE.md` imports it via `@AGENTS.md`.

## Project Overview

Kotlin Multiplatform template targeting iOS and Android with Compose Multiplatform shared UI. All shared code (UI, navigation, DI, data, networking) lives in `composeApp/src/commonMain/`; platform `actual` implementations live in `androidMain/` and `iosMain/`.

## Tech Stack

- Kotlin 2.3.20, Compose Multiplatform 1.10.3, AGP 9.0.1
- Navigation 3 (type-safe `Route : NavKey` sealed interface)
- Koin + Koin Compiler Plugin (DI)
- Room KMP with `BundledSQLiteDriver`
- Ktor client + kotlinx.serialization
- Multiplatform Settings (`SharedPreferences` / `NSUserDefaults`)
- Coil 3 with Ktor network backend
- Kermit (logging)
- BuildKonfig (build-time config)
- detekt (static analysis)

## Module Layout

```
androidApp (com.android.application)
  └── composeApp (com.android.kotlin.multiplatform.library)
        ├── commonMain/  ← shared code, Compose UI, navigation, DI, data
        ├── androidMain/ ← Platform.android.kt, PlatformModule.android.kt
        └── iosMain/     ← Platform.ios.kt, PlatformModule.ios.kt, MainViewController.kt

iosApp (Swift)
  └── imports ComposeApp.framework from composeApp
```

## Source Set Layout

| Source set | What belongs here |
|------------|-------------------|
| `commonMain` | Shared code: Compose UI, ViewModels, navigation, Koin modules, Room DAOs/entities, Ktor client, domain models |
| `androidMain` | Android: Room DB builder via `Context`, `SharedPreferences` Settings, OkHttp Ktor engine |
| `iosMain` | iOS: Room DB builder via `NSFileManager`, `NSUserDefaults` Settings, Darwin Ktor engine, `MainViewController.kt` |

## Build Commands

- Android: `./gradlew androidApp:assembleDebug`
- iOS framework: `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
- Tests: `./gradlew composeApp:allTests`
- Lint: `./gradlew detekt`
- Release: `./gradlew androidApp:assembleRelease`

## Verification Order

Run these after every change, in this order — fastest-first. Stop on the first failure; fix it before continuing.

1. `./gradlew detekt`
2. `./gradlew androidApp:assembleDebug`
3. `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
4. `./gradlew composeApp:allTests`

## Coding Discipline

### Think Before Coding
State assumptions explicitly before starting. Surface tradeoffs and edge cases. Ask clarifying questions rather than assuming. If the approach is unclear, stop and reason it out first.

### Simplicity First
Write the minimum code that solves the problem. No speculative features. No abstractions created for a single use. If a helper is only called once, inline it.

### Surgical Changes
Touch only the files and lines required by the task. Do not refactor adjacent code, rename unrelated symbols, or reorganize untouched modules while implementing a feature.

### Goal-Driven Execution
Define verifiable success criteria before implementation. Prefer test-first loops: write the failing assertion, make it pass, verify. Done means the criteria pass, not that the code looks finished.

## KMP Rules

Each rule is imperative — do X, not Y. These are the invariants most often violated by LLMs unfamiliar with KMP linker behavior.

- **`expect`/`actual` is for platform wiring only.** No business logic in `expect` declarations. Keep logic in `commonMain` Kotlin classes; reserve `expect` for thin platform primitives or `PlatformModule` DI glue.
- **Room DAOs must be `suspend` or return `Flow`.** Blocking DAO calls fail the iOS linker. Never write a DAO function that is neither `suspend` nor `Flow`.
- **Use Koin for DI. Never Hilt.** `hiltViewModel()` and `@HiltViewModel` do not compile in `commonMain`. Use `viewModelOf` in `di/AppModule.kt` and `koinViewModel()` in composables.
- **Use `Res.*` for resources in shared code.** `R.string` / `R.drawable` do not exist in `commonMain`. Use `Res.string.*` / `Res.drawable.*` imported from `com.po4yka.app.generated.resources`.
- **Navigation routes are `@Serializable` sealed subtypes of `Route : NavKey`.** Never use string-based routes or raw destinations. All routes live in `navigation/Routes.kt`.
- **Verify KMP targets before adding a dependency.** Before adding a line to `commonMain.dependencies { … }`, confirm the artifact publishes `-jvm`, `-iosarm64`, `-iosX64`, `-iosSimulatorArm64` coordinates on Maven Central. If it doesn't, place the dependency in `androidMain` / `iosMain` or wrap behind a platform interface.
- **Prefer interfaces + DI for stateful platform services; `expect/actual` only for thin primitives.** Use an interface + Koin binding for lifecycle, async, or hardware (player, auth, haptics, share sheet). Reserve `expect/actual` for stateless one-liners (UUID, platform name, default locale).
- **No Android-specific imports in `commonMain`.** `LocalContext`, `collectAsStateWithLifecycle`, `@Parcelize`, `Bundle`, `AndroidView`, `BackHandler`, `hiltViewModel()` are all Android-only. Use `collectAsState()` instead of `collectAsStateWithLifecycle()`; abstract `Context` behind an interface or `expect`.
- **Never extend detekt or lint baselines.** Fix the underlying violation. Extending `config/detekt/baseline.xml` to suppress findings is a blocking review failure.

## Conventions

- Package: `com.po4yka.app`
- Platform primitives in `Platform.kt`; platform DI in `PlatformModule.kt` (both use `expect/actual`)
- Theme in `ui/theme/` (`AppTheme` wraps `MaterialTheme`; light/dark via `isSystemInDarkTheme()`)
- Compose resources in `composeApp/src/commonMain/composeResources/`
- ViewModels registered via Koin `viewModelOf` in `di/AppModule.kt`; injected via `koinViewModel()`

## Code Quality

- detekt config: `config/detekt/detekt.yml`
- GitHub Actions CI runs detekt, unit tests, and Android debug build on every PR
- See the **Never extend baselines** rule above

## Screenshot Testing

Roborazzi is wired into `composeApp` for screenshot regression testing. Golden PNGs live in `composeApp/src/androidUnitTest/snapshots/` and are committed as the source of truth.

**Caveat (AGP 9.0 KMP library plugin):** `com.android.kotlin.multiplatform.library` does not expose an Android unit test Kotlin compilation, so `recordRoborazziDebug` is not yet generated for the Android target. The Roborazzi plugin does generate iOS snapshot tasks (`recordRoborazziIosSimulatorArm64`). Track [AGP issue](https://issuetracker.google.com/issues/kotlin-multiplatform-android-unit-tests) for when Android unit test compilation support lands.

When the Android task becomes available:
- Record new goldens: `./gradlew composeApp:recordRoborazziDebug`
- Verify against goldens: `./gradlew composeApp:verifyRoborazziDebug`
- Review the PNG diff in `composeApp/src/androidUnitTest/snapshots/` before committing.

## Skills for Common Tasks

On-demand skills (`Skill` tool / `/<name>`) exist for repeatable workflows — use them instead of re-deriving steps:

- `kmp-feature` — scaffold a new feature (screen + ViewModel + route + DI registration)
- `kmp-entity` — add a Room entity + DAO and register in `AppDatabase`
- `kmp-platform-audit` — audit `expect/actual` completeness, add a new platform implementation
- `kmp-build` — run the verification pipeline
- `compose-patterns` — Compose Multiplatform coding rules, anti-patterns, and API availability
