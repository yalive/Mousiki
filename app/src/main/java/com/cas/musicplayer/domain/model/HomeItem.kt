package com.cas.musicplayer.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
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
    // TODO replace New Releases by the id > R.String.title_new_release
    data class PopularsHeader(var loading: Boolean = false) : HeaderItem("New Releases")
    // TODO replace Top charts by the id > R.String.title_top_charts
    object ChartsHeader : HeaderItem("Top charts", false)
    // TODO replace Artists by the id > R.String.artists
    object ArtistsHeader : HeaderItem("Artists")
    // TODO replace Genres by the id > R.String.genres
    object GenresHeader : HeaderItem("Genres")
}

data class ChartModel(
    val title: String,
    val playlistId: String,
    @DrawableRes val featuredImageRes: Int
)

@Parcelize
data class GenreMusic(
    val title: String,
    val img: Int,
    val channelId: String,
    val topTracksPlaylist: String
) : Parcelable, DisplayableItem