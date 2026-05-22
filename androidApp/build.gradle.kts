plugins {
    id("kmp-app.android-application")
    id("kmp-app.kover")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.po4yka.app"

    defaultConfig {
        applicationId = "com.po4yka.app"
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        // `benchmark` build type used by :baselineProfile to measure startup
        // against release-like compilation. `isProfileable` lets Macrobenchmark
        // read accurate traces; debug signing keeps it installable locally.
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isProfileable = true
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    baselineProfile(project(":baselineProfile"))
}
