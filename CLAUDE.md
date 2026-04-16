# KMP App Template

## Project Overview

KMP + Compose Multiplatform template targeting iOS and Android with shared UI.

## Architecture

- `composeApp/` - Shared KMP library (commonMain, androidMain, iosMain)
- `androidApp/` - Android application entry point
- `iosApp/` - iOS SwiftUI wrapper
- AGP 9.0 structure: composeApp uses `com.android.kotlin.multiplatform.library`, androidApp uses `com.android.application`

## Build Commands

- Android: `./gradlew androidApp:assembleDebug`
- iOS framework: `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
- Tests: `./gradlew composeApp:allTests`
- Lint: `./gradlew detekt`
- Release: `./gradlew androidApp:assembleRelease`

## Tech Stack

- Kotlin 2.3.20, Compose Multiplatform 1.10.3, AGP 9.0.1
- Navigation: Navigation 3 (type-safe routes via sealed interface `Route : NavKey`)
- DI: Koin + Koin Compiler Plugin (not Hilt - this is KMP)
- Database: Room KMP with `BundledSQLiteDriver`
- Network: Ktor client + kotlinx.serialization
- Settings: Multiplatform Settings (SharedPreferences / NSUserDefaults)
- Image loading: Coil 3 with Ktor network backend
- Logging: Kermit
- Build config: BuildKonfig plugin

## Conventions

- Package: `com.po4yka.app`
- Platform code uses expect/actual pattern (see `Platform.kt`)
- Platform DI modules in `PlatformModule.kt` (expect/actual)
- Room DAOs must use suspend functions for non-Android targets
- Navigation routes are `@Serializable` sealed subtypes of `Route` in `Routes.kt`
- Use Koin `viewModelOf`/`viewModel` DSL for ViewModel registration
- Theme defined in `ui/theme/` (`AppTheme` with light/dark support)
- Compose resources in `composeApp/src/commonMain/composeResources/`

## Verification

Always run after changes:

1. `./gradlew androidApp:assembleDebug` - Android compiles
2. `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64` - iOS links
3. `./gradlew detekt` - static analysis passes
