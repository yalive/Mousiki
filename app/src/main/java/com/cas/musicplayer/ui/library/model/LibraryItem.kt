package com.cas.musicplayer.ui.library.model

import com.cas.delegatedadapter.DisplayableItem
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
}

sealed class LibraryHeaderItem(val title: String, val showMore: Boolean = true) : LibraryItem() {
    object RecentHeader : LibraryHeaderItem("Recent", false)
    object FavouriteHeader : LibraryHeaderItem("Favourites")
    object HeavyHeader : LibraryHeaderItem("Heavy songs", false)
}