package com.cas.musicplayer.ui.common.ads

import com.cas.common.extensions.randomOrNull
import com.cas.musicplayer.MusicApp
import com.google.android.gms.ads.nativead.NativeAd
import com.mousiki.shared.data.config.RemoteAppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AdsManager {
    private val MAX_ADS = 10
    private val nativeAds = mutableListOf<NativeAd>()
    private var loadingAds = false

    private lateinit var appScope: CoroutineScope

    fun init(applicationScope: CoroutineScope, config: RemoteAppConfig) {
        appScope = applicationScope
        if (config.autoRefreshAds()) {
            appScope.launch { autoRefreshAds(config.autoRefreshAdsDuration()) }
        }
    }

    suspend fun getAds(count: Int): List<NativeAd> {
        MusicApp.get().awaitAdmobSdkInitialized()
        if (nativeAds.isEmpty()) loadAds()
        val randomList = nativeAds.shuffled()
        return if (count > randomList.size) randomList
        else randomList.subList(0, count)
    }

    suspend fun getAd(): NativeAd? {
        MusicApp.get().awaitAdmobSdkInitialized()
        if (nativeAds.isEmpty()) loadAds()
        return nativeAds.randomOrNull()
    }

    private suspend fun loadAds() {
        if (loadingAds) return
        loadingAds = true
        appScope.launch(Dispatchers.Main) {
            val ads = loadMultipleNativeAdWithMediation(MAX_ADS)
            if (ads.isNotEmpty()) nativeAds.clear()
            nativeAds.addAll(ads)
        }.join()
        loadingAds = false
    }

    private suspend fun autoRefreshAds(duration: Int) {
        while (true) {
            // Wait "duration" min
            delay(duration.toLong() * 60 * 1000)
            loadAds()
        }
    }

    suspend fun awaitLoadAds() {
        if (!loadingAds) return
        while (loadingAds) {
            delay(100)
        }
    }
}