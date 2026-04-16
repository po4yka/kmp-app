package com.po4yka.app.data.settings

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {

    var username: String
        get() = settings.getString(KEY_USERNAME, "")
        set(value) = settings.putString(KEY_USERNAME, value)

    var isOnboardingComplete: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING, false)
        set(value) = settings.putBoolean(KEY_ONBOARDING, value)

    fun clear() {
        settings.clear()
    }

    private companion object {
        const val KEY_USERNAME = "username"
        const val KEY_ONBOARDING = "onboarding_complete"
    }
}
