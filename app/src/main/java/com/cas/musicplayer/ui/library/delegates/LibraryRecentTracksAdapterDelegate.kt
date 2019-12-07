package com.cas.musicplayer.ui.library.delegates

import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.library.model.LibraryItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryRecentTracksAdapterDelegate(
    onVideoSelected: (MusicTrack) -> Unit
) : HorizontalListSongsAdapterDelegate(onVideoSelected) {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryItem.Recent
    }

    override fun songsFromItem(item: DisplayableItem): Resource<List<DisplayedVideoItem>> {
        return Resource.Success((item as LibraryItem.Recent).tracks)
    }

    override fun getEmptyMessage(): Int {
        return R.string.library_empty_recent_list
    }
}