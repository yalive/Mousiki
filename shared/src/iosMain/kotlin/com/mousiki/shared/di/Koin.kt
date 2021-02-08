package com.mousiki.shared.di

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.config.RemoteConfigDelegate
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.StorageApi
import com.mousiki.shared.utils.Strings
import org.koin.dsl.bind
import org.koin.dsl.module

fun initIOSKoin(provider: IOSDependenciesProvider) {
    val iOSModule = module {
        single { provider.storage } bind StorageApi::class
        single { provider.analytics } bind AnalyticsApi::class
        single { RemoteAppConfig(provider.remoteConfigDelegate, get(), get(), get(), get()) }
        single { provider.playSongDelegate } bind PlaySongDelegate::class
        single { provider.strings } bind Strings::class
    }
    initKoin(iOSModule)
}


interface IOSDependenciesProvider {
    val storage: StorageApi
    val remoteConfigDelegate: RemoteConfigDelegate
    val analytics: AnalyticsApi

    val playSongDelegate: PlaySongDelegate

    val strings: Strings
}