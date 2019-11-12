package com.cas.musicplayer.ui.home.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.cas.musicplayer.ui.home.ui.adapters.HomeAdapter
import kotlinx.android.parcel.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
sealed class HomeItem {
    abstract val type: Int
}

object FeaturedItem : HomeItem() {
    override val type = HomeAdapter.TYPE_FEATURED
}

object NewReleaseItem : HomeItem() {
    override val type = HomeAdapter.TYPE_NEW_RELEASE
}

object ChartItem : HomeItem() {
    override val type = HomeAdapter.TYPE_CHART
}

data class HeaderItem(val title: String) : HomeItem() {
    override val type = HomeAdapter.TYPE_HEADER
}

object ArtistItem : HomeItem() {
    override val type = HomeAdapter.TYPE_ARTIST
}

object GenreItem : HomeItem() {
    override val type = HomeAdapter.TYPE_GENRE
}

@Parcelize
data class ChartModel(
    val title: String,
    @DrawableRes val image: Int,
    val playlistId: String
) : Parcelable {
    var track1 = ""
    var track2 = ""
    var track3 = ""
}

@Parcelize
data class GenreMusic(
    val title: String,
    val img: Int,
    val channelId: String,
    val topTracksPlaylist: String
) : Parcelable