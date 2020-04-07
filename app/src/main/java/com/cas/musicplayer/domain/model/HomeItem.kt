package com.cas.musicplayer.domain.model

import android.os.Parcelable
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlinx.android.parcel.Parcelize

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
}

sealed class HeaderItem(val title: String, val showMore: Boolean = true) : HomeItem() {
    data class PopularsHeader(var loading: Boolean = false) : HeaderItem("New Releases")
    object ChartsHeader : HeaderItem("Top charts", false)
    object ArtistsHeader : HeaderItem("Artists")
    object GenresHeader : HeaderItem("Genres")
}

data class ChartModel(
    val title: String,
    val playlistId: String,
    var featuredImageUrl: String
)

@Parcelize
data class GenreMusic(
    val title: String,
    val img: Int,
    val channelId: String,
    val topTracksPlaylist: String
) : Parcelable, DisplayableItem