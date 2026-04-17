plugins {
    id("kmp-app.kmp-public-compose")
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.core.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.industrial.design)
        }

        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
            implementation(libs.roborazzi.junit)
            implementation(libs.robolectric)
            implementation("org.jetbrains.compose.ui:ui-test-junit4-android:1.10.3")
        }
    }
}
