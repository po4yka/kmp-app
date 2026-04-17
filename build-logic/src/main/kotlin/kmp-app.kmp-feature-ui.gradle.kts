plugins {
    id("kmp-app.kmp-compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(catalog.findLibrary("navigation3-ui").get())
            implementation(catalog.findLibrary("lifecycle-viewmodel-navigation3").get())
            implementation(catalog.findLibrary("lifecycle-viewmodel-compose").get())
            implementation(catalog.findLibrary("koin-core").get())
            implementation(catalog.findLibrary("koin-compose-viewmodel").get())
            implementation(catalog.findLibrary("kotlinx-serialization-json").get())
            implementation(catalog.findLibrary("kotlinx-coroutines-core").get())
            implementation(catalog.findLibrary("kermit").get())
        }
    }
}
