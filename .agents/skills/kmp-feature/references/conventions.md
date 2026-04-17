# KMP Feature Conventions

Reference for the `kmp-feature` skill. Every new product area ships as a `:feature:<name>:api` + `:feature:<name>:impl` pair.

## Package Layout

```
com.po4yka.app.feature.<name>.api    // :feature:<name>:api
com.po4yka.app.feature.<name>.impl   // :feature:<name>:impl
```

The Android namespace in each module's `build.gradle.kts` (`kotlin.androidLibrary { namespace = … }`) matches the Kotlin package.

## File Naming

| File | Module |
|---|---|
| `<Feature>Route.kt` | `:api` |
| `<Feature>Screen.kt` | `:impl` |
| `<Feature>ViewModel.kt` | `:impl` |
| `<Feature>FeatureModule.kt` | `:impl` |
| `<Feature>NavEntries.kt` | `:impl` |

## Route

```kotlin
package com.po4yka.app.feature.<name>.api

import com.po4yka.app.core.navigation.Route
import kotlinx.serialization.Serializable

// No parameters
@Serializable
public data object <Feature>Route : Route

// With parameters
@Serializable
public data class <Feature>Route(public val itemId: Long) : Route
```

Routes are **not** `sealed` (Kotlin `sealed` can't span modules). Explicit API mode is enforced on `:feature:*:api`, so every declaration needs a `public` / `internal` modifier.

## ViewModel

```kotlin
package com.po4yka.app.feature.<name>.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class <Feature>ViewModel(
    private val repository: SomeRepository, // usually from :data:<domain>
) : ViewModel() {
    private val _state = MutableStateFlow(<Feature>UiState())
    val state: StateFlow<<Feature>UiState> = _state.asStateFlow()

    private val _effects = Channel<<Feature>Effect>(Channel.BUFFERED)
    val effects: Flow<<Feature>Effect> = _effects.receiveAsFlow()

    // event handlers below
}

data class <Feature>UiState(/* immutable fields */)
sealed interface <Feature>Effect { /* one-shot events */ }
```

See `docs/state-management.md` for the full UDF + effect pattern.

## Screen

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Feature>Screen(
    onBack: () -> Unit,
    viewModel: <Feature>ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("<Feature>") }) },
    ) { padding ->
        // content; render `state`, forward events via viewModel methods
    }
}
```

- `onBack` and any other navigation callbacks are lambda parameters — never navigate from the VM directly.
- `ViewModel` injected via `koinViewModel()` default param so the composable is scope-agnostic.

## Koin Module (`<Feature>FeatureModule.kt`)

```kotlin
package com.po4yka.app.feature.<name>.impl

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val <name>FeatureModule: Module = module {
    viewModelOf(::<Feature>ViewModel)
}
```

Each feature exposes exactly one Koin module. `:composeApp/.../di/AppModule.kt` aggregates them all in `appModules()`.

## Navigation Entries (`<Feature>NavEntries.kt`)

```kotlin
package com.po4yka.app.feature.<name>.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.po4yka.app.feature.<name>.api.<Feature>Route

fun EntryProviderScope<NavKey>.<name>Entries(
    onBack: () -> Unit,
    // add more callbacks as needed, one per cross-feature nav target
) {
    entry<<Feature>Route> {
        <Feature>Screen(onBack = onBack)
    }
}
```

The receiver is `EntryProviderScope<NavKey>` — the lambda receiver of `entryProvider {}` in Navigation 3. `entry<T>` is a member method of that scope.

## App-shell wiring (`:composeApp`)

1. `composeApp/build.gradle.kts`:
   ```kotlin
   implementation(project(":feature:<name>:api"))
   implementation(project(":feature:<name>:impl"))
   ```
2. `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`:
   ```kotlin
   import com.po4yka.app.feature.<name>.impl.<name>FeatureModule

   fun appModules(): List<Module> = listOf(
       // …,
       <name>FeatureModule,
   )
   ```
3. `composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/AppNavigation.kt`:
   ```kotlin
   // Register the route serializer
   subclass(<Feature>Route::class, <Feature>Route.serializer())
   ```
   and inside the `entryProvider {}` block:
   ```kotlin
   <name>Entries(onBack = { backStack.removeLastOrNull() })
   ```

## Conventions

- `:feature:<name>:api` applies `kmp-app.kmp-public-library` + `kotlin-serialization`.
- `:feature:<name>:impl` applies `kmp-app.kmp-feature-ui` only — it bundles Compose, navigation3, koin-compose-viewmodel, lifecycle, kotlin-serialization, kermit.

## Verification

Run the `kmp-build` skill, or:

```bash
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```
