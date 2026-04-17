package com.po4yka.app.feature.home.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.po4yka.app.feature.home.api.HomeRoute

fun EntryProviderScope<NavKey>.homeEntries(
    onOpenDetail: (Long) -> Unit,
) {
    entry<HomeRoute> {
        HomeScreen(onItemClick = onOpenDetail)
    }
}
