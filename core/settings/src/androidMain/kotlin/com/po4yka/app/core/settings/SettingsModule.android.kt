package com.po4yka.app.core.settings

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

public actual fun platformSettingsModule(): Module = module {
    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        )
    }
}
