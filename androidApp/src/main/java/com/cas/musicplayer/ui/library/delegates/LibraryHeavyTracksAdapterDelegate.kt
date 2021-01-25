package com.cas.musicplayer.ui.library.delegates

import com.cas.common.resource.Resource
import com.mousiki.shared.domain.models.DisplayableItem
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.library.model.LibraryItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryHeavyTracksAdapterDelegate(
    onVideoSelected: (MusicTrack, List<MusicTrack>) -> Unit
) : HorizontalListSongsAdapterDelegate(onVideoSelected) {

    override val showRetryButton: Boolean = false

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryItem.Heavy
    }

    override fun songsFromItem(item: DisplayableItem): Resource<List<DisplayedVideoItem>> {
        return Resource.Success((item as LibraryItem.Heavy).tracks)
    }

    override fun getEmptyMessage(): Int {
        return R.string.library_empty_heavy_list
    }

    override fun getHeaderTitle(items: List<DisplayableItem>, position: Int): String {
        return MusicApp.get().getString(R.string.library_heavy)
    }
}