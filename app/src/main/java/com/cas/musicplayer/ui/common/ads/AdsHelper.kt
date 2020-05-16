package com.cas.musicplayer.ui.common.ads

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.coroutines.*
import kotlin.coroutines.resume

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/2/20.
 ***************************************
 */

suspend fun loadMultipleNativeAds(count: Int) =
    suspendCancellableCoroutine<List<UnifiedNativeAd>> { continuation ->
        val ads = mutableListOf<UnifiedNativeAd>()
        lateinit var adLoader: AdLoader
        adLoader = AdLoader.Builder(
            MusicApp.get(), MusicApp.get().getString(R.string.admob_native_id)
        ).forUnifiedNativeAd {
            ads.add(it)
            if (!adLoader.isLoading && continuation.isActive) {
                continuation.resume(ads)
            }
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                if ((!adLoader.isLoading || count == 1) && continuation.isActive) {
                    continuation.resume(ads)
                }
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        adLoader.loadAds(AdRequest.Builder().build(), count)
    }

suspend fun loadSingleNativeAd() =
    suspendCancellableCoroutine<UnifiedNativeAd?> { continuation ->
        val adLoader: AdLoader = AdLoader.Builder(
            MusicApp.get(), MusicApp.get().getString(R.string.admob_native_id)
        ).forUnifiedNativeAd {
            if (continuation.isActive) {
                continuation.resume(it)
            }
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

suspend fun loadMultipleNativeAdWithMediation(count: Int): List<UnifiedNativeAd> =
    withContext(Dispatchers.IO) {
        val listAsync = mutableListOf<Deferred<UnifiedNativeAd?>>()
        for (i in 0 until count) {
            listAsync.add(async { loadSingleNativeAd() })
        }
        return@withContext listAsync.awaitAll().filterNotNull()
    }