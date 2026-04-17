package com.po4yka.app.feature.detail.impl

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailFeatureModule: Module = module {
    viewModel { parameters -> DetailViewModel(parameters.get(), get()) }
}
