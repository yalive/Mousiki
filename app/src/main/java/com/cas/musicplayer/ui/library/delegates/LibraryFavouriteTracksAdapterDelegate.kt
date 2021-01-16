package com.cas.musicplayer.ui.library.delegates

import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.MusicApp
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
class LibraryFavouriteTracksAdapterDelegate(
    onVideoSelected: (MusicTrack) -> Unit
) : HorizontalListSongsAdapterDelegate(onVideoSelected) {

    override val showRetryButton: Boolean = false

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryItem.Favourite
    }

    override fun songsFromItem(item: DisplayableItem): Resource<List<DisplayedVideoItem>> {
        return Resource.Success((item as LibraryItem.Favourite).tracks)
    }

    override fun getEmptyMessage(): Int {
        return R.string.library_empty_favourite_list
    }

    override fun getHeaderTitle(items: List<DisplayableItem>, position: Int): String {
        return MusicApp.get().getString(R.string.library_favourites)
    }
}