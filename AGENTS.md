# KMP App Template

## Overview

Kotlin Multiplatform template for iOS and Android with Compose Multiplatform shared UI.

## Structure

- `composeApp/` - KMP shared library (commonMain, androidMain, iosMain)
- `androidApp/` - Android app entry point (Activity, Application)
- `iosApp/` - iOS SwiftUI wrapper
- All shared code lives in `composeApp/src/commonMain/`
- Platform-specific implementations in `androidMain/` and `iosMain/`

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

- **expect/actual** for platform code (`Platform.kt`, `PlatformModule.kt`)
- **Room DAOs**: suspend functions required for non-Android targets
- **Navigation**: sealed `Route : NavKey` with `@Serializable` in `Routes.kt`
- **DI**: Koin modules in `di/AppModule.kt` + `di/PlatformModule.kt`
- **ViewModels**: in commonMain using AndroidX ViewModel, injected via Koin

## Adding a New Feature

1. Add route to `composeApp/src/commonMain/.../navigation/Routes.kt`
2. Create `Screen.kt` + `ViewModel.kt` in `ui/<feature>/`
3. Register ViewModel in `di/AppModule.kt`
4. Add navigation entry in `navigation/AppNavigation.kt`
5. Run both Android and iOS builds to verify
