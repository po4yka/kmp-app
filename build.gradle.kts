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
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.gradle.doctor)
}

// Gradle Doctor: surfaces JDK mismatches, cache misses, and slow tasks.
// Tune thresholds rather than disabling once a baseline is established.
doctor {
    javaHome {
        ensureJavaHomeMatches.set(false) // CI can set JAVA_HOME explicitly
        ensureJavaHomeIsSet.set(true)
    }
    // Warn (don't fail) on negative-savings cached tasks during the first runs.
    negativeAvoidanceThreshold.set(500)
}

// Dependency Analysis: keeps api/implementation discipline honest and
// flags unused/transitive-leaked deps across the multi-module graph.
// Run: `./gradlew buildHealth`
dependencyAnalysis {
    issues {
        all {
            // The module-boundary table in AGENTS.md is the source of truth;
            // surface findings as warnings until the team agrees on hard-fails.
            onAny { severity("warn") }
            onUnusedDependencies { severity("warn") }
            onIncorrectConfiguration { severity("warn") }
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    source.setFrom(
        "$rootDir/shared/src/commonMain/kotlin",
        "$rootDir/shared/src/androidMain/kotlin",
        "$rootDir/shared/src/iosMain/kotlin",
        "$rootDir/androidApp/src/main/kotlin",
        "$rootDir/core/common/src/commonMain/kotlin",
        "$rootDir/core/ui/src/commonMain/kotlin",
        "$rootDir/core/navigation/src/commonMain/kotlin",
        "$rootDir/core/network/src/commonMain/kotlin",
        "$rootDir/core/network/src/androidMain/kotlin",
        "$rootDir/core/network/src/iosMain/kotlin",
        "$rootDir/core/settings/src/commonMain/kotlin",
        "$rootDir/core/settings/src/androidMain/kotlin",
        "$rootDir/core/settings/src/iosMain/kotlin",
        "$rootDir/data/sample/src/commonMain/kotlin",
        "$rootDir/feature/home/api/src/commonMain/kotlin",
        "$rootDir/feature/home/impl/src/commonMain/kotlin",
        "$rootDir/feature/detail/api/src/commonMain/kotlin",
        "$rootDir/feature/detail/impl/src/commonMain/kotlin",
    )
}
