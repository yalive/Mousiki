package com.cas.musicplayer.ui.library.adapters

import com.cas.musicplayer.delegateadapter.BaseDelegationAdapter
import com.cas.musicplayer.ui.library.delegates.*
import com.cas.musicplayer.ui.library.model.LibraryHeaderItem
import com.cas.musicplayer.ui.library.model.LibraryItem
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.ui.library.LibraryViewModel
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
        LibraryRecentTracksAdapterDelegate { track, tracks ->
            viewModel.onClickRecentTrack(track, tracks)
        },
        LibraryHeavyTracksAdapterDelegate { track, tracks ->
            viewModel.onClickHeavyTrack(track, tracks)
        },
        LibraryFavouriteTracksAdapterDelegate { track, tracks ->
            viewModel.onClickFavouriteTrack(track, tracks)
        },
        LibraryHeaderAdapterDelegate(),
        LibraryPlaylistsDelegate(viewModel),
        LibrarySettingDelegate()
    )
) {
    init {
        dataItems = mutableListOf(
            LibraryHeaderItem.PlaylistsHeader,
            LibraryItem.Playlists(listOf()),
            LibraryItem.Recent(emptyList()),
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
            dataItems.add(LibraryItem.Heavy(songs))
            notifyItemRangeInserted(oldSize, 2)
        }
    }

    fun updatePlaylists(playlists: List<LibraryPlaylistItem>) {
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

object LibrarySettingItem : DisplayableItem