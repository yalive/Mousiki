package com.cas.musicplayer.ui.common.ads

import com.cas.common.extensions.randomOrNull
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AdsManager {
    private val TAG = "AdsManager"
    private val MAX_ADS = 10
    private val nativeAds = mutableListOf<UnifiedNativeAd>()
    private var loadingAds = false

    private lateinit var appScope: CoroutineScope

    fun init(applicationScope: CoroutineScope) {
        appScope = applicationScope
        //appScope.launch { autoRefreshAds() }
    }

    suspend fun getAds(count: Int): List<UnifiedNativeAd> {
        if (nativeAds.isEmpty()) loadAds()
        val randomList = nativeAds.shuffled()
        return if (count > randomList.size) randomList
        else randomList.subList(0, count)
    }

    suspend fun getAd(): UnifiedNativeAd? {
        if (nativeAds.isEmpty()) loadAds()
        return nativeAds.randomOrNull()
    }

    private suspend fun loadAds() {
        if (loadingAds) return
        loadingAds = true
        appScope.launch {
            val ads = loadMultipleNativeAdWithMediation(MAX_ADS)
            if (ads.isNotEmpty()) nativeAds.clear()
            nativeAds.addAll(ads)
        }.join()
        loadingAds = false
    }

    private suspend fun autoRefreshAds() {
        while (true) {
            // Wait 15min
            delay(15 * 60 * 1000)
            loadAds()
        }
    }
}