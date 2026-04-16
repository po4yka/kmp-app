---
name: kmp-build
description: Build, test, and verify the KMP project. Use when asked to build, test, verify changes compile, or run CI checks locally.
allowed-tools: Bash(./gradlew *)
---

# KMP Build & Verify

Run the following verification steps in order. Stop on first failure and report the error.

## 1. Android Build

```bash
./gradlew androidApp:assembleDebug
```

## 2. iOS Framework

```bash
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64
```

## 3. Tests

```bash
./gradlew composeApp:allTests
```

## 4. Static Analysis

```bash
./gradlew detekt
```

## Reporting

After all steps complete, report:
- Which steps passed/failed
- Any compilation errors with file paths and line numbers
- Any test failures with test names
- Any detekt violations with rule names

If all pass, confirm the project is in a clean state.
