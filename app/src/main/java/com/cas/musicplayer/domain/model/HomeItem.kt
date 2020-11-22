package com.cas.musicplayer.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.models.mousiki.CompactPlaylist
import com.cas.musicplayer.data.remote.models.mousiki.SimplePlaylist
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
    data class CompactPlaylists(
        val title: String, val playlists: List<CompactPlaylist>
    ) : HomeItem()

    data class SimplePlaylists(
        val title: String, val playlists: List<SimplePlaylist>
    ) : HomeItem()

    data class VideoLists(
        val title: String, val tracks: List<MusicTrack>
    ) : HomeItem()
}

sealed class HeaderItem(@StringRes val title: Int, val showMore: Boolean = true) : HomeItem() {
    data class PopularsHeader(var loading: Boolean = false) : HeaderItem(R.string.title_new_release)
    object ChartsHeader : HeaderItem(R.string.title_top_charts, false)
    object ArtistsHeader : HeaderItem(R.string.artists)
    object GenresHeader : HeaderItem(R.string.genres)
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
    val topTracksPlaylist: String,
    val isMood: Boolean,
    val backgroundColor: String
) : Parcelable, DisplayableItem