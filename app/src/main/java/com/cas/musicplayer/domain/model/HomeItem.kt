package com.cas.musicplayer.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
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
    object RecentItem : HomeItem()
    data class PopularsItem(val songs: List<DisplayedVideoItem>) : HomeItem()
    data class ChartItem(val charts: List<ChartModel>) : HomeItem()
    data class ArtistItem(val artists: List<Artist>) : HomeItem()
    data class GenreItem(val genres: List<GenreMusic>) : HomeItem()
}

sealed class HeaderItem(val title: String) : HomeItem() {
    object RecentHeader : HeaderItem("Recent")
    object PopularsHeader : HeaderItem("New Releases")
    object ChartsHeader : HeaderItem("Top charts")
    object ArtistsHeader : HeaderItem("Artists")
    object GenresHeader : HeaderItem("Genres")
}

@Parcelize
data class ChartModel(
    val title: String,
    @DrawableRes val image: Int,
    val playlistId: String,
    val track1: String = "",
    val track2: String = "",
    val track3: String = ""
) : Parcelable

@Parcelize
data class GenreMusic(
    val title: String,
    val img: Int,
    val channelId: String,
    val topTracksPlaylist: String
) : Parcelable