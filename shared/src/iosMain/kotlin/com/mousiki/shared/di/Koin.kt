package com.mousiki.shared.di

import com.mousiki.shared.ads.FacebookAdsDelegate
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.config.RemoteConfigDelegate
import com.mousiki.shared.data.repository.LocalSongProvider
import com.mousiki.shared.data.repository.LocalTrackMapper
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.downloader.extractor.Extractor
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.home.model.HomeItem
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
        single { provider.listAdsDelegate } bind GetListAdsDelegate::class
        single { provider.strings } bind Strings::class
        single { provider.extractor } bind Extractor::class
        single {
            LocalTrackMapper(
                object : LocalSongProvider {
                    override suspend fun getSongById(id: Long): Song {
                        TODO("Not supported in iOS")
                    }
                }
            )
        }

        // TEMP
        single {
            object : FacebookAdsDelegate {
                override suspend fun getHomeFacebookNativeAds(count: Int): List<HomeItem.FBNativeAd> {
                    return emptyList()
                }
            }
        } bind FacebookAdsDelegate::class
    }
    initKoin(iOSModule)
}


interface IOSDependenciesProvider {
    val storage: StorageApi
    val remoteConfigDelegate: RemoteConfigDelegate
    val analytics: AnalyticsApi

    val playSongDelegate: PlaySongDelegate

    val listAdsDelegate: GetListAdsDelegate

    val strings: Strings

    val extractor: Extractor
}