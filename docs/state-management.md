# State Management

Long-form companion to the **State Management** section of `AGENTS.md`. Patterns and anti-patterns for screens and state holders in this KMP + Compose Multiplatform codebase.

## Unidirectional data flow

```
         ┌──────────────────┐
         │   Repository     │   (suspend / Flow; lives in :data:*)
         └────────┬─────────┘
                  │ data
                  ▼
         ┌──────────────────┐
         │   ViewModel      │   ← one per screen; holds MutableStateFlow<UiState>
         └────────┬─────────┘
        StateFlow │          │ Channel<Effect>
          (state) │          │ (one-shot)
                  ▼          ▼
         ┌──────────────────┐
         │    Composable    │   ← collectAsState() + LaunchedEffect for effects
         └────────┬─────────┘
                  │ events / intents (lambda props or sealed Action)
                  ▼
         ┌──────────────────┐
         │   ViewModel      │
         └──────────────────┘
```

State flows down; events flow up; one-shot commands travel through a separate `Channel`.

## UiState

One immutable data class per screen. No exceptions.

```kotlin
data class HomeUiState(
    val items: List<SampleEntity> = emptyList(),
    val inputTitle: String = "",
    val inputDescription: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)
```

- Fields are immutable (`val`). Collections are read-only (`List<T>`, not `MutableList<T>`).
- No `MutableStateFlow`, `MutableState<*>`, or `SnapshotStateList<*>` in the `UiState`. Those are VM-internal.
- Annotate with `@Immutable` when the VM emits the class frequently and Compose's stability inference can't prove it stable.

## ViewModel

```kotlin
class HomeViewModel(
    private val repository: SampleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    init {
        repository.itemsFlow
            .onEach { items -> _state.update { it.copy(items = items) } }
            .launchIn(viewModelScope)
    }

    fun onTitleChanged(value: String) {
        _state.update { it.copy(inputTitle = value) }
    }

    fun onAddClicked() {
        viewModelScope.launch {
            val snapshot = _state.value
            if (snapshot.inputTitle.isBlank()) return@launch
            _state.update { it.copy(isSaving = true) }
            runCatching { repository.add(snapshot.inputTitle, snapshot.inputDescription) }
                .onSuccess {
                    _state.update { it.copy(inputTitle = "", inputDescription = "", isSaving = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, errorMessage = error.message) }
                    _effects.send(HomeEffect.ShowSnackbar(error.message.orEmpty()))
                }
        }
    }
}

sealed interface HomeEffect {
    data class ShowSnackbar(val message: String) : HomeEffect
    data class NavigateToDetail(val itemId: Long) : HomeEffect
}
```

- Public surface: `state: StateFlow<UiState>`, `effects: Flow<Effect>`, and event functions.
- `MutableStateFlow` and `Channel` are `private`. `asStateFlow()` / `receiveAsFlow()` erase the mutable type.
- Mutate state via `_state.update { it.copy(...) }` — never reassign `_state.value` from multiple coroutines.

## Composable wiring

```kotlin
@Composable
fun HomeScreen(
    onOpenDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is HomeEffect.NavigateToDetail -> onOpenDetail(effect.itemId)
            }
        }
    }

    HomeContent(
        state = state,
        onTitleChanged = viewModel::onTitleChanged,
        onAddClicked = viewModel::onAddClicked,
        snackbarHostState = snackbarHostState,
    )
}
```

- The screen composable takes **navigation callbacks** as params (`onOpenDetail`, `onBack`). Navigation is wired by the app shell, not by the VM.
- The "content" composable (`HomeContent`) is parameter-only — no DI, no `viewModel()`. Easy to preview and snapshot-test.
- `LaunchedEffect(viewModel)` restarts the effect collector if the VM instance changes (won't in practice, but safest).
- `collectAsState()`, not `collectAsStateWithLifecycle()` — the latter is Android-only and won't compile in `commonMain`.

## ViewModel lifetime

| Scope | Source | Dies when |
|---|---|---|
| **NavEntry** | `koinViewModel()` inside an `entry<X> { ... }` lambda (decorated by `rememberViewModelStoreNavEntryDecorator`) | The nav entry pops from the back stack |
| **Saveable** | `rememberSaveable { ... }` with a `@Serializable` saver | Process death only (survives config change) |
| **Repository-scoped** | `single` in a Koin module | Process death |

**Rule:** transient screen state that should die with the entry goes in the screen VM. Form input that must survive rotation but not back-stack pop goes in `rememberSaveable`. App-wide state goes in a repository.

## One-shot effects

Use `Channel<Effect>` when the UI must act exactly once on an event: show snackbar, navigate away, start a share sheet, play a haptic.

Do **not** use a consumable boolean:

```kotlin
// ANTI-PATTERN — fires twice on recomposition, races on reset
data class UiState(val showToast: Boolean)
// composable: if (state.showToast) { toast(); viewModel.toastShown() }
```

Use `Channel` instead. Capacity `Channel.BUFFERED` is fine for user-initiated events. For high-throughput effects (unlikely in UI) consider `Channel.UNLIMITED`. Never `Channel.CONFLATED` — you will drop a legitimate effect.

## Anti-patterns

| Don't | Do |
|---|----|
| Expose `MutableStateFlow` from the VM | Expose `StateFlow` via `asStateFlow()` |
| Put VM logic in a composable (`remember { mutableStateOf(...)}` for screen state) | One VM per screen, `UiState` is the source of truth |
| Re-read many fields of a big `UiState` in one big composable | Slice — pass only the fields a child needs; Compose can skip more |
| Trigger navigation from the VM by calling `backStack.add(...)` | VM emits `Effect.NavigateToX`; shell's NavDisplay handles it |
| Boolean `shown`/`consumed` flags for one-shot events | `Channel<Effect>` |
| Business logic in `@Composable` (if/else on data-layer concerns) | Reduce in VM, composable renders |
| Broad full-screen loading that wipes visible content | Keep prior content visible, overlay a slim refresh indicator |

## Sources

- Android Developers: *Guide to app architecture* — UDF and state holders.
- Android Developers: *Compose performance* — stability and recomposition.
- Kotlin Multiplatform docs: *Discover your project* — `commonMain` constraints.
