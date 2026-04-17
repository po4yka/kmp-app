package com.po4yka.app

import androidx.compose.runtime.Composable
import com.po4yka.app.core.ui.theme.AppTheme
import com.po4yka.app.navigation.AppNavigation

@Composable
fun App() {
    AppTheme {
        AppNavigation()
    }
}
