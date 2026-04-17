plugins {
    id("kmp-app.kmp-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(catalog.findLibrary("room-runtime").get())
            implementation(catalog.findLibrary("sqlite-bundled").get())
            implementation(catalog.findLibrary("koin-core").get())
            implementation(catalog.findLibrary("kotlinx-coroutines-core").get())
            implementation(catalog.findLibrary("kotlinx-serialization-json").get())
        }
    }
}
