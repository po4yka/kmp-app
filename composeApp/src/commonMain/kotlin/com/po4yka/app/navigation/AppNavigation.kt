package com.po4yka.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.po4yka.app.feature.detail.api.DetailRoute
import com.po4yka.app.feature.detail.impl.detailEntries
import com.po4yka.app.feature.home.api.HomeRoute
import com.po4yka.app.feature.home.impl.homeEntries
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

private val savedStateConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeRoute::class, HomeRoute.serializer())
            subclass(DetailRoute::class, DetailRoute.serializer())
        }
    }
}

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(savedStateConfig, HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            homeEntries(onOpenDetail = { itemId -> backStack.add(DetailRoute(itemId)) })
            detailEntries(onBack = { backStack.removeLastOrNull() })
        }
    )
}
