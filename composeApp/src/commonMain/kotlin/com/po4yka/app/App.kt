package com.po4yka.app

import androidx.compose.runtime.Composable
import com.po4yka.app.navigation.AppNavigation
import com.po4yka.app.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        AppNavigation()
    }
}
