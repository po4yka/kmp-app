package com.po4yka.app.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Marker for typed Navigation 3 destinations.
 *
 * Each feature declares its own `@Serializable` subtype in its `:api` module.
 * The app shell registers each subtype in the polymorphic `SavedStateConfiguration`.
 * Not `sealed` because Kotlin `sealed` cannot span Gradle modules.
 */
public interface Route : NavKey
