package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.config.RemoteConfigDelegate
import com.mousiki.shared.utils.StorageApi
import org.koin.dsl.bind
import org.koin.dsl.module

fun initIOSKoin(provider: IOSDependenciesProvider) {
    val iOSModule = module {
        single { provider.storage } bind StorageApi::class
        single { RemoteAppConfig(provider.remoteConfigDelegate, get(), get()) }
    }
    initKoin(iOSModule)
}


interface IOSDependenciesProvider {
    val storage: StorageApi
    val remoteConfigDelegate: RemoteConfigDelegate
}