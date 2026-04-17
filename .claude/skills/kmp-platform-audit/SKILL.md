---
name: kmp-platform-audit
description: Audit expect/actual declarations and platform-specific code for completeness and correctness. Use when adding platform code or reviewing multiplatform implementations.
allowed-tools: Bash(./gradlew *) Read Grep Glob
---

# Audit KMP Platform Code

Audit expect/actual declarations and platform-specific code in this project.

## Steps

Scan every module — not just `:composeApp`. Module roots: `composeApp/`, `core/*/`, `data/*/`, `feature/*/api/`, `feature/*/impl/`.

### 1. Find all expect declarations in commonMain across all modules

```
grep -rn "^expect " composeApp/src/commonMain composeApp/src/iosMain composeApp/src/androidMain core/ data/ feature/ 2>/dev/null | grep "commonMain"
```

List every `expect` class, function, and property found, grouped by module.

### 2. Verify actual implementations exist in both platforms

For each `expect` declaration, confirm a matching `actual` in the same module's `androidMain/` and `iosMain/` source sets (e.g., `core/settings/src/androidMain/...` and `core/settings/src/iosMain/...`).

Report any missing actuals — format: `module | symbol | missing-platform`.

### 3. Check Room DAOs use suspend functions

DAOs live in `:data:*` modules. All DAO functions must be `suspend` (except Flow-returning queries):

```
grep -rn "@Query\|@Insert\|@Update\|@Delete" data/ 2>/dev/null
```

Flag any function that is neither `suspend` nor returns `Flow`.

### 4. Check for Android-specific imports in any commonMain

```
grep -rn "^import android\.\|^import androidx\.activity\." composeApp/src/commonMain core/*/src/commonMain data/*/src/commonMain feature/*/*/src/commonMain 2>/dev/null
```

Any match is a platform leak — report the file and line.

### 5. Check for iOS-specific imports in any commonMain

```
grep -rn "^import platform\." composeApp/src/commonMain core/*/src/commonMain data/*/src/commonMain feature/*/*/src/commonMain 2>/dev/null
```

Any match is a platform leak — report the file and line.

### 6. Verify both platforms build

```
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
```

Report build success or failure for each platform.

### 7. Report findings

Summarize results in three sections:
- **Missing actuals**: list expect declarations without a matching actual on one or both platforms
- **Platform leaks**: list files with Android or iOS imports found in commonMain
- **DAO issues**: list DAO functions missing `suspend` that should have it

---

## Adding a New Platform Implementation

When a new capability needs platform-specific code, use this flow instead of the audit steps above.

### 1. Choose the right bridge

- **Interface + Koin binding** if the capability has lifecycle, state, async, or multiple impls (player, auth, haptics, share, analytics, file picker).
- **`expect/actual`** only for thin stateless primitives (UUID, platform name, default locale).

### 2. Declare the contract in `commonMain`

For interfaces, put the interface in the relevant common package (e.g., `platform/`, `data/`, or the feature package).

For `expect/actual`, put the `expect fun` / `expect class` / `expect val` in the commonMain of the module that owns the primitive (e.g., `core/settings` owns `platformSettingsModule`; `:composeApp` owns `getDatabaseBuilder`). Do NOT put cross-cutting primitives in `:core:common` unless every module genuinely needs them.

Keep the surface minimal — expose only what `commonMain` actually needs.

### 3. Add `actual` implementations on both platforms

In the same module that owns the `expect` declaration:

- `<module>/src/androidMain/kotlin/com/po4yka/app/…/*.android.kt`
- `<module>/src/iosMain/kotlin/com/po4yka/app/…/*.ios.kt`

Interface impls go as regular classes (`class AndroidPlayer(private val context: Context) : Player`). `expect` declarations pair with `actual fun` / `actual class` / `actual val`.

### 4. Wire DI if needed

If the capability is injected, add Koin bindings in the owning module's `platform<X>Module()` expect/actual pair. For example, `:core:settings/src/androidMain/.../SettingsModule.android.kt` binds the Android `Settings` impl; `:composeApp/src/androidMain/.../di/PlatformModule.android.kt` binds the `AppDatabase` and Android `Context`.

Do NOT push platform bindings into `:composeApp` when they belong to a more focused module (e.g., a video player's Android impl should live in the feature or core module that owns the `Player` interface, not in the app shell).

### 5. Verify

Run the full verification pipeline:

```
./gradlew detekt
./gradlew androidApp:assembleDebug
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew composeApp:allTests
```

Stop on the first failure — fix it before continuing.
