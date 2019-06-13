package com.secureappinc.musicplayer.data.models

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
    @SerializedName("items")
    val items: List<T>?
)

typealias Videos = YoutubeResource<YTBVideo>
typealias PlaylistItems = YoutubeResource<YTBPlaylistItem>
typealias Playlists = YoutubeResource<YTBPlaylist>
typealias Channels = YoutubeResource<YTBChannel>
typealias SearchResults = YoutubeResource<YTBSearchResult>