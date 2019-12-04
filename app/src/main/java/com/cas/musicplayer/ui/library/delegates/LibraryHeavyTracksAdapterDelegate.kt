package com.cas.musicplayer.ui.library.delegates

import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.delegates.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.library.model.LibraryItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryHeavyTracksAdapterDelegate(
    onVideoSelected: () -> Unit
) : HorizontalListSongsAdapterDelegate(onVideoSelected) {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryItem.Heavy
    }

    override fun songsFromItem(item: DisplayableItem): Resource<List<DisplayedVideoItem>> {
        return Resource.Success((item as LibraryItem.Heavy).tracks)
    }

    override fun getEmptyMessage(): Int {
        return R.string.library_empty_heavy_list
    }
}