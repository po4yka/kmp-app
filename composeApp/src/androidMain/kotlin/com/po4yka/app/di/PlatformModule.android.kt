package com.po4yka.app.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.po4yka.app.data.local.AppDatabase
import com.po4yka.app.getDatabaseBuilder
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<Settings> {
        SharedPreferencesSettings(
            com.po4yka.app.appContext.getSharedPreferences("app_prefs", 0)
        )
    }
}
