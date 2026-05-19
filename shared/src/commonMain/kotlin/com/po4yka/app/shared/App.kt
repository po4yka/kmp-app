package com.po4yka.app.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.po4yka.app.core.ui.theme.AppTheme
import com.po4yka.app.shared.navigation.AppNavigation
import com.po4yka.app.shared.observability.initSentry

@Composable
fun App() {
    remember { initSentry(); Unit }
    AppTheme {
        AppNavigation()
    }
}
