package com.cas.musicplayer.ui.common.ads

import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.valueOrNull
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.logEvent
import kotlinx.coroutines.flow.MutableStateFlow

class GetListAdsDelegateImp(
    private val config: RemoteAppConfig,
    private val analytics: AnalyticsApi
) : GetListAdsDelegate {


    override suspend fun populateAdsIn(resource: MutableStateFlow<Resource<List<DisplayableItem>>?>) {
        val items = resource.valueOrNull() ?: return
        if (items.isEmpty()) return
        val itemsWithAd = insertAdsIn(items)
        resource.value = Resource.Success(itemsWithAd)
    }

    override suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem> {
        val startTime = System.currentTimeMillis()
        val size = items.size
        val offset = config.getOffsetListAds()
        val adsCount = size / offset
        if (adsCount > 0) {
            val ads = AdsManager.getAds(adsCount).map {
                AdsItem(it)
            }
            val songsList = items.toMutableList()
            var index = offset
            ads.forEach { adsItem ->
                songsList.add(index, adsItem)
                index += offset + 1
            }
            val duration = System.currentTimeMillis() - startTime
            if (duration > 5 * 1000) {
                analytics.logEvent(KEY_EVENT_AD_TAKE_TOO_MUCH_TIME, "duration" to duration)
            }
            return songsList
        }
        return items
    }

    override suspend fun getNativeAds(count: Int): List<DisplayableItem> {
        return AdsManager.getAds(count).map {
            AdsItem(it)
        }
    }

    override suspend fun getOrAwaitNativeAds(count: Int): List<DisplayableItem> {
        awaitLoadAds()
        return getNativeAds(count)
    }

    override suspend fun awaitLoadAds() {
        AdsManager.awaitLoadAds()
    }
}

private val KEY_EVENT_AD_TAKE_TOO_MUCH_TIME = "list_ad_take_too_much_time"