pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
                includeGroupByRegex("androidx\\..*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
                includeGroupByRegex("androidx\\..*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "kmp-app"
include(":shared")
include(":androidApp")

// Core infrastructure modules (framework glue; depend only on :core:common + external libs)
include(":core:common")
include(":core:ui")
include(":core:navigation")
include(":core:network")
include(":core:settings")

// Data modules (business domains; entities, DAOs, repositories)
include(":data:sample")

// Feature modules (product areas; api/impl split per feature)
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:detail:api")
include(":feature:detail:impl")

// Test-only modules
include(":tests:konsist")
