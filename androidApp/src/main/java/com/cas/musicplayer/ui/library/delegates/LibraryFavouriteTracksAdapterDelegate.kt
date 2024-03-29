package com.cas.musicplayer.ui.library.delegates

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.songs.NewHorizontalSongsAdapterDelegate
import com.cas.musicplayer.ui.library.model.LibraryItem
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.resource.Resource

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryFavouriteTracksAdapterDelegate(
    onVideoSelected: (Track, List<Track>) -> Unit
) : NewHorizontalSongsAdapterDelegate(onVideoSelected) {

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