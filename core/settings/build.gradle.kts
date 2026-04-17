plugins {
    id("kmp-app.kmp-public-library")
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.core.settings"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.multiplatform.settings)
            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.multiplatform.settings.test)
        }
    }
}
