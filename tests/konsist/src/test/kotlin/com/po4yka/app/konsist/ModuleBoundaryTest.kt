package com.po4yka.app.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withInternalModifier
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withPublicOrDefaultModifier
import com.lemonappdev.konsist.api.verify.assertEmpty
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Architecture tests enforcing the module-boundary table in AGENTS.md.
 * These are review blockers — failures here mean a forbidden edge has been added
 * to the module graph.
 */
class ModuleBoundaryTest {

    @Test
    fun `no feature impl depends on another feature impl`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.path.contains("/feature/") && it.path.contains("/impl/") }
            .flatMap { it.imports }
            .filter {
                val n = it.name
                n.startsWith("com.po4yka.app.feature.") && n.contains(".impl.")
            }
            .assertEmpty(
                additionalMessage = "Feature :impl modules must not import another feature :impl. " +
                    "Cross-feature navigation goes through callbacks wired in :shared.",
            )
    }

    @Test
    fun `room entities and DAOs live only in data modules`() {
        Konsist.scopeFromProject()
            .classes(includeNested = true)
            .filter { clazz ->
                clazz.annotations.any { it.name == "Entity" || it.name == "Dao" }
            }
            .assertTrue(additionalMessage = "@Entity / @Dao must live under :data:<domain>") { clazz ->
                clazz.resideInPackage("com.po4yka.app.data..")
            }
    }

    @Test
    fun `routes implement Route and are Serializable`() {
        Konsist.scopeFromProject()
            .classes(includeNested = true)
            .filter { it.name.endsWith("Route") && it.resideInPackage("..api..") }
            .assertTrue(
                additionalMessage = "Route classes must be @Serializable and implement core.navigation.Route",
            ) { route ->
                val isSerializable = route.annotations.any { it.name == "Serializable" }
                val implementsRoute = route.parents().any { it.name == "Route" }
                isSerializable && implementsRoute
            }
    }

    @Test
    fun `viewmodels live in feature impl packages`() {
        Konsist.scopeFromProject()
            .classes(includeNested = true)
            .filter { it.name.endsWith("ViewModel") }
            .assertTrue(
                additionalMessage = "ViewModels must live in com.po4yka.app.feature.<name>.impl",
            ) { vm ->
                vm.resideInPackage("com.po4yka.app.feature..impl..")
            }
    }

    @Test
    fun `core common has no module dependencies`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.path.contains("/core/common/") }
            .flatMap { it.imports }
            .filter { imp ->
                imp.name.startsWith("com.po4yka.app.") &&
                    !imp.name.startsWith("com.po4yka.app.core.common")
            }
            .assertEmpty(
                additionalMessage = ":core:common must not depend on any other project module.",
            )
    }

    @Test
    fun `feature api modules do not depend on data or impl`() {
        Konsist.scopeFromProject()
            .files
            .filter { it.path.contains("/feature/") && it.path.contains("/api/") }
            .flatMap { it.imports }
            .filter { imp ->
                imp.name.startsWith("com.po4yka.app.data.") ||
                    (imp.name.contains(".feature.") && imp.name.contains(".impl."))
            }
            .assertEmpty(
                additionalMessage = ":feature:*:api may only depend on :core:* and external libs.",
            )
    }
}
