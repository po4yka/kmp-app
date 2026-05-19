# Sentry — KMP Crash Reporting

This template ships with [`sentry-kotlin-multiplatform`](https://github.com/getsentry/sentry-kotlin-multiplatform) wired into `:shared` (commonMain). By default it is a no-op: nothing is sent until a DSN is provided.

## Wiring

- **SDK**: `io.sentry:sentry-kotlin-multiplatform` (in `gradle/libs.versions.toml` as `sentry-kmp`)
- **Config**: `shared/src/commonMain/kotlin/com/po4yka/app/shared/observability/SentryConfig.kt` — `initSentry()`
- **Init call**: `shared/src/commonMain/kotlin/com/po4yka/app/shared/App.kt` (`remember { initSentry(); Unit }`)
- **DSN source**: `BuildKonfig.SENTRY_DSN`, populated at configuration time from one of:
  1. `-PsentryDsn=https://...@sentry.io/...` Gradle property (preferred for CI)
  2. `SENTRY_DSN` environment variable
  3. Empty (default — Sentry no-op)

The SDK is initialized exactly once via `remember { ... }` at the top of the `App` composable. Re-initialization across recompositions is suppressed.

## Setting the DSN

### Local development

You usually do **not** want Sentry firing on every local rebuild. Leave `SENTRY_DSN` unset.

If you need to test the integration locally, add to `local.properties` (gitignored) and update the `buildkonfig` block in `shared/build.gradle.kts` to read from it, **or** invoke Gradle directly:

```bash
./gradlew androidApp:assembleDebug -PsentryDsn="https://<your-dsn>@sentry.io/<project>"
```

### CI

Store the DSN as a GitHub Actions secret `SENTRY_DSN`. The `.github/workflows/release.yml` step that builds the release artifact reads it as an environment variable, which `BuildKonfig` picks up automatically:

```yaml
- name: Build release AAB
  env:
    SENTRY_DSN: ${{ secrets.SENTRY_DSN }}
  run: ./gradlew androidApp:assembleRelease
```

The debug-build CI job (`build.yml`) intentionally does **not** wire the DSN — debug builds from PR runs should not generate crash events in the production Sentry project.

## Sampling

`SentryConfig.initSentry()` currently sets `options.tracesSampleRate = 0.0` — crashes are captured, performance tracing is off. Tune after the first production release once a baseline traffic shape is known:

| Setting | Recommended starting value | Notes |
|---|---|---|
| `sampleRate` (errors) | `1.0` | Capture all errors during early production |
| `tracesSampleRate` | `0.0` → `0.1` | Bump only after you've measured event volume |
| `attachStacktrace` | `true` | iOS native crashes need this |
| `environment` | `"production"` / `"staging"` | Pipe in from `BuildKonfig` once a `staging` variant exists (`docs/variants.md`) |

## iOS-side caveats

The Kotlin SDK delegates to the Sentry Cocoa SDK on iOS. The Cocoa SDK is bundled transitively; no extra `Podfile` entry is needed when `iosApp/` imports `Shared.framework`.

If `iosApp/` adds its own Swift code that should also report to Sentry, do **not** double-init — call into Kotlin's `initSentry()` from the Swift side (it is exposed as `SentryConfigKt.initSentry()` on the framework boundary), and let the existing Cocoa instance handle both surfaces.

## Verification

To confirm the DSN reaches the SDK, build with a known DSN, run the app, then force a test crash from a debug-only screen:

```kotlin
import io.sentry.kotlin.multiplatform.Sentry

Sentry.captureMessage("Sentry wiring smoke test from <date>")
```

A new event must appear in the Sentry project within ~30 seconds. If it does not:

1. Check `adb logcat | grep -i sentry` — the SDK logs init failures.
2. Check `BuildKonfig.SENTRY_DSN` is non-empty at runtime (`Kermit.d { "DSN: $it" }` — log the *length*, never the value).
3. Confirm network access — the SDK uses HTTPS to `sentry.io` (or your self-hosted URL).

## Removing Sentry

If a future project does not want crash telemetry:

1. Delete `shared/src/commonMain/kotlin/com/po4yka/app/shared/observability/`.
2. Remove the `remember { initSentry(); ... }` line in `App.kt`.
3. Remove the `implementation(libs.sentry.kmp)` line in `shared/build.gradle.kts`.
4. Remove the `SENTRY_DSN` BuildKonfig field.
5. Remove the `sentry-kmp` entries from `gradle/libs.versions.toml`.
