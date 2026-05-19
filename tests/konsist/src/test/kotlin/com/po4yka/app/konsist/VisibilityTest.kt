package com.po4yka.app.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Backstop for the explicit API mode contract on public-surface modules.
 * The Kotlin compiler enforces this too, but Konsist catches it earlier
 * in IDE / pre-commit before a full ./gradlew assembleDebug is needed.
 */
class VisibilityTest {

    private val publicSurfaceModuleSegments = listOf(
        "/core/common/",
        "/core/ui/",
        "/core/navigation/",
        "/core/network/",
        "/core/settings/",
        "/feature/home/api/",
        "/feature/detail/api/",
    )

    @Test
    fun `top-level declarations in public-surface modules use explicit visibility`() {
        Konsist.scopeFromProject()
            .declarations(includeNested = false, includeLocal = false)
            .filter { decl ->
                publicSurfaceModuleSegments.any { decl.path.contains(it) }
            }
            .assertTrue(
                additionalMessage = "Public-surface modules require explicit `public` or `internal` " +
                    "on every top-level declaration (explicit API mode).",
            ) { decl ->
                decl.hasPublicOrInternalModifier || decl.hasPrivateModifier
            }
    }
}
