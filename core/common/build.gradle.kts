plugins {
    id("kmp-app.kmp-public-library")
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.core.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kermit)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
        }
    }
}
