package com.mousiki.shared.ads

import com.mousiki.shared.ui.home.model.HomeItem

interface FacebookAdsDelegate {
    suspend fun getHomeFacebookNativeAds(count: Int): List<HomeItem.FBNativeAd>
}