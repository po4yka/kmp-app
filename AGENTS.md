# KMP App Template

## Overview

Kotlin Multiplatform template for iOS and Android with Compose Multiplatform shared UI.

## Architecture

Module dependency diagram:

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
|------------|------------------|
| `commonMain` | All shared code: Compose UI, ViewModels, navigation, Koin DI modules, Room DAOs, Ktor client, domain models |
| `androidMain` | Android-specific: Room DB builder via `Context`, `SharedPreferences`-backed Settings, OkHttp Ktor engine |
| `iosMain` | iOS-specific: Room DB builder via `NSFileManager`, `NSUserDefaults`-backed Settings, Darwin Ktor engine, `MainViewController.kt` |

## Build & Test

- Build Android: `./gradlew androidApp:assembleDebug`
- Build iOS: `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
- Run tests: `./gradlew composeApp:allTests`
- Static analysis: `./gradlew detekt`
- Release build: `./gradlew androidApp:assembleRelease`

## Key Libraries

| Category | Library |
|----------|---------|
| UI | Compose Multiplatform 1.10.3 |
| Navigation | Navigation 3 (type-safe sealed routes) |
| DI | Koin + Koin Compiler Plugin |
| Database | Room KMP + BundledSQLiteDriver |
| Network | Ktor client + kotlinx.serialization |
| Settings | Multiplatform Settings |
| Images | Coil 3 |
| Logging | Kermit |
| Build config | BuildKonfig |

## Patterns

### expect/actual
Platform-specific code is declared with `expect` in `commonMain` and implemented with `actual` in `androidMain` and `iosMain`.

- `Platform.kt` — `expect class Platform` with platform name/info
- `PlatformModule.kt` — `expect val platformModule: Module` providing platform DI bindings

### Room DAOs
All DAO functions must be `suspend` (or return `Flow`). Direct blocking calls are not supported on non-Android targets. Define entities and DAOs in `commonMain`; provide the DB builder in each platform source set.

### Nav3 Routes
Routes are defined as a `@Serializable` sealed interface `Route : NavKey` in `navigation/Routes.kt`. Each screen is a data object or data class nested inside `Route`. Navigation graph is assembled in `navigation/AppNavigation.kt`.

```kotlin
@Serializable
sealed interface Route : NavKey {
    @Serializable data object Home : Route
    @Serializable data class Detail(val id: String) : Route
}
```

### DI
Koin modules live in `di/AppModule.kt` (shared) and `di/PlatformModule.kt` (expect/actual). ViewModels are registered with `viewModelOf` and injected in composables with `koinViewModel()`.

## Adding a New Feature

1. Add route to `composeApp/src/commonMain/.../navigation/Routes.kt`
2. Create `Screen.kt` + `ViewModel.kt` in `ui/<feature>/`
3. Register ViewModel in `di/AppModule.kt`
4. Add navigation entry in `navigation/AppNavigation.kt`
5. Run both Android and iOS builds to verify

## Adding a New Platform Implementation

When a new capability requires platform-specific code:

1. Declare `expect fun/class/val` in the relevant `commonMain` file
2. Add `actual` implementations in `androidMain` and `iosMain`
3. If DI is needed, add bindings in `PlatformModule.android.kt` and `PlatformModule.ios.kt`
4. Keep the common interface minimal — only expose what commonMain actually needs
5. Verify with `./gradlew composeApp:allTests` and both platform builds

## Code Quality

- **detekt** config: `config/detekt/detekt.yml`
- Run locally: `./gradlew detekt`
- Never extend the detekt baseline (`config/detekt/baseline.xml`) to suppress violations — fix the underlying issue
- GitHub Actions CI runs detekt, unit tests, and Android debug build on every PR
