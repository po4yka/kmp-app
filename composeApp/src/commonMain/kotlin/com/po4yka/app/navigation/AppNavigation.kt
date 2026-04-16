package com.po4yka.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.subclassesOfSealed
import com.po4yka.app.ui.home.HomeScreen
import com.po4yka.app.ui.detail.DetailScreen

@OptIn(ExperimentalSerializationApi::class)
private val savedStateConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<Route>()
        }
    }
}

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(savedStateConfig, Route.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeScreen(
                    onItemClick = { itemId ->
                        backStack.add(Route.Detail(itemId))
                    }
                )
            }

            entry<Route.Detail> { key ->
                DetailScreen(
                    itemId = key.itemId,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
