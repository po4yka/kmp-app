import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        compileSdk = 36
        minSdk = 27

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS targets are only configurable on macOS hosts (Kotlin/Native + Room KSP
    // require a Mac to compile iOS klibs). Skipping them on Linux keeps
    // `./gradlew check` green on the Android CI runner.
    // iosX64 (Intel Mac simulator) is intentionally omitted: Compose Multiplatform
    // 1.11+ no longer publishes that variant, and Apple silicon is now the only
    // supported development host for iOS work.
    if (OperatingSystem.current().isMacOsX) {
        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "Shared"
                isStatic = true
                binaryOption("bundleId", "com.po4yka.app.Shared")
            }
        }
    }
}
