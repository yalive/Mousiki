package com.cas.musicplayer.ui.popular

import androidx.recyclerview.widget.DiffUtil
import com.cas.delegatedadapter.DisplayableItem
import com.cas.delegatedadapter.LoadingItem
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem

class SongsDiffUtil(
    private val oldList: List<DisplayableItem>,
    private val newList: List<DisplayableItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is DisplayedVideoItem && newItem is DisplayedVideoItem) {
            return oldItem.track.youtubeId == newItem.track.youtubeId
        }

        if (oldItem is AdsItem && newItem is AdsItem) {
            return oldItem.ad.headline == newItem.ad.headline
                    && oldItem.ad.body == newItem.ad.body
                    && oldItem.ad.callToAction == newItem.ad.callToAction
        }

        if (oldItem is SongsHeaderItem && newItem is SongsHeaderItem) {
            return true
        }

        if (oldItem is LoadingItem && newItem is LoadingItem) {
            return true
        }
        return false
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is DisplayedVideoItem && newItem is DisplayedVideoItem) {
            return oldItem.track.youtubeId == newItem.track.youtubeId
                    && oldItem.isPlaying == newItem.isPlaying
                    && oldItem.songTitle == newItem.songTitle
        }

        if (oldItem is AdsItem && newItem is AdsItem) {
            return oldItem.ad.headline == newItem.ad.headline
                    && oldItem.ad.body == newItem.ad.body
                    && oldItem.ad.callToAction == newItem.ad.callToAction
                    && oldItem.ad.price == newItem.ad.price
                    && oldItem.ad.advertiser == newItem.ad.advertiser
                    && oldItem.ad.store == newItem.ad.store
        }

        if (oldItem is SongsHeaderItem && newItem is SongsHeaderItem) {
            return true
        }

        if (oldItem is LoadingItem && newItem is LoadingItem) {
            return true
        }
        return false
    }
}