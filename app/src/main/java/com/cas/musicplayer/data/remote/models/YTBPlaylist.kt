package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
@Keep
@kotlinx.serialization.Serializable
data class YTBPlaylist(
    val id: String? = null,
    val snippet: YTBPlaylistSnippet? = null,
    val contentDetails: YTBPlaylistContentDetail? = null
)

@Keep
@kotlinx.serialization.Serializable
data class YTBPlaylistSnippet(
    val title: String? = null,
    var thumbnails: YTThumbnails? = null,
    val channelTitle: String? = null
)

@Keep
@Serializable
data class YTBPlaylistContentDetail(
    val itemCount: Int? = null
)
