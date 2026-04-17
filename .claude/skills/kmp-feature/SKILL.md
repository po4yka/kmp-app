---
name: kmp-feature
description: Scaffold a new KMP feature as a Navigation 3 api/impl module pair. Use when adding a new screen or product area.
argument-hint: [FeatureName]
---

# Scaffold KMP Feature

Create feature **$ARGUMENTS** following the feature-first module layout. Every feature ships two modules:

- `:feature:<name>:api` — the typed route (only thing other modules are allowed to depend on).
- `:feature:<name>:impl` — screen, ViewModel, Koin module, Nav3 entries.

Use lowercase for `<name>` (e.g., `Profile` → `profile`). The app shell (`:composeApp`) wires the impl into navigation and DI.

## Steps

### 1. Include both modules in `settings.gradle.kts`

```kotlin
include(":feature:<name>:api")
include(":feature:<name>:impl")
```

### 2. `:api` build file — `feature/<name>/api/build.gradle.kts`

```kotlin
plugins {
    id("kmp-app.kmp-public-library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.feature.<name>.api"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:navigation"))
        }
    }
}
```

### 3. `:api` route — `feature/<name>/api/src/commonMain/kotlin/com/po4yka/app/feature/<name>/api/<Feature>Route.kt`

```kotlin
package com.po4yka.app.feature.<name>.api

import com.po4yka.app.core.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
public data object <Feature>Route : Route
// or, for routes that carry arguments:
// public data class <Feature>Route(public val itemId: Long) : Route
```

Routes are **not** `sealed` — Kotlin `sealed` can't span Gradle modules. Explicit API mode is enforced on `:feature:*:api` modules (via `kmp-app.kmp-public-library`), so every declaration needs an explicit `public`/`internal` modifier.

### 4. `:impl` build file — `feature/<name>/impl/build.gradle.kts`

```kotlin
plugins {
    id("kmp-app.kmp-feature-ui")
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.feature.<name>.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.foundation)

            implementation(project(":core:common"))
            implementation(project(":core:navigation"))
            // implementation(project(":data:<domain>"))      // if the feature reads/writes data
            // implementation(project(":core:ui"))            // if the feature uses shared composables
            implementation(project(":feature:<name>:api"))
            // implementation(project(":feature:<other>:api")) // only if constructing another feature's typed route
        }
    }
}
```

### 5. `:impl` ViewModel

Create `feature/<name>/impl/src/commonMain/kotlin/com/po4yka/app/feature/<name>/impl/<Feature>ViewModel.kt`:

```kotlin
package com.po4yka.app.feature.<name>.impl

import androidx.lifecycle.ViewModel

class <Feature>ViewModel : ViewModel() {
    // TODO: StateFlow, intent handlers
}
```

### 6. `:impl` screen

Create `<Feature>Screen.kt`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Feature>Screen(
    onBack: () -> Unit,
    viewModel: <Feature>ViewModel = koinViewModel(),
) {
    Scaffold(topBar = { TopAppBar(title = { Text("<Feature>") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("TODO: implement <Feature>")
        }
    }
}
```

### 7. `:impl` Koin module — `<Feature>FeatureModule.kt`

```kotlin
package com.po4yka.app.feature.<name>.impl

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val <name>FeatureModule: Module = module {
    viewModelOf(::<Feature>ViewModel)
}
```

### 8. `:impl` navigation entries — `<Feature>NavEntries.kt`

```kotlin
package com.po4yka.app.feature.<name>.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.po4yka.app.feature.<name>.api.<Feature>Route

fun EntryProviderScope<NavKey>.<name>Entries(
    onBack: () -> Unit,
) {
    entry<<Feature>Route> {
        <Feature>Screen(onBack = onBack)
    }
}
```

The receiver is `EntryProviderScope<NavKey>` — the builder type in Navigation 3. `entry<T>` is a member method of that scope, not a package-level function.

### 9. Wire into `:composeApp`

In `composeApp/build.gradle.kts`, add both project deps:

```kotlin
implementation(project(":feature:<name>:api"))
implementation(project(":feature:<name>:impl"))
```

In `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`, add the feature module to `appModules()`:

```kotlin
import com.po4yka.app.feature.<name>.impl.<name>FeatureModule

fun appModules(): List<Module> = listOf(
    // …,
    <name>FeatureModule,
)
```

In `composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/AppNavigation.kt`:

1. Add the route to the polymorphic `SerializersModule`:
   ```kotlin
   subclass(<Feature>Route::class, <Feature>Route.serializer())
   ```
2. Add the entries function to the `entryProvider` block:
   ```kotlin
   <name>Entries(onBack = { backStack.removeLastOrNull() })
   ```

### 10. Verify

Run the `kmp-build` skill, or:

```bash
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```
