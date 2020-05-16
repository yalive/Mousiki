package com.cas.musicplayer.ui.common.ads

import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.config.RemoteAppConfig
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
    private val config: RemoteAppConfig
) : GetListAdsDelegate {


    override suspend fun populateAdsIn(resource: MutableLiveData<Resource<List<DisplayableItem>>>) {
        val items = resource.valueOrNull() ?: return
        if (items.isEmpty()) return
        val itemsWithAd = insertAdsIn(items)
        resource.value = Resource.Success(itemsWithAd)
    }

    override suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem> {
        val size = items.size
        val offset = config.getOffsetListAds()
        val adsCount = size / offset
        if (adsCount > 0) {
            val ads = loadMultipleNativeAdWithMediation(adsCount).map {
                AdsItem(it)
            }
            val songsList = items.toMutableList()
            var index = offset
            ads.forEach { adsItem ->
                songsList.add(index, adsItem)
                index += offset + 1
            }
            return songsList
        }
        return items
    }
}