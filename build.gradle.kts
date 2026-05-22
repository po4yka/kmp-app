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
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    alias(libs.plugins.modulegraph)
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

// Spotless: enforces ktlint formatting across every module. Formatting-only
// (no license header). Run `./gradlew spotlessApply` to fix, `spotlessCheck` to verify.
// ktlint reads `.editorconfig` (intellij_idea code style) for its rules.
val ktlintVersion = libs.versions.ktlint.get()
// @Composable functions are intentionally PascalCase (Compose convention) and
// detekt already owns naming mechanics here — disable ktlint's overlapping rule.
val ktlintRuleOverrides = mapOf("ktlint_standard_function-naming" to "disabled")
subprojects {
    apply(plugin = "com.diffplug.spotless")
    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            ktlint(ktlintVersion).editorConfigOverride(ktlintRuleOverrides)
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(ktlintVersion)
        }
    }
}

// Kover: aggregates per-module coverage into one merged report.
// Run `./gradlew koverXmlReport` or `koverHtmlReport`. Exclusions for generated
// code are configured per module via the `kmp-app.kover` convention plugin.
dependencies {
    kover(project(":shared"))
    kover(project(":androidApp"))
    kover(project(":core:common"))
    kover(project(":core:ui"))
    kover(project(":core:navigation"))
    kover(project(":core:network"))
    kover(project(":core:settings"))
    kover(project(":data:sample"))
    kover(project(":feature:home:api"))
    kover(project(":feature:home:impl"))
    kover(project(":feature:detail:api"))
    kover(project(":feature:detail:impl"))
}

// Module graph: renders the inter-module dependency graph as a Mermaid diagram.
// Run `./gradlew createModuleGraph` to regenerate docs/MODULE_GRAPH.md.
// `:androidApp` is the single graph root (it transitively reaches every
// production module); rooting there drops the noisy root-project + test edges.
moduleGraphConfig {
    readmePath.set("$rootDir/docs/MODULE_GRAPH.md")
    heading.set("## Module Graph")
    rootModulesRegex.set(":androidApp")
    // Drop test/kover edges, plus the `baselineProfile` consumer configuration
    // (:androidApp -> :baselineProfile) which is build tooling, not a runtime dep.
    excludedConfigurationsRegex.set(".*([tT]est|[kK]over|[bB]aselineProfile).*")
}
