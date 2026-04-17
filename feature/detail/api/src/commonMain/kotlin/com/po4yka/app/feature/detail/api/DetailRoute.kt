package com.po4yka.app.feature.detail.api

import com.po4yka.app.core.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
public data class DetailRoute(public val itemId: Long) : Route
