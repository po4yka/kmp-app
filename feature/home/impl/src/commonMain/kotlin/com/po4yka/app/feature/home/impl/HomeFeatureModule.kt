package com.po4yka.app.feature.home.impl

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeFeatureModule: Module = module {
    viewModelOf(::HomeViewModel)
}
