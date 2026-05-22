import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.internal.os.OperatingSystem

plugins {
    id("kmp-app.kmp-compose")
    id("kmp-app.kover")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.shared"
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Navigation 3 + Lifecycle (needed here for NavDisplay in AppNavigation)
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.navigation3)

            // DI - Koin (shell aggregates feature modules + builds databaseModule)
            implementation(libs.koin.core)

            // Data - Room (@Database lives here; KSP runs here)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Serialization (polymorphic nav key registration)
            implementation(libs.kotlinx.serialization.json)

            // Image loading (app-level)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            // Crash reporting (opt-in via SENTRY_DSN BuildKonfig field; no-op when empty)
            implementation(libs.sentry.kmp)

            // Modules
            implementation(project(":core:common"))
            implementation(project(":core:ui"))
            implementation(project(":core:navigation"))
            implementation(project(":core:network"))
            implementation(project(":core:settings"))
            implementation(project(":data:sample"))
            implementation(project(":feature:home:api"))
            implementation(project(":feature:home:impl"))
            implementation(project(":feature:detail:api"))
            implementation(project(":feature:detail:impl"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    // iOS targets are only configured on macOS hosts (see kmp-app.kmp-library),
    // so the matching ksp configurations only exist there. iosX64 is omitted
    // because Compose Multiplatform 1.11+ does not publish that variant.
    if (OperatingSystem.current().isMacOsX) {
        add("kspIosSimulatorArm64", libs.room.compiler)
        add("kspIosArm64", libs.room.compiler)
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildkonfig {
    packageName = "com.po4yka.app.shared"

    // SENTRY_DSN is sourced from `-PsentryDsn=...` (CI secret), `local.properties`,
    // or environment variable `SENTRY_DSN`. Empty default keeps Sentry no-op locally.
    // See docs/sentry.md for the full setup matrix.
    val sentryDsn = (findProperty("sentryDsn") as? String)
        ?: System.getenv("SENTRY_DSN")
        ?: ""

    defaultConfigs {
        buildConfigField(STRING, "APP_NAME", "KMP App")
        buildConfigField(STRING, "BASE_URL", "https://api.example.com")
        buildConfigField(STRING, "SENTRY_DSN", sentryDsn)
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.po4yka.app.shared.resources"
}
