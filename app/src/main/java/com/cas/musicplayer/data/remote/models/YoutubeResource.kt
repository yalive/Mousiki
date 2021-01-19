package com.cas.musicplayer.data.remote.models

import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
@Serializable
open class YoutubeResource<T>(
    val kind: String? = null,
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: YoutubePageInfo? = null,
    val items: List<T>? = null
)

@Serializable
data class YoutubePageInfo(
    val totalResults: Int? = null,
    val resultsPerPage: Int? = null
)

typealias Videos = YoutubeResource<YTBVideo>
typealias PlaylistItems = YoutubeResource<YTBPlaylistItem>
typealias Playlists = YoutubeResource<YTBPlaylist>
typealias Channels = YoutubeResource<YTBChannel>
typealias SearchResults = YoutubeResource<YTBSearchResult>