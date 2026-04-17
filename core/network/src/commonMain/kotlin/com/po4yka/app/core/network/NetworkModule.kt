package com.po4yka.app.core.network

import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

public fun networkModule(baseUrl: String): Module = module {
    single<HttpClient> { createHttpClient(baseUrl) }
}
