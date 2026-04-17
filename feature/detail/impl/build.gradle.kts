plugins {
    id("kmp-app.kmp-feature-ui")
}

kotlin {
    androidLibrary {
        namespace = "com.po4yka.app.feature.detail.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.foundation)

            implementation(project(":core:common"))
            implementation(project(":core:navigation"))
            implementation(project(":data:sample"))
            implementation(project(":feature:detail:api"))
        }
    }
}
