package com.po4yka.app.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider
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
        val files = Konsist.scopeFromProject()
            .files
            .filter { file ->
                // Only main source sets — Kotlin's explicit API mode doesn't apply to
                // commonTest / androidUnitTest / iosTest. The folder convention is
                // src/<name>Main/kotlin for main, src/<name>Test/kotlin for tests.
                file.path.contains("Main/kotlin/") &&
                    publicSurfaceModuleSegments.any { file.path.contains(it) }
            }

        val message = "Public-surface modules require explicit `public` or `internal` " +
            "on every top-level declaration (explicit API mode)."

        // KoBaseDeclaration does not carry the visibility-modifier provider, so iterate by
        // concrete typed accessor — each return type implements KoVisibilityModifierProvider.
        files.flatMap { it.classes() }
            .assertTrue(additionalMessage = message, function = ::hasExplicitVisibility)
        files.flatMap { it.interfaces() }
            .assertTrue(additionalMessage = message, function = ::hasExplicitVisibility)
        files.flatMap { it.objects() }
            .assertTrue(additionalMessage = message, function = ::hasExplicitVisibility)
        files.flatMap { it.functions(includeNested = false, includeLocal = false) }
            .assertTrue(additionalMessage = message, function = ::hasExplicitVisibility)
        files.flatMap { it.properties(includeNested = false) }
            .assertTrue(additionalMessage = message, function = ::hasExplicitVisibility)
    }

    private fun hasExplicitVisibility(decl: KoVisibilityModifierProvider): Boolean =
        decl.hasPublicModifier || decl.hasInternalModifier ||
            decl.hasPrivateModifier || decl.hasProtectedModifier
}
