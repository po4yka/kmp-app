import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Screenshot-testing convention. Applies the Roborazzi plugin and wires the
// Roborazzi + Robolectric dependencies into the `androidUnitTest` source set so
// any KMP module can adopt Compose screenshot tests with a single
// `id("kmp-app.roborazzi")` line.
//
// CAVEAT (AGP 9.x `com.android.kotlin.multiplatform.library`): the KMP Android
// library plugin does not yet expose an Android unit-test Kotlin compilation,
// so `recordRoborazziDebug` / `verifyRoborazziDebug` are NOT generated for the
// Android target. Roborazzi still generates iOS snapshot tasks
// (`recordRoborazziIosSimulatorArm64`). Track the AGP issue linked in
// AGENTS.md "Screenshot Testing" for when Android unit-test support lands.
//
// The Compose `ui-test-junit4` dependency is Compose-version-coupled and stays
// in the consuming module's build script — it is not catalog-managed.
plugins {
    id("io.github.takahirom.roborazzi")
}

// The `androidUnitTest` source set is registered lazily by the AGP KMP Android
// target, so configure it via `sourceSets.matching` rather than a direct
// `named(...)` lookup (which fails at convention-plugin apply time).
extensions.configure<KotlinMultiplatformExtension> {
    sourceSets.matching { it.name == "androidUnitTest" }.configureEach {
        dependencies {
            implementation(kotlin("test"))
            implementation(catalog.findLibrary("roborazzi").get())
            implementation(catalog.findLibrary("roborazzi-compose").get())
            implementation(catalog.findLibrary("roborazzi-junit").get())
            implementation(catalog.findLibrary("robolectric").get())
        }
    }
}
