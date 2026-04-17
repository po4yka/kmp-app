# KMP App Template

A production-ready Kotlin Multiplatform template for iOS and Android with shared Compose Multiplatform UI, type-safe navigation, Koin DI, Room KMP persistence, Ktor networking, and a pre-wired monochrome design system. Ships with a full Claude Code / Codex / Cursor agent configuration: imperative rules, on-demand skills, and verification gates.

## What you get

- **One shared UI codebase.** Compose Multiplatform renders the same screens on Android and iOS. No per-platform UI duplication.
- **Type-safe navigation.** Navigation 3 with `@Serializable` sealed routes — no string paths, no manual deep-link wiring.
- **Persistence that works on both platforms.** Room KMP with `BundledSQLiteDriver` — identical DAOs in `commonMain`, platform-specific DB builders in `androidMain` / `iosMain`.
- **Networking that respects platform engines.** Ktor client with OkHttp on Android and Darwin on iOS — same `HttpClient` API surface, native-performance transport.
- **A real design system.** [`industrial-design-cmp`](https://github.com/po4yka/industrial-design-cmp) is consumed via JitPack — monochrome palette, bundled fonts, `IndustrialTokens` for spacing/motion/radius. No placeholder Material 3 purple.
- **Agent rules on day one.** `AGENTS.md` is the single source of truth for Claude Code, Codex CLI, and Cursor. On-demand skills (`kmp-feature`, `kmp-entity`, `kmp-platform-audit`, `kmp-build`, `compose-patterns`, `industrial-design`) handle repeatable workflows.
- **Verification pipeline baked in.** Fastest-first: detekt → Android build → iOS link → tests. Stop-on-first-failure.

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
| Android Gradle Plugin | AGP | 9.0.1 |
| Static analysis | [detekt](https://detekt.dev/) | 1.23.8 |

## Project structure

```
kmp-app/
├── composeApp/                       # Shared KMP module
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/com/po4yka/app/
│       │   │   ├── App.kt            # Root composable, wires AppTheme + navigation
│       │   │   ├── navigation/       # Nav3 routes + graph assembly
│       │   │   ├── ui/               # Feature screens + ViewModels + theme
│       │   │   ├── di/               # Koin modules (shared + expect/actual PlatformModule)
│       │   │   ├── data/             # Room DAOs, entities, Ktor client factory
│       │   │   └── platform/         # expect/actual primitives (Platform.kt)
│       │   └── composeResources/
│       │       └── values/           # strings.xml (shared across platforms)
│       ├── androidMain/              # Android actuals: Room DB, Settings, Ktor OkHttp
│       └── iosMain/                  # iOS actuals: Room DB, Settings, Ktor Darwin, MainViewController
├── androidApp/                       # Android app entry (com.android.application)
├── iosApp/                           # iOS SwiftUI wrapper (imports ComposeApp.framework)
├── gradle/
│   ├── libs.versions.toml            # Version catalog
│   └── wrapper/
├── config/detekt/detekt.yml          # detekt rules
├── .claude/skills/                   # On-demand Claude Code skills
│   ├── kmp-feature/
│   ├── kmp-entity/
│   ├── kmp-platform-audit/
│   ├── kmp-build/
│   ├── compose-patterns/
│   └── industrial-design/            # Git subtree from industrial-design-cmp
├── AGENTS.md                         # Canonical agent rules (shared across tools)
└── CLAUDE.md                         # Thin wrapper that imports AGENTS.md
```

The project follows the **AGP 9.0 two-module layout**: `composeApp` is a `com.android.kotlin.multiplatform.library` (the KMP shared module, no Application manifest), while `androidApp` is the `com.android.application` entry point that consumes it.

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
./gradlew help
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
./gradlew detekt                                                # 1. Static analysis (fastest)
./gradlew androidApp:assembleDebug                              # 2. Android compiles
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64        # 3. iOS links
./gradlew composeApp:allTests                                   # 4. All KMP tests
```

The GitHub Actions workflow runs these on every PR. **Never extend `config/detekt/baseline.xml`** to suppress violations — fix the root cause. This is a blocking rule.

## Agent configuration

This template is built for AI-assisted development. Both Claude Code and Codex CLI (and Cursor) read the same rules from [`AGENTS.md`](AGENTS.md) — `CLAUDE.md` imports it via `@AGENTS.md`.

### Rules (always loaded)

`AGENTS.md` covers: tech stack, module layout, build commands, **verification order**, coding discipline (think-before-coding, simplicity first, surgical changes, goal-driven execution), and **KMP rules** as imperative one-liners:

- `expect`/`actual` is for platform wiring only — never business logic
- Room DAOs must be `suspend` or return `Flow` (iOS linker rejects blocking calls)
- Use Koin for DI — never Hilt (`hiltViewModel()` won't compile in `commonMain`)
- `Res.*` for resources in shared code — never `R.*`
- Navigation routes are `@Serializable` sealed subtypes of `Route : NavKey`
- Verify KMP targets before adding a dependency (many AndroidX libs are Android-only)
- No Android-specific imports in `commonMain` (`LocalContext`, `collectAsStateWithLifecycle`, `@Parcelize`, …)
- Never extend detekt baselines

### Skills (on-demand)

Invoked via `/skill-name` in Claude Code, or via keyword match. Loaded only when needed so the always-on context stays lean.

| Skill | Use for |
|-------|---------|
| `kmp-feature` | Scaffold a new feature: screen + ViewModel + route + DI registration |
| `kmp-entity` | Add a Room entity + DAO and register in `AppDatabase` |
| `kmp-platform-audit` | Audit `expect`/`actual` completeness; add a new platform implementation |
| `kmp-build` | Run the verification pipeline and interpret failures |
| `compose-patterns` | Compose Multiplatform patterns, anti-patterns, API availability matrix |
| `industrial-design` | Apply the monochrome design system to a new screen — craft rules, component patterns |

## Design system

The template consumes [`industrial-design-cmp`](https://github.com/po4yka/industrial-design-cmp) via JitPack. `AppTheme` in `ui/theme/Theme.kt` is a thin wrapper over `IndustrialTheme`:

```kotlin
@Composable
fun AppTheme(
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

The `kmp-feature` skill encodes the recipe — either invoke it from Claude Code or follow manually:

1. **Route**: add a `@Serializable` data object or data class inside the `Route` sealed interface in [`navigation/Routes.kt`](composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/Routes.kt).
2. **ViewModel**: create `<Feature>ViewModel.kt` in `ui/<feature>/`. Keep state in a `StateFlow<T>`, expose semantic events via a single `onEvent(Event)` method.
3. **Screen**: create `<Feature>Screen.kt`. Takes navigation callbacks as parameters; ViewModel via `koinViewModel()` default.
4. **DI**: register the ViewModel with `viewModelOf(::<Feature>ViewModel)` in [`di/AppModule.kt`](composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt).
5. **Nav graph**: add an `entry<Route.<Feature>> { … }` block in [`navigation/AppNavigation.kt`](composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/AppNavigation.kt).
6. **Verify**: run the full pipeline.

## Customization

This is a starter template — fork or clone, then adjust:

1. **Package name**: rename `com.po4yka.app` throughout (search-and-replace across `composeApp/`, `androidApp/`, package declarations, and Gradle namespaces).
2. **Android IDs**: update `applicationId` in [`androidApp/build.gradle.kts`](androidApp/build.gradle.kts) and `namespace` fields in both `composeApp` and `androidApp` build files.
3. **iOS bundle ID**: update `binaryOption("bundleId", "…")` in [`composeApp/build.gradle.kts`](composeApp/build.gradle.kts) and the Xcode target in `iosApp/iosApp.xcodeproj`.
4. **App name**: change `APP_NAME` in the BuildKonfig block of `composeApp/build.gradle.kts`.
5. **Network base URL**: change `BASE_URL` in BuildKonfig, or override per-environment via `defaultConfigs`/`flavors`.
6. **Entities**: add Room `@Entity` classes and DAOs in `data/local/` (use the `kmp-entity` skill).
7. **Settings keys**: extend or replace the settings schema in `data/settings/`.
8. **Design system**: fork `industrial-design-cmp` or swap the dependency for your own `MaterialTheme` — `AppTheme.kt` is one file.

## Troubleshooting

**"`Res.*` imports unresolved"** — the `Res` class regenerates when fonts or strings change. Run `./gradlew composeApp:generateComposeResClass` or clean + rebuild.

**"iOS framework won't link"** — verify `isStatic = true` in `composeApp/build.gradle.kts` and that Xcode's "Link Binary with Libraries" references `ComposeApp.framework`. For simulator runs, use `iosSimulatorArm64`; for Apple silicon devices, `iosArm64`.

**"Room compiler doesn't run on iOS targets"** — ensure KSP is registered for each iOS target in the root `dependencies { }` block, as the template already does:

```kotlin
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
```

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
