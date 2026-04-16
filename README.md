# KMP App Template

A Kotlin Multiplatform template for building iOS and Android applications with shared UI using Compose Multiplatform.

## Tech Stack

| Category | Library |
|----------|---------|
| UI | [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) |
| Navigation | [Jetpack Navigation 3](https://developer.android.com/guide/navigation/navigation-3) |
| DI | [Koin](https://insert-koin.io/) + Koin Compiler Plugin |
| ViewModel | [AndroidX ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel) (commonMain) |
| Logging | [Kermit](https://kermit.touchlab.co/) |
| Network | [Ktor Client](https://ktor.io/) + kotlinx.serialization |
| Database | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) |
| Settings | [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) |

## Project Structure

```
kmp-app/
├── composeApp/          # Shared KMP library (commonMain, androidMain, iosMain)
│   └── src/
│       ├── commonMain/  # Shared Kotlin code, Compose UI, navigation, DI
│       ├── androidMain/ # Android platform implementations (DB builder, Settings)
│       └── iosMain/     # iOS platform implementations (DB builder, Settings)
├── androidApp/          # Android application entry point (Activity, Application)
├── iosApp/              # iOS entry point (SwiftUI wrapper)
└── gradle/              # Version catalog and wrapper
```

The project follows the AGP 9.0 recommended structure: the KMP shared module (`composeApp`) uses the `com.android.kotlin.multiplatform.library` plugin, while the Android entry point (`androidApp`) uses `com.android.application`.

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- [Xcode](https://developer.apple.com/xcode/) (for iOS)
- JDK 17+

### Run Android

Open the project in Android Studio and run the `androidApp` configuration, or:

```bash
./gradlew androidApp:assembleDebug
```

### Run iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run, or build the framework:

```bash
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Customization

1. Replace the package name `com.po4yka.app` with your own
2. Update `applicationId` in `androidApp/build.gradle.kts`
3. Update `namespace` in both `androidApp` and `composeApp` build files
4. Configure the Ktor base URL in `HttpClientFactory.kt`
5. Add your Room entities and DAOs in `data/local/`
6. Adjust Multiplatform Settings keys in `AppSettings.kt`

## License

[MIT](LICENSE)
