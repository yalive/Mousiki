package com.mousiki.shared.ui.home.model

import com.mousiki.shared.ads.NativeAdItem
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.CompactPlaylist
import com.mousiki.shared.data.models.SimplePlaylist
import com.mousiki.shared.domain.models.ChartModel
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.utils.Strings

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

    data class FBNativeAd(val adItem: NativeAdItem) : HomeItem()
}

sealed class HeaderItem(val showMore: Boolean = true) : HomeItem() {
    data class PopularsHeader(var loading: Boolean = false) : HeaderItem()
    object ChartsHeader : HeaderItem(false)
    object ArtistsHeader : HeaderItem()
    object GenresHeader : HeaderItem()
}

fun HomeItem.title(strings: Strings): String {
    return when (this) {
        is HeaderItem.PopularsHeader -> strings.titleNewRelease
        is HeaderItem.ChartsHeader -> strings.titleTopCharts
        is HeaderItem.ArtistsHeader -> strings.artists
        is HeaderItem.GenresHeader -> strings.genres
        is HomeItem.PopularsItem -> strings.titleNewRelease
        is HomeItem.ChartItem -> strings.titleTopCharts
        is HomeItem.ArtistItem -> strings.artists
        is HomeItem.GenreItem -> strings.genres
        is HomeItem.CompactPlaylists -> title
        is HomeItem.SimplePlaylists -> title
        is HomeItem.VideoList -> title
        is HomeItem.FBNativeAd -> ""
    }
}