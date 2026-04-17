package com.po4yka.app

import android.app.Application
import com.po4yka.app.di.appModules
import com.po4yka.app.di.platformModule
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            modules(appModules() + platformModule())
        }
    }
}
