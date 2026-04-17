import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "com.po4yka.app.shared"
        compileSdk = 36
        minSdk = 27

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.po4yka.app.ComposeApp")
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Navigation 3
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.navigation3)

            // Lifecycle
            implementation(libs.lifecycle.viewmodel.compose)

            // DI - Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)

            // Logging
            implementation(libs.kermit)

            // Network
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DateTime
            implementation(libs.kotlinx.datetime)

            // Image loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            // Data - Room
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Data - Settings
            implementation(libs.multiplatform.settings)

            // Design system
            implementation(libs.industrial.design)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.multiplatform.settings.test)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
