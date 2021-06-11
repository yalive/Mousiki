package com.cas.musicplayer.ui.common.ads.facebook

import com.cas.musicplayer.MusicApp
import com.facebook.ads.AdError
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdsManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun getFbNativeAds(count: Int): List<NativeAd> = suspendCancellableCoroutine { cont ->
    val adsManager = NativeAdsManager(MusicApp.get(), "134138665415635_145411384288363", count)
    val listener = object : NativeAdsManager.Listener {
        override fun onAdsLoaded() {
            val adsCount = adsManager.uniqueNativeAdCount
            val ads = mutableListOf<NativeAd>()
            repeat(count) {
                ads.add(adsManager.nextNativeAd())
            }

            if (cont.isActive) {
                cont.resume(ads)
            }
        }

        override fun onAdError(p0: AdError?) {
            if (cont.isActive) {
                cont.resume(emptyList())
            }
        }
    }
    adsManager.setListener(listener)
    adsManager.loadAds()
}