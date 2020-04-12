package com.cas.musicplayer.ui.library.model

import androidx.annotation.StringRes
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
sealed class LibraryItem : DisplayableItem {
    data class Recent(val tracks: List<DisplayedVideoItem>) : LibraryItem()
    data class Favourite(val tracks: List<DisplayedVideoItem>) : LibraryItem()
    data class Heavy(val tracks: List<DisplayedVideoItem>) : LibraryItem()
    open class Playlists(val items: List<LibraryPlaylistItem>) : LibraryItem()
}

sealed class LibraryPlaylistItem : DisplayableItem {
    data class CustomPlaylist(val item: Playlist) : LibraryPlaylistItem()
    object NewPlaylist : LibraryPlaylistItem()
}

sealed class LibraryHeaderItem(@StringRes val title: Int, val showMore: Boolean = true) :
    LibraryItem() {
    object RecentHeader : LibraryHeaderItem(R.string.library_recent, false)
    object PlaylistsHeader : LibraryHeaderItem(R.string.title_playlist, false)
    data class FavouriteHeader(val withMore: Boolean = false) :
        LibraryHeaderItem(R.string.library_favourites, withMore)

    object HeavyHeader : LibraryHeaderItem(R.string.library_heavy_songs, false)
}