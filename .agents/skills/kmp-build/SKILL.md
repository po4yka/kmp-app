---
name: kmp-build
description: Build, test, and verify the KMP project. Use when asked to build, test, verify changes compile, or run CI checks locally.
allowed-tools: Bash(./gradlew *)
---

# KMP Build & Verify

Run the following verification steps in order. Stop on first failure and report the error.

## 1. Static Analysis

```bash
./gradlew detekt
```

Detekt runs across every module's commonMain/androidMain/iosMain (the root `build.gradle.kts` enumerates the source sets).

## 2. Android Build

```bash
./gradlew androidApp:assembleDebug
```

This pulls the full module graph through KSP (Room), Compose compiler, and the `kmp-app.kmp-*` conventions. Explicit API mode enforcement on `:core:*` and `:feature:*:api` kicks in here.

## 3. iOS Framework

```bash
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
```

Produces `ComposeApp.framework` for the iOS simulator. iOS-side `actual` impls compile here; failures here usually mean a missing `actual` in `iosMain`.

## 4. Tests

```bash
./gradlew composeApp:allTests
./gradlew core:settings:allTests       # run for any module that has its own tests
```

`./gradlew check` runs every module's tests + detekt in one shot if you want a single command.

## Reporting

After all steps complete, report:

- Which steps passed/failed
- Any compilation errors with file paths and line numbers — note the owning module (`:feature:home:impl`, `:core:network`, `:data:sample`, etc.) so the fix lands in the right place
- Any test failures with test names
- Any detekt violations with rule names
- Any explicit-API-mode violations (errors saying "Visibility must be specified in explicit API mode" or "Return type must be specified in explicit API mode") — fix by adding `public` / `internal` + an explicit return type in the owning module's source

Per-module failures point at the owning module, not `:composeApp`. If `:data:sample` fails to compile, fix it there — don't patch around it in the app shell.

If all pass, confirm the project is in a clean state.
