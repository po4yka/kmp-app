package com.po4yka.app.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.app.shared.di.appModules
import com.po4yka.app.shared.di.platformModule
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
