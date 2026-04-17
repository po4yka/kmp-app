package com.po4yka.app.core.settings

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

public actual fun platformSettingsModule(): Module = module {
    single<Settings> { NSUserDefaultsSettings.Factory().create("app_prefs") }
}
