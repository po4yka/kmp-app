plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.kotlin.multiplatform)
    implementation(libs.plugin.android.kmp.library)
    implementation(libs.plugin.android.application)
    implementation(libs.plugin.compose.multiplatform)
    implementation(libs.plugin.compose.compiler)
}
