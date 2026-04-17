package com.po4yka.app.core.settings

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppSettingsTest {

    private fun createSettings() = AppSettings(MapSettings())

    @Test
    fun defaultUsernameIsEmpty() {
        val settings = createSettings()
        assertEquals("", settings.username)
    }

    @Test
    fun setAndGetUsername() {
        val settings = createSettings()
        settings.username = "testUser"
        assertEquals("testUser", settings.username)
    }

    @Test
    fun defaultOnboardingIsFalse() {
        val settings = createSettings()
        assertFalse(settings.isOnboardingComplete)
    }

    @Test
    fun setAndGetOnboarding() {
        val settings = createSettings()
        settings.isOnboardingComplete = true
        assertTrue(settings.isOnboardingComplete)
    }

    @Test
    fun clearResetsAllValues() {
        val settings = createSettings()
        settings.username = "testUser"
        settings.isOnboardingComplete = true

        settings.clear()

        assertEquals("", settings.username)
        assertFalse(settings.isOnboardingComplete)
    }
}
