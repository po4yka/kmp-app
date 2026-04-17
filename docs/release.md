# Release Guide

This document covers what you need to configure to ship release builds from the
`.github/workflows/release.yml` workflow.

The workflow triggers on any tag that matches `v*` (e.g. `git tag v1.0.0 && git push --tags`).

---

## Android

### 1. Generate a keystore

Run this once on your local machine and keep the file out of source control:

```sh
keytool -genkey -v \
  -keystore release.keystore \
  -alias my-key-alias \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

You will be prompted for a keystore password, distinguished name fields, and a
key password. Note these values — you will need them as secrets below.

### 2. Base64-encode the keystore

```sh
# macOS / Linux
base64 -i release.keystore | pbcopy   # copies to clipboard on macOS
# or write to a file
base64 -i release.keystore > release.keystore.b64
```

Copy the entire base64 string (single line or multiline — GitHub handles both).

### 3. Add GitHub secrets

Go to **Settings → Secrets and variables → Actions → New repository secret** for
each of the following:

| Secret name | Value |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | The base64 string from step 2 |
| `KEYSTORE_PASSWORD` | The keystore password you chose |
| `KEY_ALIAS` | The alias you passed to `-alias` |
| `KEY_PASSWORD` | The key password (often the same as the keystore password) |

### 4. Wire signing into Gradle (required)

The workflow sets `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and
`KEY_PASSWORD` as environment variables. You need to read them in
`androidApp/build.gradle.kts` to actually sign the bundle:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

This keeps signing out of the template defaults and opt-in per CLAUDE.md conventions.

### 5. Publish to Google Play (optional)

The workflow contains a commented-out step for
[gradle-play-publisher](https://github.com/Triple-T/gradle-play-publisher).
To enable it:

1. Create a Google Play service account with the **Release manager** role and
   download its JSON key.
2. Add the JSON as a secret named `PLAY_SERVICE_ACCOUNT_JSON`.
3. Apply the `com.github.triplet.play` plugin in `androidApp/build.gradle.kts`
   and configure the `play { }` block.
4. Uncomment the publish step in the workflow.

---

## iOS

### What the workflow produces

The `release-ios` job builds two Kotlin/Native release frameworks:

- `composeApp/build/bin/iosArm64/releaseFramework/` — device slice
- `composeApp/build/bin/iosSimulatorArm64/releaseFramework/` — simulator slice

Both are uploaded as a workflow artifact named `release-framework-<tag>`.

### Shipping to TestFlight

Producing the framework is only the first step. To archive and upload to
TestFlight you also need:

1. **Link the framework** — Open `iosApp/iosApp.xcodeproj`, go to the target's
   *Frameworks, Libraries, and Embedded Content* section, and add the release
   framework path (or use a script phase that copies the artifact).

2. **App Store Connect API key** — Create an API key in App Store Connect under
   *Users and Access → Integrations → App Store Connect API* (Team Agent role).
   Download the `.p8` file and add three secrets to GitHub:

   | Secret name | Value |
   |---|---|
   | `APP_STORE_CONNECT_API_KEY_ID` | Key ID shown in App Store Connect |
   | `APP_STORE_CONNECT_API_ISSUER_ID` | Issuer ID shown on the same page |
   | `APP_STORE_CONNECT_API_KEY_BASE64` | `base64 -i AuthKey_XXXX.p8` output |

3. **Archive and upload** — Add an Xcode archive step after the framework build.
   [fastlane pilot](https://docs.fastlane.tools/actions/pilot/) or a plain
   `xcodebuild -exportArchive` invocation both work. See the
   [fastlane Getting Started guide](https://docs.fastlane.tools/getting-started/ios/setup/)
   for a complete example.

   > TODO: add a concrete `xcodebuild` example once the Xcode project is wired up.

---

## Triggering a release

```sh
git tag v1.0.0
git push origin v1.0.0
```

The workflow runs automatically. The AAB and iOS framework appear as downloadable
artifacts on the Actions run page until you connect the publish steps above.
