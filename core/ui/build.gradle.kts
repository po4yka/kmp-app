plugins {
    id("kmp-app.kmp-public-compose")
    id("kmp-app.kover")
    id("kmp-app.roborazzi")
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
            // Roborazzi + Robolectric come from the `kmp-app.roborazzi` convention.
            // The Compose ui-test runner is Compose-version-coupled, not catalog-managed.
            implementation("org.jetbrains.compose.ui:ui-test-junit4-android:1.10.3")
        }
    }
}
