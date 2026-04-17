package com.po4yka.app

import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.app.di.appModules
import com.po4yka.app.di.platformModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModules() + platformModule())
        }
    }
) {
    App()
}
