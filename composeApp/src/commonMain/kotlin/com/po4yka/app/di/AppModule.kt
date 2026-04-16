package com.po4yka.app.di

import com.po4yka.app.data.local.AppDatabase
import com.po4yka.app.data.remote.createHttpClient
import com.po4yka.app.data.settings.AppSettings
import com.po4yka.app.ui.detail.DetailViewModel
import com.po4yka.app.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single { AppSettings(get()) }
    single { get<AppDatabase>().sampleDao() }

    viewModelOf(::HomeViewModel)
    viewModel { parameters -> DetailViewModel(parameters.get(), get()) }
}
