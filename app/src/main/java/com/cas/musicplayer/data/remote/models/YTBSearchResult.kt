package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
@Keep
@Serializable
data class YTBSearchResult(
    val id: YTBSearchResultId? = null,
    val snippet: YTBSearchResultSnippet? = null
)

@Keep
@Serializable
data class YTBSearchResultId(
    val kind: String? = null,
    val videoId: String? = null,
    val channelId: String? = null,
    val playlistId: String? = null
)

@Keep
@Serializable
data class YTBSearchResultSnippet(
    val title: String? = null,
    var thumbnails: YTThumbnails? = null
)