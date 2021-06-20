package com.cas.musicplayer.ui.home.delegates

import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.ui.resource.Resource

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class VideoListAdapterDelegate(
    onVideoSelected: (Track, List<Track>) -> Unit
) : HorizontalListSongsAdapterDelegate(onVideoSelected) {

    override val showRetryButton: Boolean = false

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.VideoList
    }

    override fun songsFromItem(item: DisplayableItem): Resource<List<DisplayedVideoItem>> {
        return Resource.Success((item as HomeItem.VideoList).items)
    }

    override fun getHeaderTitle(items: List<DisplayableItem>, position: Int): String {
        val videoListsItem = items[position] as HomeItem.VideoList
        return videoListsItem.title
    }
}