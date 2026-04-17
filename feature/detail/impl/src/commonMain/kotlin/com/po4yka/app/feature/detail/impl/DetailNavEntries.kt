package com.po4yka.app.feature.detail.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.po4yka.app.feature.detail.api.DetailRoute

fun EntryProviderScope<NavKey>.detailEntries(
    onBack: () -> Unit,
) {
    entry<DetailRoute> { key ->
        DetailScreen(itemId = key.itemId, onBack = onBack)
    }
}
