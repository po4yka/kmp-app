package com.po4yka.app

import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.app.di.appModule
import com.po4yka.app.di.platformModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModule, platformModule())
        }
    }
) {
    App()
}
