# KMP App Template

Instructions for any AI agent (Claude Code, Codex CLI, Cursor, Gemini CLI, Copilot) working in this repo. This file is the canonical source of truth; `CLAUDE.md` imports it via `@AGENTS.md`.

## Project Overview

Kotlin Multiplatform template targeting iOS and Android with Compose Multiplatform shared UI. Shared code is split across **feature-first modules** (product areas), **data modules** (business domains), and **core modules** (reusable infrastructure). The `:composeApp` module is the composition root (app shell) that aggregates everything and produces the iOS framework. Platform `actual` implementations live in each module's `androidMain/` and `iosMain/` source sets.

## Tech Stack

- Kotlin 2.3.20, Compose Multiplatform 1.10.3, AGP 9.0.1
- **KMP library modules use `com.android.kotlin.multiplatform.library`** (via the `kmp-app.kmp-library` convention and its descendants). Never apply `com.android.library` to a KMP module.
- Navigation 3 (typed `Route : NavKey` interface; routes are per-feature `@Serializable` classes)
- Koin + Koin Compiler Plugin (DI); one module per policy area (see **Module Boundaries**)
- Room KMP with `BundledSQLiteDriver`; `@Database` lives in `:composeApp`, DAOs/entities in `:data:*`
- Ktor client + kotlinx.serialization
- Multiplatform Settings (`SharedPreferences` / `NSUserDefaults`)
- Coil 3 with Ktor network backend
- Kermit (logging)
- BuildKonfig (build-time config)
- detekt (static analysis); Kotlin explicit API mode on `:core:*` and `:feature:*:api`

## Module Layout

```
:androidApp   (com.android.application)
   └─► :composeApp

:composeApp   (kmp-app.kmp-compose)      — app shell / composition root
   │   App.kt, AppNavigation.kt (entryProvider aggregator), MainViewController.kt,
   │   AppDatabase (@Database + Room KSP here), AppModule.kt (DI aggregator),
   │   Platform.kt (expect getDatabaseBuilder()), BuildKonfig, Coil
   ├─► :core:common, :core:ui, :core:navigation, :core:network, :core:settings
   ├─► :data:sample
   └─► :feature:home:impl, :feature:detail:impl
         (transitively pull :feature:home:api, :feature:detail:api)

:core:common        (kmp-app.kmp-public-library) — Kermit, coroutines, datetime; no module deps
:core:ui            (kmp-app.kmp-public-compose) — AppTheme, reusable composables (industrial-design)
:core:navigation    (kmp-app.kmp-public-library) — Route : NavKey marker; Navigation3 api
:core:network       (kmp-app.kmp-public-library) — HttpClientFactory + networkModule(baseUrl)
:core:settings      (kmp-app.kmp-public-library) — AppSettings + settingsModule + platformSettingsModule()

:data:sample        (kmp-app.kmp-data)           — SampleEntity, SampleDao

:feature:home:api   (kmp-app.kmp-public-library) — @Serializable HomeRoute : Route
:feature:home:impl  (kmp-app.kmp-feature-ui)     — HomeScreen, HomeViewModel,
                                                    homeFeatureModule, homeEntries(...)
:feature:detail:api (kmp-app.kmp-public-library) — @Serializable DetailRoute(itemId) : Route
:feature:detail:impl(kmp-app.kmp-feature-ui)     — DetailScreen, DetailViewModel,
                                                    detailFeatureModule, detailEntries(...)

iosApp (Swift, Xcode)
   └─► imports ComposeApp.framework built from :composeApp
```

Every module (except `:androidApp`) is a KMP library with `commonMain`, `androidMain`, and `iosMain` source sets.

## Source Set Layout

Within **every** module:

| Source set | What belongs here |
|------------|-------------------|
| `commonMain` | Compose UI, ViewModels, Koin modules, Room DAOs/entities, Ktor client, domain models, routes |
| `androidMain` | Android `actual` impls: DB builder via `Context`, `SharedPreferences` Settings, OkHttp Ktor engine, Android-specific Koin bindings |
| `iosMain`     | iOS `actual` impls: DB builder via `NSFileManager`, `NSUserDefaults` Settings, Darwin Ktor engine. `MainViewController.kt` lives in `:composeApp/iosMain` specifically. |

## Module Boundaries

Dependency rules keep the graph acyclic and the app shell in exclusive control of composition.

**Allowed direction (anything else is a review blocker):**

| From → To | `:core:common` | other `:core:*` | `:data:*` | `:feature:*:api` | `:feature:*:impl` | `:composeApp` |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| `:core:common` | — | ✗ | ✗ | ✗ | ✗ | ✗ |
| other `:core:*` | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| `:data:*` | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| `:feature:*:api` | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ |
| `:feature:*:impl` | ✓ | ✓ | ✓ | ✓ | ✗ | ✗ |
| `:composeApp` | ✓ | ✓ | ✓ | ✓ | ✓ | — |
| `:androidApp` | ✗ | ✗ | ✗ | ✗ | ✗ | ✓ |

- **New product areas are `:feature:<name>:api` + `:feature:<name>:impl`.** Scaffold with the `kmp-feature` skill. Never drop screens into `:composeApp` or another feature.
- **Cross-feature navigation happens via callback props wired by `:composeApp`.** Depend on another feature's `:api` only when the source feature needs to construct that feature's typed route locally.
- **Business domains live in `:data:<domain>`.** `@Entity`/`@Dao` never appear in a `:feature:*` module.
- **`:composeApp` is the only composition root.** It owns `AppDatabase`, the Koin aggregator (`appModules()`), and the NavDisplay's `entryProvider` block.
- **Prefer `implementation(project(...))` over `api(...)`.** `api` is only used on `:core:navigation` (re-exposes Navigation 3) and `:core:common` (re-exposes Kermit/coroutines/datetime).
- **New repeated build logic goes into a convention plugin.** If the same Gradle block appears in two modules, extract it into `build-logic/` before adding it to a third.
- **Add a `:feature:<name>:domain` module only when justified.** Triggers: ≥2 repositories orchestrated by the feature, non-trivial state machines, or logic shared across features. Default: no domain layer; put use-case code in the ViewModel or repository.

### Convention plugins

- `kmp-app.kmp-library` — KMP + `com.android.kotlin.multiplatform.library` + iOS framework. Base for all other conventions.
- `kmp-app.kmp-compose` — `kmp-library` + Compose Multiplatform.
- `kmp-app.kmp-public-library` — `kmp-library` + `kotlin { explicitApi() }`. Apply on `:core:common`, `:core:navigation`, `:core:network`, `:core:settings`, `:feature:*:api`.
- `kmp-app.kmp-public-compose` — `kmp-compose` + `kotlin { explicitApi() }`. Apply on `:core:ui`.
- `kmp-app.kmp-feature-ui` — `kmp-compose` + kotlinx-serialization + Navigation3 + koin-compose-viewmodel + lifecycle. Apply on `:feature:*:impl`.
- `kmp-app.kmp-data` — `kmp-library` + kotlinx-serialization + room-runtime + sqlite-bundled + koin + coroutines. Apply on `:data:*`.
- `kmp-app.android-application` — for `:androidApp` only.

Never reapply Compose, Room, or KSP plugins per module when a convention already covers them.

## Navigation

- Routes are `@Serializable` classes in `:feature:<x>:api`, implementing `com.po4yka.app.core.navigation.Route`. Strings and raw destinations are forbidden.
- Routes are **not `sealed`** — Kotlin `sealed` can't span modules. Polymorphic registration for saved-state serialization lives once in `composeApp/…/navigation/AppNavigation.kt`'s `SavedStateConfiguration` (explicit `subclass(FooRoute::class, FooRoute.serializer())` for each route).
- Each `:feature:<name>:impl` exposes one extension: `fun EntryProviderScope<NavKey>.<name>Entries(...)`. The app shell calls it inside its `entryProvider {}` block. No overloads.
- Entry decorators are wired once in `AppNavigation`: `rememberSaveableStateHolderNavEntryDecorator()` + `rememberViewModelStoreNavEntryDecorator()`. ViewModels obtained through `koinViewModel()` inside an `entry<...>` block are scoped to that back-stack entry and die when the entry pops.
- **State scope decision rule:**
  - Screen state that must die with the back-stack entry → screen `ViewModel` (scoped via the entry decorator).
  - Lightweight input state that must survive config change but not back-stack pop → `rememberSaveable` with a `@Serializable` saver.
  - App-wide persistent state → repository in `:data:*`, injected into the VM.
- Navigate forward: `backStack.add(SomeRoute(...))`. Back: `backStack.removeLastOrNull()`.

## State Management

See `docs/state-management.md` for patterns and anti-patterns.

- Every screen has exactly one immutable `UiState` data class. `MutableStateFlow` is a VM internal; never expose `MutableState<*>` from the VM.
- State flows down (VM → composable); events flow up (composable → VM via lambda props or sealed `Action`/`Intent`). No back-channel.
- One-shot commands (snackbar, navigate away, share) use `Channel<Effect>` with `receiveAsFlow()`. Never a consumable boolean flag.
- Business logic lives in the state holder (VM or repository). Composables render state and emit events — no branching on data-layer concerns.
- One state holder per screen. Cross-screen shared state goes through a repository, not a shared VM.

## Platform Boundaries

- **Services that MUST go behind a `commonMain` interface + Koin binding** (interface in the relevant `:core:*` or `:feature:*` module; `actual` classes in `androidMain`/`iosMain`):
  - secure storage (Keystore / Keychain), notifications (FCM / APNs), background scheduler (WorkManager / BGTaskScheduler), analytics, crash reporting, biometrics, deep-link parser, share sheet, audio/video player, haptics, file picker, location, connectivity monitor.
- `expect`/`actual` is reserved for stateless one-liners (platform name, UUID generation, default locale). If the thing has a lifecycle, state, async work, or needs fakes in tests — use an interface.
- `commonMain` never imports `android.*`, `androidx.activity.*`, `platform.*`, or `kotlinx.cinterop.*`. Those are platform-only.
- Android-specific Compose APIs (`LocalContext`, `collectAsStateWithLifecycle`, `@Parcelize`, `AndroidView`, `BackHandler`, `hiltViewModel()`) do not compile in `commonMain` — use the KMP alternatives listed in the `compose-patterns` skill.

## Data Ownership

- Features never inject a DAO directly. They depend on a **repository interface** declared in `:data:<domain>`, implemented in the same module, bound in a Koin module exposed by that `:data:*` module.
- Repositories own conflict resolution (network ↔ local cache) and are the only place a DAO is used.
- **Cross-feature navigation passes IDs, never entities or DTOs.** The destination feature looks up the entity via its own repository. Payload-in-route is a review blocker.
- Template state: `HomeViewModel` and `DetailViewModel` depend directly on `SampleDao` — this is a known deviation kept for template simplicity. Introducing `SampleRepository` is the natural first step when the domain grows.
- Room DAOs are `suspend` or return `Flow`. Blocking DAO calls fail the iOS linker.

## Resources

- Strings, drawables, fonts, and plurals for a feature live in that feature's `:impl/src/commonMain/composeResources/`. App-global copy lives in `:composeApp/src/commonMain/composeResources/`.
- Access via `stringResource(Res.string.x)` / `painterResource(Res.drawable.x)` / `pluralStringResource(Res.plurals.x, count)`. Never `R.string.*` or `R.drawable.*` in shared code.
- Qualifiers go in hyphenated sibling folders: `values-fr/`, `values-ar/`, `drawable-dark/`, `drawable-xxhdpi/`, `font-sw600dp/`.
- **No hardcoded user-facing strings in code.** Every user-facing string goes through a `Res.string.*` key — even one-off labels.
- Each module generates its own `Res` under `<module-package>.generated.resources`. Never import another module's `Res`; copy the asset or move the string to a shared module.
- RTL: test any screen with directional UI using a `values-ar` qualifier before shipping. Use `LocalLayoutDirection` for conditional mirroring.

## Testing

See `docs/testing.md` for the full matrix, fake/mock guidance, Turbine patterns, and Roborazzi workflow.

| Layer | Where | What to cover |
|---|---|---|
| Pure logic, ViewModels with fakes, serialization | every module's `commonTest` | Business rules, state reducers, DTO round-trips |
| Platform actuals | `androidMain`/`iosMain` tests | `SharedPreferences` settings, Darwin Ktor, `NSFileManager` paths |
| Compose UI snapshots | `<module>/androidUnitTest` via Roborazzi | Each golden state per screen (wired once AGP unblocks KMP Android unit tests) |
| End-to-end UI | `:composeApp/androidInstrumentedTest` (future) | Happy-path flows on a real device |
| Performance | `:composeApp/benchmarks` (future) | Cold start, nav transition, scroll p95 |

- Every VM bug fix ships with a `commonTest` that would have caught it.
- Integration tests hit real Room via `Room.inMemoryDatabaseBuilder` — no mocked DAOs in integration tests.
- Coroutines-test + Turbine for `Flow` assertions. No `delay`/`Thread.sleep` in tests.

## Performance

See `docs/performance.md` for budget categories, Baseline/Startup Profile generation, and the Compose stability checklist.

- Budget categories are defined up front: cold start, warm start, first-frame render, nav transition, scroll frame p95, memory peak. Concrete numbers are pinned against the first release-candidate measurement — not guessed now.
- Baseline Profile + Startup Profile generation is part of the release pipeline from day one, not an afterthought.
- Compose stability: annotate shared data classes emitted by VMs with `@Immutable`. Keep hot composables skippable (stable params, no inline lambda capture of unstable state).
- Macrobenchmark covers the top 3 user journeys (cold start → Home, Home → Detail, add item). Regressions fail CI once budgets are pinned.

## Build Variants

See `docs/variants.md` for signing matrix, environment rules, and how to add a `staging` variant when needed.

- Two variants today: `debug` (local development, debug keystore) and `release` (shipping, release keystore via CI secrets; see `docs/release.md`).
- `staging` is added only when a staging backend exists. Until then, env-specific constants stay behind BuildKonfig, not feature flags.
- All per-variant constants (base URLs, feature flags, analytics keys, signing) live in BuildKonfig fields or CI-injected env vars. **Never** sprinkle `if (BuildConfig.DEBUG)` branches through feature code.
- When variants diverge, use `applicationIdSuffix` so they install side-by-side on device.

## Build Commands

- Android: `./gradlew androidApp:assembleDebug`
- iOS framework: `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
- Tests: `./gradlew composeApp:allTests` (+ per-module `allTests` for modules with their own tests, e.g., `./gradlew core:settings:allTests`)
- Lint: `./gradlew detekt`
- Release: `./gradlew androidApp:assembleRelease`

## Verification Order

Run these after every change, in this order — fastest-first. Stop on the first failure; fix it before continuing.

1. `./gradlew detekt`
2. `./gradlew androidApp:assembleDebug`
3. `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64`
4. `./gradlew composeApp:allTests`
5. `./gradlew core:settings:allTests` (any module with its own tests)

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

Imperative invariants most often violated by LLMs unfamiliar with KMP linker behavior. These are linker-level invariants; the policy-oriented rules live in the dedicated sections above.

- **`expect`/`actual` is for platform wiring only.** No business logic in `expect` declarations. Keep logic in `commonMain` Kotlin classes; reserve `expect` for thin platform primitives or `PlatformModule` DI glue.
- **Room DAOs must be `suspend` or return `Flow`.** Blocking DAO calls fail the iOS linker.
- **Use Koin for DI. Never Hilt.** `hiltViewModel()` and `@HiltViewModel` do not compile in `commonMain`. Use `viewModelOf` + `koinViewModel()`.
- **Verify KMP targets before adding a dependency.** Before adding a line to `commonMain.dependencies { … }`, confirm the artifact publishes `-jvm`, `-iosarm64`, `-iosX64`, `-iosSimulatorArm64` on Maven Central. Otherwise, scope to `androidMain`/`iosMain` or wrap behind a platform interface.
- **Never extend detekt or lint baselines.** Fix the underlying violation. Extending `config/detekt/baseline.xml` is a blocking review failure.
- **Koin modules are owned by the module that declares the bindings.** Each `:feature:*:impl` exposes `<feature>FeatureModule`; each `:core:*` with bindings exposes `<core>Module` (+ `platform<Core>Module()` expect/actual when platform-specific); `:composeApp/di/AppModule.kt` aggregates them in `appModules()`.

## Visibility

See `docs/visibility.md` for the explicit-API audit guide.

- Every declaration in `:core:*` and `:feature:*:api` is explicitly `public` or `internal`. No default visibility.
- Kotlin explicit API mode is **enforced by the compiler** on `:core:common`, `:core:ui`, `:core:navigation`, `:core:network`, `:core:settings`, `:feature:home:api`, `:feature:detail:api` (via `kmp-app.kmp-public-library` / `kmp-app.kmp-public-compose` conventions).
- `:feature:*:impl` modules do not enable explicit API mode — their intentional public surface is the nav-entries function + Koin module; everything else is incidental.
- Audit rule during review: when a PR adds a `public` symbol to a public-mode module, ask "who outside this module consumes it?" If nobody does, mark it `internal`.

## Dependency Governance

- Single version catalog `gradle/libs.versions.toml`. Every dep declaration uses `libs.*` (main scripts) or `catalog.findLibrary(...)` (convention plugins via `build-logic/src/main/kotlin/Libs.kt`). **No inline versions** in `build.gradle.kts`.
- Convention plugins in `build-logic/` own recurring dependency groups. The third module that wants the same `implementation(libs.X)` block is the trigger to extract a convention.
- Renovate (`renovate.json`) auto-bumps the catalog. Renovate PRs go through the standard verification pipeline before merge.
- New deps require a KMP-target check (publishes `-jvm`, `-iosarm64`, `-iosX64`, `-iosSimulatorArm64` on Maven Central) before landing in any `commonMain.dependencies` block.

## Conventions

- Root package: `com.po4yka.app`. Sub-package per module (e.g., `com.po4yka.app.feature.home.impl`, `com.po4yka.app.core.settings`).
- Android namespace per module matches the Kotlin package and is set in the module's `build.gradle.kts` under `kotlin.androidLibrary { namespace = … }`.
- `AppTheme` lives in `:core:ui` (`com.po4yka.app.core.ui.theme`); wraps `IndustrialTheme` with light/dark via `isSystemInDarkTheme()`.
- Compose resources are per-module (`<module>/src/commonMain/composeResources/`). App-global strings stay in `:composeApp`.
- ViewModels are registered via `viewModelOf(::XViewModel)` in the feature `:impl` module's `<feature>FeatureModule` (Koin), injected via `koinViewModel()`.
- Platform primitives use `expect/actual` inside the module that owns the primitive. `:composeApp` owns `getDatabaseBuilder()` (DB-specific). `:core:settings` owns `platformSettingsModule()`. Cross-module primitives use Koin bindings instead.

## Code Quality

- Kotlin style follows the [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide). Detekt (`config/detekt/detekt.yml`) enforces the mechanics (indent, wildcard imports, line length, naming, function size).
- CI hard-fails on detekt violations. Failing lint ≡ failing build. Never extend `config/detekt/baseline.xml` — fix the violation.
- Explicit API mode is enforced on the public-surface modules listed in **Visibility**. New public symbols in those modules require explicit visibility and return types.
- GitHub Actions CI runs detekt, unit tests, and the Android debug build on every PR.

## Screenshot Testing

Roborazzi is wired into `:core:ui` (where `AppTheme` lives). Screenshot tests colocate with the module whose UI they cover — add Roborazzi to each feature `:impl` module that needs screenshot coverage.

**Caveat (AGP 9.0 KMP library plugin):** `com.android.kotlin.multiplatform.library` does not yet expose an Android unit test Kotlin compilation, so `recordRoborazziDebug` is not generated for the Android target. The Roborazzi plugin does generate iOS snapshot tasks (`recordRoborazziIosSimulatorArm64`). Track [AGP issue](https://issuetracker.google.com/issues/kotlin-multiplatform-android-unit-tests) for when Android unit test compilation support lands.

When the Android task becomes available:
- Record new goldens: `./gradlew core:ui:recordRoborazziDebug` (or per feature module).
- Verify against goldens: `./gradlew core:ui:verifyRoborazziDebug`.
- Review the PNG diff in `<module>/src/androidUnitTest/snapshots/` before committing.

## Skills for Common Tasks

On-demand skills (`Skill` tool / `/<name>`) exist for repeatable workflows — use them instead of re-deriving steps:

- `kmp-feature` — scaffold a new feature as `:feature:<name>:api` + `:feature:<name>:impl` (route, screen, ViewModel, Koin module, nav entries, DI aggregation in `:composeApp`)
- `kmp-entity` — add a Room entity + DAO to an existing `:data:<domain>` module (or scaffold a new one), then register in `:composeApp`'s `AppDatabase`
- `kmp-platform-audit` — audit `expect/actual` completeness across all modules, add a new platform implementation
- `kmp-build` — run the verification pipeline
- `compose-patterns` — Compose Multiplatform coding rules, anti-patterns, and API availability
