package com.cas.musicplayer.ui.popular

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsItem
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LoadingItem

class SongItemDiffUtil : DiffUtil.ItemCallback<DisplayableItem>() {

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (oldItem !is DisplayedVideoItem || newItem !is DisplayedVideoItem) {
            return super.getChangePayload(oldItem, newItem)
        }
        if (oldItem.track.id != newItem.track.id) return super.getChangePayload(oldItem, newItem)

        if (oldItem.isCurrent != newItem.isCurrent || oldItem.isPlaying != newItem.isPlaying) {
            return bundleOf(
                "is_current" to newItem.isCurrent,
                "is_playing" to newItem.isPlaying
            )
        }
        return super.getChangePayload(oldItem, newItem)
    }

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is DisplayedVideoItem && newItem is DisplayedVideoItem) {
            return oldItem.track.id == newItem.track.id
        }

        if (oldItem is AdsItem && newItem is AdsItem) {
            return oldItem.ad.headline == newItem.ad.headline
                    && oldItem.ad.body == newItem.ad.body
                    && oldItem.ad.callToAction == newItem.ad.callToAction
        }

        if (oldItem is SongsHeaderItem && newItem is SongsHeaderItem) {
            return true
        }

        if (oldItem is HeaderSongsActionsItem && newItem is HeaderSongsActionsItem) {
            return true
        }

        if (oldItem is LoadingItem && newItem is LoadingItem) {
            return true
        }
        return false
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is DisplayedVideoItem && newItem is DisplayedVideoItem) {
            return oldItem as DisplayedVideoItem == newItem as DisplayedVideoItem
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