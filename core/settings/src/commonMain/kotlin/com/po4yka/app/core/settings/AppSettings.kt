package com.po4yka.app.core.settings

import com.russhwolf.settings.Settings

public class AppSettings(private val settings: Settings) {

    public var username: String
        get() = settings.getString(KEY_USERNAME, "")
        set(value) = settings.putString(KEY_USERNAME, value)

    public var isOnboardingComplete: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING, false)
        set(value) = settings.putBoolean(KEY_ONBOARDING, value)

    public fun clear() {
        settings.clear()
    }

    private companion object {
        const val KEY_USERNAME = "username"
        const val KEY_ONBOARDING = "onboarding_complete"
    }
}
