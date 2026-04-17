---
name: compose-patterns
description: Compose Multiplatform patterns and best practices for this KMP project. Use when writing, reviewing, or debugging Compose UI code, screens, themes, or cross-platform wiring.
user-invocable: false
---

# Compose Multiplatform Patterns

Reference for shared Compose UI in `composeApp/src/commonMain/`. Follow these defaults unless the task has an explicit reason to deviate. For repo-wide invariants (Koin-not-Hilt, Room suspend, verification order) see `AGENTS.md`.

## State Management

- Use `StateFlow` in ViewModels, collect with `collectAsState()` in Composables — **not** `collectAsStateWithLifecycle()` (Android-only).
- Never hold Android `Context` references in ViewModels — `commonMain` has no Android framework.
- Use Koin `koinViewModel()` for injection. Never `hiltViewModel()` (Android-only, won't compile in `commonMain`).
- Keep state immutable and equality-friendly (`data class`, immutable collections) so Compose can skip recomposition.
- Emit one-shot commands (snackbar, navigate, share) as `Effect` via `Channel`, never as consumable booleans in state.

## Navigation 3

- Routes are `@Serializable` sealed subtypes of `Route : NavKey` in `navigation/Routes.kt`.
- `NavDisplay` with `entryProvider { entry<Route.X> { … } }`.
- Entry decorators: `rememberSaveableStateHolderNavEntryDecorator()`, `rememberViewModelStoreNavEntryDecorator()`.
- Non-JVM platforms need `SavedStateConfiguration` with polymorphic serialization.
- Navigate: `backStack.add(Route.X)`; back: `backStack.removeLastOrNull()`.

## Screen Structure

- Every screen takes navigation callbacks as parameters (`onBack`, `onItemClick`, …).
- ViewModel injected via default parameter: `viewModel: XViewModel = koinViewModel()`.
- Use `Scaffold` + `TopAppBar` pattern with `@OptIn(ExperimentalMaterial3Api::class)`.
- Business logic stays in the ViewModel; composables render state and emit events.

## Resources

- All shared strings/drawables/fonts go in `composeApp/src/commonMain/composeResources/` (`values/strings.xml`, `drawable/`, `font/`).
- Access via `stringResource(Res.string.xxx)` and `painterResource(Res.drawable.xxx)`.
- Never `R.string` or `R.drawable` in `commonMain` — those are Android-only.
- Qualifiers are hyphenated: `values-fr/`, `drawable-dark/`, `drawable-xxhdpi/`.
- Fonts: `Font()` is a composable in CMP (unlike Android), so `Typography` construction must also be composable.
- For non-composable contexts use suspend variants: `getString(Res.string.xxx)`, `getPluralString(…)`.
- MVI rule: semantic keys/enums in state, resource resolution in UI — never resolve strings or load resources inside reducers or ViewModels.

## Image Loading

- Use Coil 3 `AsyncImage` for network images; Coil uses the Ktor backend (`coil-network-ktor3`).
- For bundled assets, use `painterResource(Res.drawable.x)`, not Coil.

## Theme

- `AppTheme` in `ui/theme/Theme.kt` wraps `MaterialTheme`.
- Light/dark via `isSystemInDarkTheme()`.
- Colors in `Color.kt`, typography in `Type.kt`.

## API Availability (commonMain vs platform)

Quick reference for what compiles where. If something is not in `commonMain`, abstract behind `expect` or an interface.

| API | commonMain | Android | iOS |
|-----|:----------:|:-------:|:---:|
| Compose Runtime/Foundation/Material3 | ✓ | ✓ | ✓ |
| `collectAsState()` | ✓ | ✓ | ✓ |
| `Res.string` / `Res.drawable` / `Res.font` | ✓ | ✓ | ✓ |
| `rememberSaveable` (with `@Serializable` saver) | ✓ | ✓ | ✓ |
| Navigation 3 with `@Serializable` routes | ✓ | ✓ | ✓ |
| `AndroidView`, `BackHandler`, `LocalContext`, `collectAsStateWithLifecycle`, `hiltViewModel()` | ✗ | ✓ | ✗ |
| `@Parcelize`, `Bundle` | ✗ | ✓ | ✗ |
| `UIKitView`, `ComposeUIViewController` | ✗ | ✗ | ✓ |

## Dependency Verification Rule

Before adding any line to `commonMain.dependencies { … }`:

1. **Coordinates** — confirm `group:artifact:version` exists on Maven Central.
2. **Target support** — confirm it publishes `-jvm`, `-iosarm64`, `-iosX64`, `-iosSimulatorArm64`. Much of AndroidX is still Android-only.
3. **API shape** — confirm the API exists in the version; signatures drift across major versions.

If verification isn't possible (no docs MCP, no network), add the dep with a `// verify latest` comment so the next pass fixes it, or scope the dep to `androidMain` only.

## Interface vs `expect/actual`

- **Interface + Koin binding** for capabilities with lifecycle, state, async, or multiple impls (player, auth, haptics, share sheet, analytics, file picker).
- **`expect/actual`** only for stateless one-liners (UUID, platform name, default locale) with no DI overhead.
- Never use `expect/actual` for anything that needs fakes in tests — interfaces win every time.

## Anti-Patterns

Pair each prohibition with the preferred alternative.

| Don't | Do |
|-------|----|
| `collectAsStateWithLifecycle()` in `commonMain` | `collectAsState()` |
| `hiltViewModel()` / `@HiltViewModel` | `koinViewModel()` + `viewModelOf(::XViewModel)` |
| `R.string.x` / `R.drawable.x` | `Res.string.x` / `Res.drawable.x` |
| `@Parcelize` / `Bundle` for saved state | `@Serializable` + custom `Saver<T, String>` with `Json.encodeToString` |
| `LocalContext.current` in shared composables | interface + DI (`interface Sharer { fun share(text: String) }`) |
| Blocking DAO calls | `suspend` DAO functions or `Flow` returns |
| `@Preview` from `androidx.compose.ui.tooling.preview` in `commonMain` | Keep previews in `androidMain` only |
| Consumable state booleans (`showSnackbar = true`, reset later) | `Channel<Effect>` delivering exactly once |
| Business logic in composables | Derive in ViewModel/state; composable only renders |
| Broad state reads in parent composables | Slice state; pass only the required props to each child |
| Full-screen loading wipes existing content | Keep old content + inline refresh indicator |
| Lottie `com.airbnb.lottie:lottie-compose` in `commonMain` | Kottie (`io.github.ismai117:kottie`) or Compottie (`io.github.alexzhirkevich:compottie`) |

## Migration Pitfalls (Android-only → CMP)

If porting Android-only code into `commonMain`:

1. `LocalContext.current` scattered everywhere → audit every usage, abstract behind `expect` or interface.
2. Compose compiler stability: non-Android targets may flag stable classes as unstable. Annotate shared data classes with `@Immutable` when you see excessive recomposition on iOS.
3. Migrate top-down from the app module, not bottom-up — leaf-first migrations leave the build broken for weeks.
4. `rememberSaveable` with `@Parcelize` breaks on iOS → switch to `@Serializable` + `Saver`.
5. Watch version lockstep: Kotlin, Compose plugin, and `kotlin.plugin.compose` must all match per [JetBrains compatibility table](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html).

## Sources

Anti-patterns, API matrix, and resource rules adapted from public compose-skill references (aldefy/compose-skill, Meet-Miyani/compose-skill); verified against this repo's stack (Kotlin 2.3.20, CMP 1.10.3, Navigation 3, Koin, Room KMP).
