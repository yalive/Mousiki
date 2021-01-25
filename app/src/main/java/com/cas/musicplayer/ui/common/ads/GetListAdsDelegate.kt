package com.cas.musicplayer.ui.common.ads

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
import com.cas.common.resource.Resource
import com.mousiki.shared.domain.models.DisplayableItem
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
interface GetListAdsDelegate {
    suspend fun populateAdsIn(resource: MutableLiveData<Resource<List<DisplayableItem>>>)
    suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem>
}

class GetListAdsDelegateImp @Inject constructor(
    private val config: RemoteAppConfig,
    private val analytics: FirebaseAnalytics
) : GetListAdsDelegate {


    override suspend fun populateAdsIn(resource: MutableLiveData<Resource<List<DisplayableItem>>>) {
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
                analytics.logEvent(
                    KEY_EVENT_AD_TAKE_TOO_MUCH_TIME, bundleOf(
                        "duration" to duration
                    )
                )
            }
            return songsList
        }
        return items
    }
}

private val KEY_EVENT_AD_TAKE_TOO_MUCH_TIME = "list_ad_take_too_much_time"