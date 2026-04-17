# Build Variants

Long-form companion to the **Build Variants** section of `AGENTS.md`. What `debug` and `release` mean concretely today and the rules for adding a `staging` variant when the team needs one.

## Current state

| Variant | Keystore | Base URL | Analytics | `applicationId` | Notes |
|---|---|---|---|---|---|
| debug | debug keystore (AGP default) | `BuildKonfig.BASE_URL` (shared constant, `https://api.example.com`) | off | `com.po4yka.app` (no suffix) | Day-to-day development; `isMinifyEnabled = false` |
| release | release keystore (CI secret `ANDROID_KEYSTORE_BASE64` + `ANDROID_KEY_PASSWORD`) | `BuildKonfig.BASE_URL` | off | `com.po4yka.app` | Shipping; `isMinifyEnabled = true`, `isShrinkResources = true`, Proguard via `proguard-android-optimize.txt` + `proguard-rules.pro` |

Signing config is defined in `androidApp/build.gradle.kts` (`android { buildTypes { release { ... } } }`). Keystore loading for release should pull from environment variables in CI (see `docs/release.md`).

## Rules

- **All per-variant constants live in BuildKonfig fields or CI env vars.** Never hardcode an `if (BuildConfig.DEBUG)` branch inside feature code to toggle a URL or a feature flag — when the rule breaks down, the feature module ends up depending on `BuildConfig`, which it must not.
- **Config access goes through a typed wrapper.** `BuildKonfig.BASE_URL` is read in one place (`:composeApp/di/AppModule.kt` when calling `networkModule(BuildKonfig.BASE_URL)`). Features receive the baseline URL via Koin — they don't read BuildKonfig directly.
- **Application IDs** use `applicationIdSuffix` when variants need to install side-by-side. Base ID (`com.po4yka.app`) stays constant; variants add `.debug`, `.staging` as needed. This keeps the release-variant ID stable across the app's lifetime.

## Adding `staging`

When a staging backend exists:

1. Add `staging` build type under `androidApp/build.gradle.kts`:
   ```kotlin
   buildTypes {
       create("staging") {
           initWith(getByName("release"))
           applicationIdSuffix = ".staging"
           signingConfig = signingConfigs.getByName("debug") // or a dedicated staging key
           isMinifyEnabled = false
       }
   }
   ```
2. Add per-variant BuildKonfig:
   ```kotlin
   buildkonfig {
       packageName = "com.po4yka.app"
       defaultConfigs { buildConfigField(STRING, "BASE_URL", "https://api.example.com") }
       defaultConfigs("debug")   { buildConfigField(STRING, "BASE_URL", "https://dev.example.com") }
       defaultConfigs("staging") { buildConfigField(STRING, "BASE_URL", "https://staging.example.com") }
       defaultConfigs("release") { buildConfigField(STRING, "BASE_URL", "https://api.example.com") }
   }
   ```
3. Update CI to build and upload `assembleStaging` artifacts on `develop` branch merges.
4. iOS: match by Xcode scheme (Debug/Release/Staging). Staging scheme passes a compile flag or `Info.plist` entry that the Swift entry point reads before initializing Koin.

## Feature flags

- Feature flags flow through `BuildKonfig` fields for compile-time flags and through a runtime `FeatureFlags` interface (Koin-bound, implemented against a remote config source) for runtime flags.
- Never scatter flag reads across the codebase. Features receive specific booleans as constructor params to the VM — the DI graph does the flag-to-boolean translation.

## Signing

Signing details live in `docs/release.md`. Summary:
- Debug: AGP-managed debug keystore (unstable across machines — fine for development).
- Release: CI-injected keystore from `ANDROID_KEYSTORE_BASE64` / `ANDROID_KEY_ALIAS` / `ANDROID_KEY_PASSWORD` / `ANDROID_KEYSTORE_PASSWORD`. Never commit the keystore file.
- Staging (future): same mechanism as release, separate secret names.

## Sources

- Android Developers: *Configure build variants* — build types, product flavors, source sets.
- Android Developers: *Sign your app* — signing configuration via environment variables for CI.
