package com.cas.musicplayer.ui.common.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.data.config.RemoteAppConfig
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/16/20.
 ***************************************
 */
class CommonAdsViewModel @Inject constructor(
    private val appConfig: RemoteAppConfig
) : ViewModel() {

    var exitAd: UnifiedNativeAd? = null
        private set
        get() {
            if (field == null) {
                loadExitAd()
            }
            return field
        }

    var trackOptionsAd: UnifiedNativeAd? = null
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
        AdsManager.getAd()?.also { ad ->
            exitAd = ad
        }
    }

    fun loadTrackOptionsAd() = viewModelScope.launch {
        if (!appConfig.showNativeAdTrackOptions()) return@launch
        AdsManager.getAd()?.also { ad ->
            trackOptionsAd = ad
        }
    }
}