plugins {
    id("kmp-app.kmp-public-library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.core.navigation"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.navigation3.ui)
            api(libs.lifecycle.viewmodel.navigation3)
            api(libs.kotlinx.serialization.json)
        }
    }
}
