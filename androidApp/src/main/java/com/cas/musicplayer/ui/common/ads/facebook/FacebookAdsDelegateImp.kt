package com.cas.musicplayer.ui.common.ads.facebook

import com.mousiki.shared.ads.FacebookAdsDelegate
import com.mousiki.shared.ui.home.model.HomeItem

class FacebookAdsDelegateImp : FacebookAdsDelegate {

    override suspend fun getHomeFacebookNativeAds(count: Int): List<HomeItem.FBNativeAd> {
        val facebookAds = getFbNativeAds(count)
        return facebookAds.map {
            HomeItem.FBNativeAd(FacebookNativeAd(it))
        }
    }
}

