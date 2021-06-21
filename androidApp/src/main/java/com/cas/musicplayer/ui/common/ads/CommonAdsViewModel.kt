package com.cas.musicplayer.ui.common.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.mousiki.shared.data.config.RemoteAppConfig
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/16/20.
 ***************************************
 */
class CommonAdsViewModel constructor(
    private val appConfig: RemoteAppConfig
) : ViewModel() {

    var exitAd: NativeAd? = null
        private set
        get() {
            if (field == null) {
                loadExitAd()
            }
            return field
        }

    var trackOptionsAd: NativeAd? = null
        private set
        get() {
            if (field == null) {
                loadTrackOptionsAd()
            }
            return field
        }

    init {
        loadExitAd()
        loadTrackOptionsAd()
    }

    fun loadExitAd() = viewModelScope.launch {
        AdsManager.getAd()?.also { ad:NativeAd ->
            exitAd = ad
        }
    }

    fun loadTrackOptionsAd() = viewModelScope.launch {
        if (!appConfig.showNativeAdTrackOptions()) return@launch
        AdsManager.getAd()?.also { ad:NativeAd ->
            trackOptionsAd = ad
        }
    }
}