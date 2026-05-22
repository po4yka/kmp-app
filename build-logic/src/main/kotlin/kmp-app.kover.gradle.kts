plugins {
    id("org.jetbrains.kotlinx.kover")
}

// Code coverage convention. Applied to every production module; the root build
// aggregates the per-module reports via `kover(project(...))` dependencies.
// Generated code is excluded so coverage reflects hand-written sources only.
kover {
    reports {
        filters {
            excludes {
                classes(
                    // Koin compiler-plugin generated DI modules.
                    "*_KoinModule*",
                    // Compose compiler generated singleton holders for stable lambdas.
                    "*ComposableSingletons*",
                    // Room compiler generated DAO/database implementations.
                    "*_Impl",
                    // BuildKonfig generated build-time config.
                    "*.BuildKonfig",
                )
            }
        }
    }
}
