package com.cas.musicplayer.ui.library.model

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.utils.Strings

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

sealed class LibraryHeaderItem(val showMore: Boolean = true) : LibraryItem() {
    object RecentHeader : LibraryHeaderItem(false)
    object PlaylistsHeader : LibraryHeaderItem(false)
    data class FavouriteHeader(val withMore: Boolean = false) : LibraryHeaderItem(withMore)
    object HeavyHeader : LibraryHeaderItem(false)
}

fun LibraryItem.title(strings: Strings): String {
    return when (this) {
        LibraryHeaderItem.RecentHeader -> strings.libraryRecent
        LibraryHeaderItem.PlaylistsHeader -> strings.libraryTitlePlaylist
        is LibraryHeaderItem.FavouriteHeader -> strings.libraryFavourites
        LibraryHeaderItem.HeavyHeader -> strings.libraryHeavySongs
        is LibraryItem.Recent -> strings.libraryRecent
        is LibraryItem.Favourite -> strings.libraryFavourites
        is LibraryItem.Heavy -> strings.libraryHeavySongs
        is LibraryItem.Playlists -> strings.libraryTitlePlaylist
    }
}