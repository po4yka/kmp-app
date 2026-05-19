package com.po4yka.app

import android.app.Application
import com.po4yka.app.shared.di.appModules
import com.po4yka.app.shared.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModules() + platformModule())
        }
    }
}
