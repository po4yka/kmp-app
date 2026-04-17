package com.po4yka.app.core.settings

import org.koin.core.module.Module
import org.koin.dsl.module

public val settingsModule: Module = module {
    single { AppSettings(get()) }
}

public expect fun platformSettingsModule(): Module
