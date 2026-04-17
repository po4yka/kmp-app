plugins {
    id("kmp-app.kmp-public-library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.feature.detail.api"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:navigation"))
        }
    }
}
