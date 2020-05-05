package com.cas.musicplayer.ui.common.ads

import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.config.RemoteAppConfig
import javax.inject.Inject
import kotlin.math.min

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
interface GetListAdsDelegate {
    suspend fun insertAds(items: MutableLiveData<Resource<List<DisplayableItem>>>)
    suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem>
}

class GetListAdsDelegateImp @Inject constructor(
    private val config: RemoteAppConfig
) : GetListAdsDelegate {

    override suspend fun insertAds(items: MutableLiveData<Resource<List<DisplayableItem>>>) {
        val displayedItems = items.valueOrNull() ?: return
        val size = displayedItems.size
        if (size <= 2) return
        val offset = config.getOffsetListAds()
        val adsCount = min(5, size / offset)
        if (adsCount > 0) {
            val ads = loadAds(adsCount).map { AdsItem(it) }
            val songsList = displayedItems.toMutableList()
            var index = offset
            ads.forEach { adsItem ->
                songsList.add(index, adsItem)
                index += offset + 1
            }
            items.value = Resource.Success(songsList)
        }
    }

    override suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem> {
        val size = items.size
        if (size <= 2) return items
        val offset = config.getOffsetListAds()
        val adsCount = min(5, size / offset)
        if (adsCount > 0) {
            val ads = loadAds(adsCount).map { AdsItem(it) }
            val songsList = items.toMutableList()
            var index = offset
            ads.forEach { adsItem ->
                songsList.add(index, adsItem)
                index += offset + 1
            }
            return songsList
        }
        return emptyList()
    }
}