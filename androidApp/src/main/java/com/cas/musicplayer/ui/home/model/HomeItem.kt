package com.cas.musicplayer.ui.home.model

import androidx.annotation.StringRes
import com.cas.common.resource.Resource
import com.cas.musicplayer.R
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.CompactPlaylist
import com.mousiki.shared.data.models.SimplePlaylist
import com.mousiki.shared.domain.models.ChartModel
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.GenreMusic

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
sealed class HomeItem : DisplayableItem {
    data class PopularsItem(val resource: Resource<List<DisplayedVideoItem>>) : HomeItem()
    data class ChartItem(val charts: List<ChartModel>) : HomeItem()
    data class ArtistItem(val artists: List<Artist>) : HomeItem()
    data class GenreItem(val genres: List<GenreMusic>) : HomeItem()
    data class CompactPlaylists(
        val title: String, val playlists: List<CompactPlaylist>
    ) : HomeItem()

    data class SimplePlaylists(
        val title: String, val playlists: List<SimplePlaylist>
    ) : HomeItem()

    data class VideoList(
        val title: String, val items: List<DisplayedVideoItem>
    ) : HomeItem()
}

sealed class HeaderItem(@StringRes val title: Int, val showMore: Boolean = true) : HomeItem() {
    data class PopularsHeader(var loading: Boolean = false) : HeaderItem(R.string.title_new_release)
    object ChartsHeader : HeaderItem(R.string.title_top_charts, false)
    object ArtistsHeader : HeaderItem(R.string.artists)
    object GenresHeader : HeaderItem(R.string.genres)
}