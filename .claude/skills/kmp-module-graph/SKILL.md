---
name: kmp-module-graph
description: Author and run Konsist architecture tests enforcing the AGENTS.md module-boundary table. Use when adding a new boundary rule, debugging a Konsist failure, or scaffolding a graph assertion for a new module type.
allowed-tools: Bash(./gradlew *), Bash(rg *)
---

# KMP Module Graph

The module-boundary table in `AGENTS.md` is enforced two ways:

1. **DAGP** (`./gradlew buildHealth`) — flags unused/mis-scoped deps and `api` vs `implementation` mistakes.
2. **Konsist** (`./gradlew :tests:konsist:test`) — asserts forbidden imports, package placement, annotation usage, and visibility — at the *source* level.

Konsist tests live in `tests/konsist/src/test/kotlin/com/po4yka/app/konsist/`.

## When to add a new architecture test

Any **new review-blocker** rule in `AGENTS.md` should land as a Konsist test in the same PR. Examples of what belongs here:

- New module type added (e.g., `:domain`, `:di`) → assert allowed/forbidden imports.
- New annotation contract (e.g., "all repositories must be `@Single` Koin bindings").
- New naming convention enforced for a public surface (e.g., "every `:feature:*:api` exports exactly one `*Route` class").

What does NOT belong:

- Stylistic preferences (those go in `detekt.yml`).
- Performance budgets (those go in `docs/performance.md` + Macrobenchmark).
- Runtime invariants (those are unit tests in `commonTest`).

## Authoring a new test

The Konsist DSL is `Konsist.scopeFromProject()` → filter → assert. Examples that already exist in `ModuleBoundaryTest.kt`:

- Forbid imports between sibling `:feature:*:impl` modules.
- Force `@Entity`/`@Dao` into `com.po4yka.app.data..` only.
- Force `Route` classes to be `@Serializable` and implement `core.navigation.Route`.
- Constrain `ViewModel` packages to `com.po4yka.app.feature..impl..`.

Pattern for "X must not import Y":

```kotlin
@Test
fun `no <X> imports <Y>`() {
    Konsist.scopeFromProject()
        .files
        .filter { it.path.contains("/<X-path-segment>/") }
        .flatMap { it.imports }
        .filter { it.name.startsWith("com.po4yka.app.<y-package>.") }
        .assertEmpty(additionalMessage = "<why this is forbidden — cite AGENTS.md section>")
}
```

Pattern for "every class matching X has property Y":

```kotlin
@Test
fun `<X classes> have <Y property>`() {
    Konsist.scopeFromProject()
        .classes(includeNested = true)
        .filter { /* identify the population */ }
        .assertTrue(additionalMessage = "<rule + AGENTS.md citation>") { clazz ->
            // boolean predicate
        }
}
```

Always include `additionalMessage` — the failure message is what a future reviewer reads when they break the rule. Cite the AGENTS.md section so they know why it exists.

## Running

```bash
./gradlew :tests:konsist:test                       # all rules
./gradlew :tests:konsist:test --tests ModuleBoundaryTest
./gradlew :tests:konsist:test --tests "*VisibilityTest*"
```

Konsist tests are pulled into `./gradlew check` via the standard lifecycle — CI runs them on every PR.

## Performance note

Konsist re-parses every source file on each run. For a small template this is a few seconds. If the test module grows beyond ~30 rules or the project past ~500 source files, consider caching the scope:

```kotlin
private val projectScope by lazy { Konsist.scopeFromProject() }
```

and reusing it across tests via a JUnit 5 `@TestInstance(PER_CLASS)` annotation.

## Reporting

After running, report:
- Pass/fail per rule
- For failures: file paths + line numbers of the offending imports/declarations
- Suggested fix (which module the code should move to, which import should be deleted)

Never extend a Konsist suppression list — fix the violation. Same rule as `config/detekt/baseline.xml`: baselines are review blockers in this repo.
