# Visibility Discipline

Long-form companion to the **Visibility** section of `AGENTS.md`. Why explicit API mode, what the compiler enforces, and how to audit public surface during review.

## Why explicit API mode

Shared modules (`:core:*`, `:feature:*:api`) are consumed by many downstream modules. Accidental `public` symbols become part of the contract — removing them later is a breaking change. The compiler can prevent this by refusing to compile a public declaration that hasn't been explicitly marked `public` or `internal` with an explicit return type.

Kotlin's explicit API mode enables the check. It's strict (compiler error, not warning) when set via the standard `kotlin { explicitApi() }` call — which is what the `kmp-app.kmp-public-library` and `kmp-app.kmp-public-compose` conventions do.

## What it enforces

Every public (i.e., consumable by other modules) declaration in a public-mode module must:

1. **Have an explicit visibility modifier** — `public`, `internal`, `protected`, or `private`. Default visibility (implicit `public`) is a compilation error.
2. **Have an explicit return type** for functions and properties — the compiler will not infer it.

Compiler error example:

```
e: Visibility must be specified in explicit API mode
```

Fix:

```kotlin
// before
fun createHttpClient(baseUrl: String): HttpClient = ...
// after
public fun createHttpClient(baseUrl: String): HttpClient = ...
```

Or, if the function is not meant to be called from another module:

```kotlin
internal fun createHttpClient(baseUrl: String): HttpClient = ...
```

## Module coverage

Enforced (via the public conventions):

- `:core:common`
- `:core:ui`
- `:core:navigation`
- `:core:network`
- `:core:settings`
- `:feature:home:api`
- `:feature:detail:api`

Not enforced (intentional — implementation-detail modules):

- `:feature:*:impl` — the intentional public surface is the `<name>FeatureModule` val and the `<name>Entries` extension function; everything else is incidental.
- `:data:*` — currently open because the data module's consumers are internal to this app. When a `:data:*` module starts being consumed across many features, promote it to `kmp-app.kmp-public-library`.
- `:composeApp` — app shell; not consumed by anyone else.
- `:androidApp` — Kotlin classes aren't a library surface.

## Review checklist

When reviewing a PR that adds a `public` symbol to a public-mode module, ask:

1. **Who outside this module calls this?** If nobody, make it `internal`. The compiler won't complain — `internal` is as explicit as `public` for explicit API mode.
2. **Is the return type stable?** If the function returns a type from another module, will changes to that type break consumers? If yes, consider wrapping.
3. **Should this be a top-level function or a member?** Top-level functions in a public module become part of the catalog of utilities; group them into classes or interfaces when cohesion warrants.

## Common audit cases

### Helper function used once

```kotlin
// Don't export if only used inside this module
public fun parseBaseUrl(raw: String): String = ...
```
→ `internal fun parseBaseUrl(raw: String): String = ...`

### Suspending function on a public interface

```kotlin
public interface SampleRepository {
    public suspend fun add(title: String, description: String)
}
```
All members of a `public` interface inherit the interface's visibility but still need explicit modifiers under explicit API mode in Kotlin 2.x. Be explicit.

### `expect` declarations

```kotlin
public expect fun platformSettingsModule(): Module
```
Both the `expect` and every `actual` must carry the same visibility modifier. If one is `public`, all are.

### `@Composable` returning Unit

```kotlin
@Composable
public fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) { ... }
```
Composables return `Unit`. Unit is implicitly the return type when the body is a block, so no return annotation is required. Visibility still is.

## Sources

- Kotlin docs: *Explicit API mode for library authors*.
- Android Developers: *Guide to app modularization* — the "minimize public API" principle.
