---
name: kmp-platform-audit
description: Audit expect/actual declarations and platform-specific code for completeness and correctness. Use when adding platform code or reviewing multiplatform implementations.
allowed-tools: Bash(./gradlew *) Read Grep Glob
---

# Audit KMP Platform Code

Audit expect/actual declarations and platform-specific code in this project.

## Steps

### 1. Find all expect declarations in commonMain

```
grep -r "expect " composeApp/src/commonMain/
```

List every `expect` class, function, and property found.

### 2. Verify actual implementations exist in both platforms

For each expect declaration found in step 1, confirm a matching `actual` exists in:
- `composeApp/src/androidMain/`
- `composeApp/src/iosMain/`

Report any missing actuals.

### 3. Check Room DAOs use suspend functions

All DAO functions must be `suspend` (except Flow-returning queries) for KMP compatibility. Search for non-suspend, non-Flow DAO functions:

```
grep -r "@Query\|@Insert\|@Update\|@Delete" composeApp/src/commonMain/
```

Flag any function that is neither `suspend` nor returns `Flow`.

### 4. Check for Android-specific imports in commonMain

```
grep -r "^import android\.\|^import androidx\.activity\." composeApp/src/commonMain/
```

Any match is a platform leak — report the file and line.

### 5. Check for iOS-specific imports in commonMain

```
grep -r "^import platform\." composeApp/src/commonMain/
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
