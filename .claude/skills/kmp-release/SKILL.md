---
name: kmp-release
description: Cut a release of the KMP app — version bump, changelog, tag, signed artifacts for Android + iOS framework. Use when shipping a new version.
argument-hint: [semverBump=patch|minor|major]
allowed-tools: Bash(./gradlew *), Bash(git *), Bash(gh *)
---

# KMP Release

Cut release **$ARGUMENTS** (defaults to `patch`). The release flow is intentionally explicit — no automated `release-please` or similar in this template — so the human verifies each step.

The full release policy lives in `docs/release.md`; this skill encodes the executable steps.

## 1. Verify the tree is clean and on `main`

```bash
git status --short
git rev-parse --abbrev-ref HEAD   # must print `main`
git pull --ff-only
```

Refuse to continue if there are uncommitted changes or the branch is not `main`.

## 2. Resolve current and next version

Versions live in two places:

- `gradle/libs.versions.toml` — library deps; not the app version
- `androidApp/build.gradle.kts` — `versionName` / `versionCode` (or wherever the app-level metadata lives; verify with `grep -RE "versionName|versionCode" androidApp/`)

Compute the next semver per `$ARGUMENTS`:

- `patch`: `1.2.3` → `1.2.4`
- `minor`: `1.2.3` → `1.3.0`
- `major`: `1.2.3` → `2.0.0`

`versionCode` is monotonic — increment by 1 regardless of semver level.

## 3. Bump version

Edit `androidApp/build.gradle.kts`:

```kotlin
defaultConfig {
    versionName = "<new>"
    versionCode = <prev + 1>
}
```

## 4. Update the changelog

If `CHANGELOG.md` exists, prepend a new section:

```markdown
## [<new>] — YYYY-MM-DD

### Added
- ...

### Changed
- ...

### Fixed
- ...
```

Source items from `git log <last-tag>..HEAD --oneline` and curate.

## 5. Verify

Run the verification pipeline (matches `AGENTS.md` order):

```bash
./gradlew detekt
./gradlew buildHealth
./gradlew :tests:konsist:test
./gradlew androidApp:assembleDebug
./gradlew shared:linkDebugFrameworkIosSimulatorArm64
./gradlew check
```

Stop on first failure.

## 6. Build release artifacts

```bash
./gradlew androidApp:assembleRelease
./gradlew shared:linkReleaseFrameworkIosArm64
./gradlew shared:linkReleaseFrameworkIosSimulatorArm64
```

Signed `.aab` / `.apk` land in `androidApp/build/outputs/`. iOS frameworks in `shared/build/bin/`.

## 7. Commit, tag, push

```bash
git add androidApp/build.gradle.kts CHANGELOG.md
git commit -m "release: v<new>"
git tag -a "v<new>" -m "Release v<new>"
git push origin main --follow-tags
```

## 8. Open a GitHub release

```bash
gh release create "v<new>" \
  --title "v<new>" \
  --notes-file <(awk '/^## \[<new>\]/{f=1;next} /^## \[/{f=0} f' CHANGELOG.md) \
  androidApp/build/outputs/bundle/release/*.aab
```

## Failure recovery

- **Verification fails after bumping version**: do not push the tag. Reset the commit, fix, restart from step 5.
- **Already pushed a bad tag**: do not force-delete a published tag. Cut the next patch with the fix.
- **Signing fails locally**: signing keystores are CI-only per `docs/release.md`. Local `assembleRelease` will fail without the keystore; use CI to produce the signed artifact.

## Reporting

After the release, report:
- Old → new version
- Tag URL (`https://github.com/.../releases/tag/v<new>`)
- Artifact paths
- Anything skipped (e.g., changelog missing, GitHub release deferred)
