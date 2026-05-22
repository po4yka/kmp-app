package com.po4yka.app.shared.di

import com.po4yka.app.core.network.networkModule
import com.po4yka.app.core.settings.platformSettingsModule
import com.po4yka.app.core.settings.settingsModule
import com.po4yka.app.data.sample.SampleDao
import com.po4yka.app.feature.detail.impl.detailFeatureModule
import com.po4yka.app.feature.home.impl.homeFeatureModule
import com.po4yka.app.shared.BuildKonfig
import com.po4yka.app.shared.data.local.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

private val databaseModule: Module = module {
    single<SampleDao> { get<AppDatabase>().sampleDao() }
}

/**
 * Returns the full Koin module list for the app, EXCLUDING [platformModule].
 *
 * Callers must combine this with [platformModule] (which binds [AppDatabase] per platform)
 * when calling `startKoin`. On Android, also call `androidContext(...)` so [platformModule]
 * can resolve the Android `Context` for the Room builder.
 */
fun appModules(): List<Module> = listOf(
    networkModule(BuildKonfig.BASE_URL),
    settingsModule,
    platformSettingsModule(),
    databaseModule,
    homeFeatureModule,
    detailFeatureModule,
)
