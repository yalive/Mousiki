package com.cas.musicplayer.ui.library.adapters

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.library.delegates.LibraryFavouriteTracksAdapterDelegate
import com.cas.musicplayer.ui.library.delegates.LibraryHeaderAdapterDelegate
import com.cas.musicplayer.ui.library.delegates.LibraryHeavyTracksAdapterDelegate
import com.cas.musicplayer.ui.library.delegates.LibraryRecentTracksAdapterDelegate
import com.cas.musicplayer.ui.library.model.LibraryItem
import kotlin.reflect.KClass

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryAdapter(
    onVideoSelected: (MusicTrack) -> Unit
) : BaseDelegationAdapter(
    listOf(
        LibraryRecentTracksAdapterDelegate(onVideoSelected),
        LibraryHeavyTracksAdapterDelegate(onVideoSelected),
        LibraryFavouriteTracksAdapterDelegate(onVideoSelected),
        LibraryHeaderAdapterDelegate()
    )
) {
    init {
        dataItems = mutableListOf(
            LibraryItem.Header("Recent"),
            LibraryItem.Recent(emptyList()),
            LibraryItem.Header("Favourites"),
            LibraryItem.Favourite(emptyList())
        )
    }

    fun updateRecent(songs: List<DisplayedVideoItem>) {
        val index = indexOfItem(LibraryItem.Recent::class)
        if (index != -1) {
            updateItemAtIndex(index, LibraryItem.Recent(songs))
        }
    }

    fun updateHeavy(songs: List<DisplayedVideoItem>) {
        val index = indexOfItem(LibraryItem.Heavy::class)
        if (index != -1) {
            updateItemAtIndex(index, LibraryItem.Heavy(songs))
        } else if (songs.isNotEmpty()) {
            val oldSize = dataItems.size
            dataItems.add(LibraryItem.Header("Heavy songs"))
            dataItems.add(LibraryItem.Heavy(songs))
            notifyItemRangeInserted(oldSize, 2)
        }
    }

    fun updateFavourite(songs: List<DisplayedVideoItem>) {
        val index = indexOfItem(LibraryItem.Favourite::class)
        if (index != -1) {
            updateItemAtIndex(index, LibraryItem.Favourite(songs))
        }
    }

    private fun updateItemAtIndex(index: Int, item: DisplayableItem) {
        dataItems[index] = item
        notifyItemChanged(index)
    }

    private fun indexOfItem(homeItem: KClass<*>): Int {
        dataItems.forEachIndexed { index, item ->
            if (item::class == homeItem) return index
        }
        return -1
    }
}