package com.po4yka.app.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.po4yka.app.data.local.AppDatabase
import com.po4yka.app.getDatabaseBuilder
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<Settings> { NSUserDefaultsSettings.Factory().create("app_prefs") }
}
