import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("kmp-app.kmp-compose")
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
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildkonfig {
    packageName = "com.po4yka.app"

    defaultConfigs {
        buildConfigField(STRING, "APP_NAME", "KMP App")
        buildConfigField(STRING, "BASE_URL", "https://api.example.com")
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.po4yka.app.resources"
}
