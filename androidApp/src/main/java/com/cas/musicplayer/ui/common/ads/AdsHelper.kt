package com.cas.musicplayer.ui.common.ads

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_BOTTOM_RIGHT
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.*
import kotlin.coroutines.resume

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/2/20.
 ***************************************
 */

suspend fun loadMultipleNativeAdWithMediation(count: Int): List<NativeAd> =
    withContext(Dispatchers.IO) {
        val listAsync = mutableListOf<Deferred<NativeAd?>>()
        for (i in 0 until count) {
            listAsync.add(async { loadSingleNativeAd() })
        }
        return@withContext listAsync.awaitAll().filterNotNull()
    }

private suspend fun loadSingleNativeAd() =
    suspendCancellableCoroutine<NativeAd?> { continuation ->
        val adLoader: AdLoader = AdLoader.Builder(
            MusicApp.get(), MusicApp.get().getString(R.string.admob_native_id)
        ).forNativeAd { ad ->
            if (continuation.isActive) {
                continuation.resume(ad)
            } else {
                ad.destroy()
            }
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder().setAdChoicesPlacement(ADCHOICES_BOTTOM_RIGHT).build()
        ).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }