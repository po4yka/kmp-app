---
name: compose-patterns
description: Compose Multiplatform patterns and best practices for this KMP project. Use when writing, reviewing, or debugging Compose UI code, screens, or themes.
user-invocable: false
---

## State Management

- Use StateFlow in ViewModels, collect with collectAsState() in Composables
- Never hold Android Context references in ViewModels (this is KMP - no Android framework in commonMain)
- Use Koin koinViewModel() for injection, not Hilt

## Navigation 3

- Routes are @Serializable sealed subtypes of Route : NavKey in Routes.kt
- NavDisplay with entryProvider { entry<Route.X> { ... } }
- Entry decorators: rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()
- Non-JVM platforms need SavedStateConfiguration with polymorphic serialization
- Navigate: backStack.add(Route.X), back: backStack.removeLastOrNull()

## Screen Structure

- Every screen takes navigation callbacks as parameters (onBack, onItemClick, etc.)
- ViewModel injected via default parameter: viewModel: XViewModel = koinViewModel()
- Use Scaffold + TopAppBar pattern
- Use ExperimentalMaterial3Api opt-in for TopAppBar

## Resources

- Use Compose Multiplatform resources from composeResources/values/strings.xml
- Access via stringResource(Res.string.xxx)
- Images in composeResources/drawable/

## Image Loading

- Use Coil 3 AsyncImage for network images
- Coil uses Ktor backend (coil-network-ktor3)

## Theme

- AppTheme in ui/theme/Theme.kt wraps MaterialTheme
- Light/dark support via isSystemInDarkTheme()
- Colors in Color.kt, typography in Type.kt
