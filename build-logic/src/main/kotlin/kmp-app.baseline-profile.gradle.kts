// Convention for the Baseline Profile generator module (`:baselineProfile`).
// This is a `com.android.test` module — it is NOT a KMP library and does NOT
// apply `kmp-app.kmp-library`. It builds an instrumentation APK that exercises
// `:androidApp` to produce a Baseline Profile + run Macrobenchmark.
//
// minSdk 28: Baseline Profiles and AOT-friendly profile installation require
// API 28+. The benchmark/profile-gen device (`pixel6Api30`) is API 30.
plugins {
    id("com.android.test")
    id("androidx.baselineprofile")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
