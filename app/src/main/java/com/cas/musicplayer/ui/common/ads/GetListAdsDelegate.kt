package com.cas.musicplayer.ui.common.ads

import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.config.RemoteAppConfig
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
interface GetListAdsDelegate {
    suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem>
}

class GetListAdsDelegateImp @Inject constructor(
    private val config: RemoteAppConfig
) : GetListAdsDelegate {


    override suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem> {
        val size = items.size
        if (size <= 2) return items
        val offset = config.getOffsetListAds()
        val adsCount = size / offset
        if (adsCount > 0) {
            val pagesCount = adsCount / 5
            val rest = adsCount % 5

            val allAds = mutableListOf<AdsItem>()
            for (i in 0 until pagesCount) {
                allAds.addAll(loadAds(5).map { AdsItem(it) })
            }
            if (rest > 0) {
                allAds.addAll(loadAds(rest).map { AdsItem(it) })
            }
            val songsList = items.toMutableList()
            var index = offset
            allAds.forEach { adsItem ->
                songsList.add(index, adsItem)
                index += offset + 1
            }
            return songsList
        }
        return items
    }
}