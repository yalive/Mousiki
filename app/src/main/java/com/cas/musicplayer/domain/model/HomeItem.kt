package com.cas.musicplayer.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import kotlinx.android.parcel.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
sealed class HomeItem(val type: Int) : DisplayableItem {
    object RecentItem : HomeItem(HomeAdapter.TYPE_FEATURED)
    object PopularsItem : HomeItem(HomeAdapter.TYPE_NEW_RELEASE)
    object ChartItem : HomeItem(HomeAdapter.TYPE_CHART)
    object ArtistItem : HomeItem(HomeAdapter.TYPE_ARTIST)
    object GenreItem : HomeItem(HomeAdapter.TYPE_GENRE)
}

sealed class HeaderItem(val title: String) : HomeItem(HomeAdapter.TYPE_HEADER) {
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