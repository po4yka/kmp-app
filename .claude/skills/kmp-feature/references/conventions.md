# KMP Feature Conventions

## File Naming

- Screen: `FeatureNameScreen.kt`
- ViewModel: `FeatureNameViewModel.kt`

## Package

```
com.po4yka.app.ui.<feature_lowercase>/
```

## Route Naming

```kotlin
// No parameters
@Serializable
data object FeatureName : Route

// With parameters
@Serializable
data class FeatureName(val param: Type) : Route
```

## ViewModel Pattern

- Constructor injection for dependencies
- `StateFlow` for UI state
- `viewModelScope` for coroutines

```kotlin
class FeatureNameViewModel(
    private val dependency: Dependency,
) : ViewModel() {
    private val _state = MutableStateFlow(FeatureNameState())
    val state: StateFlow<FeatureNameState> = _state.asStateFlow()

    init {
        viewModelScope.launch { /* load data */ }
    }
}
```

## Screen Pattern

- `Scaffold` + `TopAppBar` as the root layout
- Navigation callbacks passed as lambda parameters
- `koinViewModel()` as the default ViewModel provider

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureNameScreen(
    onBack: () -> Unit,
    viewModel: FeatureNameViewModel = koinViewModel(),
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("FeatureName") }) }
    ) { padding ->
        // content
    }
}
```

## DI Registration

In `composeApp/src/commonMain/kotlin/com/po4yka/app/di/AppModule.kt`:

```kotlin
viewModelOf(::FeatureNameViewModel)
```

## Navigation Entry

In `composeApp/src/commonMain/kotlin/com/po4yka/app/navigation/AppNavigation.kt`:

```kotlin
entry<Route.FeatureName> {
    FeatureNameScreen(
        onBack = { backStack.removeLastOrNull() }
    )
}
```
