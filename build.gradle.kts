plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    source.setFrom(
        "$rootDir/composeApp/src/commonMain/kotlin",
        "$rootDir/composeApp/src/androidMain/kotlin",
        "$rootDir/composeApp/src/iosMain/kotlin",
        "$rootDir/androidApp/src/main/kotlin",
    )
}
