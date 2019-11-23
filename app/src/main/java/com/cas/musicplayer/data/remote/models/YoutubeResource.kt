package com.cas.musicplayer.data.remote.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
open class YoutubeResource<T>(
    @Expose
    @SerializedName("kind")
    val kind: String?,
    @Expose
    @SerializedName("nextPageToken")
    val nextPageToken: String?,
    @Expose
    @SerializedName("prevPageToken")
    val prevPageToken: String?,
    @Expose
    @SerializedName("pageInfo")
    val pageInfo: YoutubePageInfo?,
    @Expose
    @SerializedName("items")
    val items: List<T>?
)

data class YoutubePageInfo(
    @Expose
    @SerializedName("totalResults")
    val totalResults: Int?,
    @Expose
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int?
)
typealias Videos = YoutubeResource<YTBVideo>
typealias PlaylistItems = YoutubeResource<YTBPlaylistItem>
typealias Playlists = YoutubeResource<YTBPlaylist>
typealias Channels = YoutubeResource<YTBChannel>
typealias SearchResults = YoutubeResource<YTBSearchResult>