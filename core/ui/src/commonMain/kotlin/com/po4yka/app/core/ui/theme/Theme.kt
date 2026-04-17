package com.po4yka.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.po4yka.industrialdesign.IndustrialTheme

@Composable
public fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    IndustrialTheme(darkTheme = darkTheme, content = content)
}
