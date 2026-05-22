import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("kmp-app.baseline-profile")
}

android {
    namespace = "com.po4yka.app.baselineprofile"

    defaultConfig {
        // UiAutomator-driven instrumentation runner that exercises :androidApp.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Gradle Managed Device used to generate the profile and run benchmarks
    // headlessly in CI. aosp-atd is the lightweight ATD image (no Play Services).
    testOptions {
        managedDevices {
            allDevices {
                create<ManagedVirtualDevice>("pixel6Api30") {
                    device = "Pixel 6"
                    apiLevel = 30
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

// The module under test. Profiles are collected against :androidApp.
baselineProfile {
    managedDevices += "pixel6Api30"
    useConnectedDevices = false
}

// `targetProjectPath` tells the `com.android.test` plugin which app this
// test module instruments — :baselineProfile depends on :androidApp only.
android.targetProjectPath = ":androidApp"

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.junit4)
}
