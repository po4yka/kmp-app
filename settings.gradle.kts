pluginManagement {
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
        mavenLocal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "kmp-app"
include(":composeApp")
include(":androidApp")
