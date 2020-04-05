package com.cas.musicplayer.ui.library.adapters

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.library.delegates.*
import com.cas.musicplayer.ui.library.model.LibraryHeaderItem
import com.cas.musicplayer.ui.library.model.LibraryItem
import kotlin.reflect.KClass

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryAdapter(
    private val viewModel: LibraryViewModel
) : BaseDelegationAdapter(
    listOf(
        LibraryRecentTracksAdapterDelegate {
            viewModel.onClickRecentTrack(it)
        },
        LibraryHeavyTracksAdapterDelegate {
            viewModel.onClickHeavyTrack(it)
        },
        LibraryFavouriteTracksAdapterDelegate {
            viewModel.onClickFavouriteTrack(it)
        },
        LibraryHeaderAdapterDelegate(),
        LibraryPlaylistsDelegate(viewModel)
    )
) {
    init {
        dataItems = mutableListOf(
            LibraryHeaderItem.RecentHeader,
            LibraryItem.Recent(emptyList()),
            LibraryHeaderItem.FavouriteHeader(),
            LibraryItem.Favourite(emptyList()),
            LibraryHeaderItem.PlaylistsHeader,
            LibraryItem.Playlists(emptyList())
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
            dataItems.add(LibraryHeaderItem.HeavyHeader)
            dataItems.add(LibraryItem.Heavy(songs))
            notifyItemRangeInserted(oldSize, 2)
        }
    }

    fun updatePlaylists(playlists: List<Playlist>) {
        val index = indexOfItem(LibraryItem.Playlists::class)
        if (index != -1) {
            updateItemAtIndex(index, LibraryItem.Playlists(playlists))
        }
    }

    fun updateFavourite(songs: List<DisplayedVideoItem>) {
        val index = indexOfItem(LibraryItem.Favourite::class)
        if (index != -1) {
            updateItemAtIndex(index, LibraryItem.Favourite(songs))
        }

        // Show more
        if (songs.size > 5) {
            val indexHeader = indexOfItem(LibraryHeaderItem.FavouriteHeader::class)
            if (indexHeader != -1) {
                updateItemAtIndex(indexHeader, LibraryHeaderItem.FavouriteHeader(withMore = true))
            }
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