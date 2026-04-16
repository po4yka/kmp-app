---
name: kmp-feature
description: Scaffold a new KMP feature with screen, ViewModel, navigation route, and DI registration. Use when adding a new screen or feature.
argument-hint: [FeatureName]
---

# Scaffold KMP Feature

Create a new feature named **$ARGUMENTS** following project conventions.

## Steps

### 1. Create the feature directory

Create `composeApp/src/commonMain/kotlin/com/po4yka/app/ui/<feature_lowercase>/`

### 2. Add the navigation route

In `composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/Routes.kt`, add a new `@Serializable` data object or data class inside the `Route` sealed interface:

```kotlin
@Serializable
data object <FeatureName> : Route
```

### 3. Create the ViewModel

Create `<FeatureName>ViewModel.kt`:

```kotlin
class <FeatureName>ViewModel : ViewModel() {
    // Add state and logic here
}
```

### 4. Create the Screen

Create `<FeatureName>Screen.kt`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <FeatureName>Screen(
    onBack: () -> Unit,
    viewModel: <FeatureName>ViewModel = koinViewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("<FeatureName>") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("TODO: Implement <FeatureName>")
        }
    }
}
```

### 5. Register ViewModel in Koin

In `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`, add:

```kotlin
viewModelOf(::<FeatureName>ViewModel)
```

### 6. Add navigation entry

In `composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/AppNavigation.kt`, add inside the `entryProvider` block:

```kotlin
entry<Route.<FeatureName>> {
    <FeatureName>Screen(
        onBack = { backStack.removeLastOrNull() }
    )
}
```

### 7. Update SavedStateConfiguration if needed

If using a sealed Route interface with `subclassesOfSealed<Route>()`, the new route is auto-registered. Otherwise, add explicit serializer registration.

### 8. Verify

Run `./gradlew androidApp:assembleDebug` to confirm Android compiles.
Run `./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64` to confirm iOS links.
